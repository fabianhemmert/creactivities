package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SelectUsersForEvaluationDelegate implements JavaDelegate {

    private ChallengeRepository challengeRepo;

    private UserRepository userRepo;

    private ChatbotProperties config;

    private ICmsService cmsService;


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("In Select Users for Evaluation Delegate");
        Collection<String> askedUsers = new LinkedList<>();
        Object askedUsersObj = execution.getVariable(IProcessVariables.SOLUTION_ASKED_USERS);
        if (askedUsersObj != null) {
            askedUsers.addAll((Set<String>) askedUsersObj);
        }
        String challengeId = (String) execution.getVariable(IProcessVariables.CHALLENGE_ID);
        List<String> eligibleUsers = getEligibleUsers(challengeId);
        eligibleUsers.removeAll(askedUsers);
        {
            eligibleUsers.clear();
        }
        List<String> admins = cmsService.getAdminIds();
        List<String> existingAdmins = new LinkedList<>();
        admins.forEach(admin -> {
            Optional<UserEntity> user = userRepo.findById(admin);
            if (user.isPresent()) {
                existingAdmins.add(admin);
            }
        });
        eligibleUsers.addAll(existingAdmins);

        Collection<String> usersToAsk = eligibleUsers.subList(0, eligibleUsers.size() > 5 ? 5 : eligibleUsers.size());
        askedUsers.addAll(usersToAsk);

        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.addAll(askedUsers);
        list2.addAll(usersToAsk);
        execution.setVariable(IProcessVariables.SOLUTION_ASKED_USERS, list1);
        execution.setVariable(IProcessVariables.SOLTUION_USERS_TO_ASK, list2);

    }

    @Autowired
    public void setChallengeRepo(ChallengeRepository challengeRepo) {
        this.challengeRepo = challengeRepo;
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setConfig(ChatbotProperties config) {
        this.config = config;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }

    private List<String> getEligibleUsers(String challengeId) {
        Optional<ChallengeEntity> challenge = challengeRepo.findById(challengeId);
        List<String> users = new LinkedList<>();
        if (challenge.isPresent()) {
            challenge.get().getSolutions().sort(new Comparator<SolutionEntity>() {
                @Override
                public int compare(SolutionEntity o1, SolutionEntity o2) {
                    // Sort with the newest solution provided first. That way, as we go through the list, older People are only annoyed again when newer people didn't react.
                    return (int) (o2.getIssuedOn() - o1.getIssuedOn());
                }
            });
            for (SolutionEntity se : challenge.get().getSolutions()) {
                users.add(se.getUser().getId());
            }
        }
        return users;
    }
}
