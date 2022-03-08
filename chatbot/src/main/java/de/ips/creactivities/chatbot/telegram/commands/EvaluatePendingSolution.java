package de.ips.creactivities.chatbot.telegram.commands;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.constants.IProcessKeys;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.process.CamundaService;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.EvaluationHandling;
import de.ips.creactivities.chatbot.telegram.MessageSender;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.property.ManagementProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class EvaluatePendingSolution implements ICommand {

    private final MessageSender sender;
    private final SolutionRepository solutionRepo;
    private final ChatbotProperties properties;
    private final Random random = new Random(System.currentTimeMillis());
    private final EvaluationHandling evaluationHandling;
    private final ICmsService cmsService;
    private final CamundaService camundaService;
    private final UserRepository userRepository;
    private final I18nService i18n;

    @Override
    public String getCommand() {
        return "judge";
    }

    @Override
    public String getDescription() {
        return "Judge a solution.";
    }

    @Override
    @Transactional
    public void execute(Message message) throws TelegramApiException, IOException {

        Optional<UserEntity> user = userRepository.findById(message.getFrom().getId() + "");
        if (!CommandHelper.checkUserAndConsent(user, sender, message, i18n)) {
            return;
        }

        List<SolutionEntity> evaluatableSolutions = new LinkedList<>();
        solutionRepo.findAll().forEach(sol -> {
            if ((!"0".equals(sol.getUser().getId())) &&
                    (sol.getEvaluations() == null || sol.getEvaluations().size() < properties.getNumberOfRequiredEvaluations()) && !sol.isBlocked()) {
                evaluatableSolutions.add(sol);
            }
        });
        if (evaluatableSolutions.size() > 0) {
            SolutionEntity solution = evaluatableSolutions.get(evaluatableSolutions.size() > 1 ? random.nextInt(evaluatableSolutions.size() - 1) : 0);

            String lang = solution.getUser().getLanguageId() != null ? solution.getUser().getLanguageId() : "de";
            Optional<Challenge> challenge = cmsService.findChallengeById(lang, solution.getChallenge().getId());
            String callbackMessage = "cmd_" + getCommand() + "_" + solution.getId();
            sender.sendSolutionWithMarkup(message.getChatId() + "", callbackMessage, solution, challenge);
        } else {
            //noinspection OptionalGetWithoutIsPresent isPresent implicitly checked in checkUserAndConsent
            String lang = user.get().getLanguageId();
            sender.sendTextMessage(message.getChatId() + "", null, i18n.localize(lang, I18n.COMMAND_NOSOLUTIONSTOEVALUATE));
        }
    }

    @Override
    @Transactional
    public void executeQuery(CallbackQuery query) throws TelegramApiException, IOException {
        // 0 - "cmd"
        // 1 - "evaluatependingsolution"
        // 2 - <solution id>
        // 3 - score
        String[] queryParam = query.getData().split("_");
        if (queryParam.length != 4) {
            return;
        }
        Optional<SolutionEntity> solution = solutionRepo.findById(Long.parseLong(queryParam[2]));
        if (solution.isPresent()) {
            String score = queryParam[3];
            if ("0".equals(score)) {
                RuntimeService runtimeService = camundaService.getRuntimeService();
                Map<String, Object> variables = new HashMap<>();
                variables.put(IProcessVariables.USER_ID, solution.get().getUser().getId());
                variables.put(IProcessVariables.REPORTED_SOLUTION_ID, solution.get().getId());
                runtimeService.startProcessInstanceByKey(IProcessKeys.adminEvaluationProcess, variables);
            } else {
                evaluationHandling.commitEvaluationToSolution(score, "0", solution);
            }

        }
    }
}
