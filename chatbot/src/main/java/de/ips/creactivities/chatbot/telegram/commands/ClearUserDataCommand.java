package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.dm.EvaluationEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.dm.UserState;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.process.CamundaService;
import de.ips.creactivities.chatbot.repo.EvaluationRepository;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ClearUserDataCommand implements ICommand {

    private final UserRepository userRepository;
    private final SolutionRepository solutionRepository;
    private final EvaluationRepository evaluationRepository;
    private final I18nService i18n;
    private final Map<String, String> requestingUsers = new ConcurrentHashMap<>();
    private final CamundaService camundaService;
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    @Override
    public String getCommand() {
        return "deleteme";
    }

    @Override
    public String getDescription() {
        return "Delete your account.";
    }

    @Override
    public boolean isAdminCommand() {
        return false;
    }

    @Override
    public void execute(Message message) throws TelegramApiException, IOException {


        if (requestingUsers.containsKey(message.getFrom().getId() + "")) {

            Optional<UserEntity> user = userRepository.findById(message.getFrom().getId() + "");
            String lang = message.getFrom().getLanguageCode();
            if (user.isPresent()) {
                lang = user.get().getLanguageId();
                deleteUser(user.get());
                requestingUsers.remove(message.getFrom().getId() + "");
            }

            SendMessage m = SendMessage.builder().chatId(message.getChatId() + "").text(i18n.localize(lang, I18n.COMMAND_DELETEME_CONFIRMATION)).build();
            camundaService.deleteProcessInstances(String.valueOf(message.getChatId()));
            CreactivitiesBot.getInstance().execute(m);
        } else {
            Optional<UserEntity> user = userRepository.findById(message.getFrom().getId() + "");
            String lang = message.getFrom().getLanguageCode();
            if (user.isPresent()) {
                lang = user.get().getLanguageId();
            }
            requestingUsers.put(message.getFrom().getId() + "", "");
            SendMessage m = SendMessage.builder().chatId(message.getChatId() + "").text(i18n.localize(lang, I18n.COMMAND_DELETEME_CONF_REQUEST)).build();
            CreactivitiesBot.getInstance().execute(m);
            ses.schedule(() -> removeKey(message.getFrom().getId() + ""), 1, TimeUnit.MINUTES);
        }
    }

    private void removeKey(String id) {
        requestingUsers.remove(id);
    }


    /**
     * Deletes a user. ATTENTION: Is synchronized to prevent multiple creation of the Dummy User!
     *
     * @param ue the user to delete.
     */
    synchronized void deleteUser(UserEntity ue) {
        List<SolutionEntity> solutions = solutionRepository.findAllByUser(ue);
        Optional<UserEntity> dummyUser = userRepository.findById("0");
        if (dummyUser.isEmpty()) {
            // we need to add the dummy user so we can hang solutions there (solutions without a user cause issues in other parts of the code!!!)
            UserEntity dummyUserEntity = new UserEntity("0", true, UserState.IDLE, null, "0", "de", null, null, false, null, null);
            userRepository.save(dummyUserEntity);
            dummyUser = Optional.of(dummyUserEntity);
        }

        /*for (SolutionEntity s : solutions) {
            s.setUser(dummyUser.get());
        }*/
        solutionRepository.deleteAll(solutions);
        //solutionRepository.saveAll(solutions);

        List<EvaluationEntity> evaluations = evaluationRepository.findAllByUser(ue);
        for (EvaluationEntity e : evaluations) {
            e.setUser(dummyUser.get());
        }
        evaluationRepository.saveAll(evaluations);
        userRepository.deleteById(ue.getId());
    }
}
