package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChallengeLoaderDelegate implements JavaDelegate {

    private UserRepository userRepo;

    private ICmsService cmsService;

    @Autowired
    public void setUserRepository(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("In Challenge Loader Delegate");
        String levelId = (String) execution.getVariable(IProcessVariables.LEVEL_ID);
        String languageCode = userRepo.findById((String) execution.getVariable(IProcessVariables.USER_ID)).get().getLanguageId();
        List<String> challengeIds = cmsService.getChallengesForLevel(languageCode, levelId);

        Object currentChallengeIdObj = execution.getVariable(IProcessVariables.CHALLENGE_ID);
        String currentChallengeId = null;

        // Boolean that signals that we found another challenge after the one we found (so that the process knows it has to come back here after the customer solved the current challenge).
        boolean foundFollower = false;
        // Boolean that is used in the loop to skip all challenges that are not after the one we currently consider as "current".
        boolean afterCurrent = false;
        // Boolean that determines if we found a challenge to give to the user.
        boolean foundNewcurrent = false;

        // If we have no current Challenge this is the first challenge, so everything is "after current".
        if (currentChallengeIdObj == null) {
            afterCurrent = true;
        } else {
            currentChallengeId = (String) currentChallengeIdObj;
        }

        // Find out if there is another challenge after the current one that is active
        for (String id : challengeIds) {
            if (!id.equals(currentChallengeId) && !afterCurrent) {
                continue;
            } else if (!afterCurrent) {
                afterCurrent = true;
                continue;
            } else if (!foundNewcurrent) {
                Challenge challenge = cmsService.findChallengeById(languageCode, id).get();
                if (challenge.getActive()) {
                    currentChallengeId = id;
                    foundNewcurrent = true;
                }
            } else {
                Challenge challenge = cmsService.findChallengeById(languageCode, id).get();
                if (challenge.getActive()) {
                    foundFollower = true;
                    break;
                }
            }
        }

        execution.setVariable(IProcessVariables.CHALLENGE_ID, currentChallengeId);
        // If we found no follower for the challenge we are currently giving the user, the process does not have to revisit but can directly jump out.
        execution.setVariable(IProcessVariables.END_OF_CHALLENGES, !foundFollower);
    }
}
