package de.ips.creactivities.chatbot.telegram;

import de.ips.creactivities.chatbot.ChatbotProperties;
import de.ips.creactivities.chatbot.cms.CmsService;
import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IMessageEvents;
import de.ips.creactivities.chatbot.constants.IProcessKeys;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.ChallengeEntity;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.dm.UserEntity;
import de.ips.creactivities.chatbot.process.CamundaService;
import de.ips.creactivities.chatbot.repo.ChallengeRepository;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.repo.UserRepository;
import de.ips.creactivities.chatbot.telegram.commands.ICommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreactivitiesBot extends TelegramLongPollingBot {

    private final ChatbotProperties properties;

    private final UserRepository userRepo;

    private final ChallengeRepository challengeRepo;
    private final SolutionRepository solutionRepository;

    private static CreactivitiesBot instance;

    private CamundaService camundaService;

    private CmsService cmsService;

    public static CreactivitiesBot getInstance() {
        return instance;
    }

    public static void setInstance(CreactivitiesBot instance) {
        CreactivitiesBot.instance = instance;
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    @Override
    public String getBotToken() {
        return properties.getApiKey();
    }

    @Autowired
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    public ICmsService getCmsService() {
        return cmsService;
    }

    public UserRepository getUserRepo() {
        return userRepo;
    }

    @Autowired
    public void setCamundaService(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    Map<String, ICommand> commands = new HashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(20, new ThreadFactory() {
        private int i = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread th = new Thread(r);
            th.setName("Telegram Ingest Threadpool Thread " + ++i);
            return th;
        }
    });

    @Autowired
    public void addCommand(List<ICommand> cmds) {
        for (ICommand cmd : cmds) {
            this.commands.put(cmd.getCommand(), cmd);
        }
    }

    @PostConstruct
    private void init() {
        instance = this;
        List<BotCommand> botCommands = new LinkedList<>();
        for (ICommand cmd : commands.values()) {
            botCommands.add(BotCommand.builder().command(cmd.getCommand()).description(cmd.getDescription()).build());
        }
        try {
            execute(SetMyCommands.builder().commands(botCommands).build()); // .command(BotCommand.builder().command("wobinich").description("Zeigt Dir wo Du Dich auf der Weltkarte befindest.").build()).build())
        } catch (TelegramApiException e) {
            log.error("Error executing Telegram API call", e);
        }
        updateChallengesIndex();
        UserEntity user = new UserEntity();
        user.setId("0");
        userRepo.save(user);
    }

    @Scheduled(fixedRate = 60_000)
    public synchronized void updateChallengesIndex() {

        List<String> languageCodes = cmsService.getAvailableLanguageCodes();

        for (String languageCode : languageCodes) {
            List<String> courses = cmsService.getCourseIdentifiers(languageCode);
            for (String course : courses) {
                List<String> levels = cmsService.getLevelsForCourse(languageCode, course);
                for (String level : levels) {
                    List<String> challenges = cmsService.getChallengesForLevel(languageCode, level);
                    for (String challenge : challenges) {
                        Optional<ChallengeEntity> challengeEntity = challengeRepo.findById(challenge);
                        if (challengeEntity.isEmpty()) {
                            ChallengeEntity ce = new ChallengeEntity();
                            ce.setSolutions(new LinkedList<>());
                            ce.setId(challenge);
                            challengeRepo.save(ce);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        executor.submit(() -> {
            try {
                handleUpdate(update);
            } catch (Throwable e) {
                log.error("Exception happened while trying to perform Telegram Message handling!", e);
                if (e instanceof Error) {
                    throw (Error) e;
                }
            }
        });
    }

    private void handleUpdate(Update update) throws TelegramApiException {
        Optional<Message> m = Optional.ofNullable(getMessage(update));
        Optional<CallbackQuery> q = Optional.ofNullable(update.getCallbackQuery());
        m.ifPresent(msg -> {
            if ("private".equals(msg.getChat().getType())) {
                handleMessage(msg);
            } else {
                String expectedId = cmsService.getAdminGroupChatId();
                if (expectedId != null && expectedId.equals(msg.getChatId() + "")) {
                    // there is no situation in which we would actually expect Commands to be triggered here
                    // for certain we do not want to simply call handleMessage as that would start the Story mode.
                } else {
                    SendMessage chatIdReply = SendMessage.builder().chatId(msg.getChatId() + "").text("Chat ID: " + msg.getChatId()).build();
                    try {
                        execute(chatIdReply);
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });

        q.ifPresent(query -> {
            long mId = query.getMessage().getMessageId();
            long cId = query.getMessage().getChatId();
            EditMessageReplyMarkup reply = EditMessageReplyMarkup.builder().chatId(cId + "").messageId((int) mId).replyMarkup(null).build();
            List<List<InlineKeyboardButton>> keyboard = query.getMessage().getReplyMarkup().getKeyboard();
            EditMessageText replyText = null;
            EditMessageCaption replyCaption = null;
            outer:
            for (List<InlineKeyboardButton> keyrow : keyboard) {
                for (InlineKeyboardButton key : keyrow) {
                    if (query.getData().equals(key.getCallbackData())) {
                        Optional<UserEntity> user = userRepo.findById(query.getFrom().getId() + "");
                        String lang;
                        if (user.isPresent()) {
                            lang = user.get().getLanguageId();
                        } else {
                            lang = query.getFrom().getLanguageCode();
                        }
                        String answer = "de".equals(lang) ? "Antwort" : "Answer";

                        if (query.getMessage().hasText()) {
                            replyText = EditMessageText.builder().parseMode("html").messageId((int) mId).chatId(cId + "").text(query.getMessage().getText() + "\n\n<b>" + answer + ": " + key.getText() + "</b>").build();
                        } else if (query.getMessage().hasPhoto()) {
                            String newText;
                            if (query.getMessage().getCaption() == null) {
                                newText = "<b>" + answer + ": " + key.getText() + "</b>";
                            } else {
                                newText = query.getMessage().getCaption() + "\n\n<b>" + answer + ": " + key.getText() + "</b>";
                            }
                            replyCaption = EditMessageCaption.builder().parseMode("html").messageId((int) mId).chatId(cId + "").caption(newText).build();
                        }
                        break outer;
                    }
                }
            }
            try {
                execute(reply);
                if (replyText != null) {
                    execute(replyText);
                }
                if (replyCaption != null) {
                    execute(replyCaption);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            handleQuery(query);
        });
    }

    private void handleMessage(Message msg) {
        try {
            String userId = (msg.getFrom() != null ? msg.getFrom().getId() + "" : "");
            String chatId = msg.getChatId() + "";
            Optional<UserEntity> user = userRepo.findById(userId);
            if(user.isPresent() && user.get().isBlocked()) {
                // Block the user from interacting with the bot.
                return;
            }

            if (msg.hasText()) {
                if (msg.getText() != null && msg.getText().startsWith("/")) {
                    for (Map.Entry<String, ICommand> entry : commands.entrySet()) {
                        if (msg.getText().startsWith("/" + entry.getKey())) {
                            // Check if command is no admin command or - if it is - if userid is in the admins list, otherwise reject!
                            if (!entry.getValue().isAdminCommand() || cmsService.getAdminIds().contains(userId)) {
                                entry.getValue().execute(msg);
                            }
                            // Don't execute this message further.
                            return;
                        }
                    }
                }
            }

            if (user.isEmpty()) {
                // User does not yet exist, add him to the DB before handling Consent.
                handleUserCreation(userId + "", chatId, msg.getFrom().getLanguageCode());
                log.info("New User {} initiated", userId);
                Map<String, Object> variables = Map.of(IProcessVariables.USER_ID, userId, IProcessVariables.CHAT_ID, chatId);
                camundaService.getRuntimeService().startProcessInstanceByKey(IProcessKeys.userFlow, userId, variables);
                return;
            } else {
                if (user.get().getActiveCommand() != null) {
                    // we're in a command, this takes precedence
                    String command = user.get().getActiveCommand();
                    commands.get(command).executeCallback(msg);
                    return;
                } else if (user.get().getActiveChallenge() != null) {
                    ProcessInstance processInstancee = camundaService.findActiveChallengeProcessInstance(userId);
                    if (processInstancee == null) {
                        return; // User has sent us a message, but is currently not in an active challenge
                    }
                    Optional<ChallengeEntity> entity = challengeRepo.findById(user.get().getActiveChallenge());
                    Long solutionId = addSolutionToChallenge(entity, msg, user.get());

                    Map<String, Object> variables = new HashMap<>();
                    variables.put(IProcessVariables.SOLUTION_ID, solutionId);

                    camundaService.handleCamundaEvent(IMessageEvents.SOLUTION_RECEIVED, processInstancee.getProcessInstanceId(), variables);
                    user.get().setActiveChallenge(null); // Setting active challenge to null because trhe user provided a solution
                    userRepo.save(user.get());
                    return;
                }

            }

        } catch (TelegramApiException | IOException e) {
            log.error("Error executing Telegram API call", e);
        }
    }

    public Long addSolutionToChallenge(Optional<ChallengeEntity> entity, Message msg, UserEntity user) {

        if (entity.isPresent()) {

            ChallengeEntity challenge = entity.get();
            // 1. If the user already provided a solution for this challenge, remove it
            List<SolutionEntity> result = solutionRepository.findAllByChallengeAndUser(challenge, user);
            solutionRepository.deleteAll(result);

            // 2. Save the new solution
            Set<String> list = new HashSet<>();
            if (msg.hasText()) {
                list.add(msg.getText());
            } else if (msg.getCaption() != null && !msg.getCaption().isEmpty()) {
                list.add(msg.getCaption());
            }

            if (msg.hasPhoto()) {
                list.add("__photo__" + msg.getPhoto().get(0).getFileId());
            }

            SolutionEntity solution = new SolutionEntity();
            solution.setSolutionValues(list.stream().toList());
            solution.setUser(user);
            solution.setChallenge(challenge);
            solution.setEvaluations(Collections.emptyList());
            solution.setIssuedOn(System.currentTimeMillis());
            solution = solutionRepository.save(solution);
            return solution.getId();
        }
        return null;
    }

    private void handleQuery(CallbackQuery query) {
        try {

            // Syntax of eventData: <process instance id>_<event key>[_<process variable name>_<process variable value>...]
            // Syntax of eventData for commands: cmd_<commandname>
            String eventData = query.getData();
            // if the eventKey contains an under_score use the first section as event key and the second section as actual Id.
            if (eventData == null) {
                // Query was received but Data is null
                return;
            }
            if (eventData.startsWith("cmd_")) {
                // event goes to a command.
                String effectivePayload = eventData.replace("cmd_", "");
                String command = effectivePayload.substring(0, effectivePayload.indexOf('_'));
                commands.get(command).executeQuery(query);
                return;
            }
            String userId = query.getFrom().getId() + "";
            String processInstanceId = userId;
            String eventKey = "";
            processInstanceId = eventData.substring(0, eventData.indexOf('_'));
            eventKey = eventData.substring(eventData.indexOf('_') + 1);

            if (eventKey.contains("_")) {
                String tmp = eventKey;
                eventKey = eventKey.substring(0, eventKey.indexOf('_'));
                eventData = tmp.replace(eventKey, ""); // Two options: 1) "" 2) "_<process variable name>_<process variable value>"
            } else {
                eventData = null;
            }

            UserEntity entity = userRepo.findById(userId).get();
            if(entity.isBlocked()) {
                // Prevent the user from interacting with the bots via queries.
                return;
            }
            if (entity.isConsentGiven()) {
                switch (eventKey) {
                    default:
                        camundaService.handleCamundaEvent(eventKey, processInstanceId, eventData);
                }
            } else {
                if (IMessageEvents.LANGUAGE_SELECTED.equals(eventKey)) {
                    Map<String, Object> variables = camundaService.handleCamundaEvent(eventKey, processInstanceId, eventData);
                    String language = (String) variables.get(IProcessVariables.LANGUAGE);
                    entity.setLanguageId(language);
                    userRepo.save(entity);
                }
                if (IMessageEvents.PRIVACY_POLICY_ACCEPTED.equals(eventKey) || IMessageEvents.TERMS_OF_USE_ACCEPTED.equals(eventKey)) {
                    userRepo.save(entity);
                    camundaService.handleCamundaEvent(eventKey, processInstanceId, eventData);
                } else if (IMessageEvents.COPY_RIGHT_ACCEPTED.equals(eventKey)) {
                    // AFTER WE RECEIVE THIS EVENT, EVERYTHING IS ACCEPTED
                    entity.setConsentGiven(true);
                    userRepo.save(entity);
                    camundaService.handleCamundaEvent(eventKey, processInstanceId, eventData);
                }
            }
        } catch (Exception e) {
            log.error("Error executing Telegram API call", e);
        }
    }

    public byte[] downloadImage(String imageId) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile();
        getFile.setFileId(imageId);
        String filePath = CreactivitiesBot.getInstance().execute(getFile).getFilePath();
        File f = downloadFile(filePath);
        InputStream is = downloadFileAsStream(filePath);
        return is.readAllBytes();
    }

    private void handleUserCreation(String userId, String chatId, String langCode) throws TelegramApiException, IOException {
        if (!"en".equals(langCode) && !"de".equals(langCode)) {
            langCode = "en";
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setConsentGiven(false);
        userEntity.setId(userId);
        userEntity.setChatId(chatId);
        userEntity.setLanguageId(langCode);
        userRepo.save(userEntity);
    }


    private Message getMessage(Update update) throws TelegramApiException {
        Optional<Message> message = Optional.ofNullable(update.getMessage());
        if (message.isEmpty()) {
            message = Optional.ofNullable(update.getChannelPost());
        }
        if (message.isPresent()) {
            Message m = message.get();
            boolean ok = false;
            ok |= m.getText() != null;
            ok |= (m.getPhoto() != null && m.getPhoto().size() > 0);
            if (ok) {
                return message.get();
            } else {
                log.info("Discarding a message that is neither Photo nor Text.");
                SendMessage sm = SendMessage.builder().chatId(message.get().getChatId() + "").text("Es tut mir leid, aber mit dieser Nachricht weiß ich nichts anzufangen. Bitte sende ausschließlich Texte oder Bilder (mit Untertitel)!").build();
                execute(sm);
                return null;
            }
        }
        return Optional.ofNullable(update.getMessage()).orElse(update.getChannelPost());
    }

    //========================================= CUSTOM Methods ==============================

    /**
     * USE THIS METHOD instead of execute to send photos! This allows us to mock it in tests without resolving to Mock Frameworks.
     *
     * @param sendPhoto the photo to send
     * @return the message object
     * @throws TelegramApiException when something within Telegram or the connection to it fails.
     */
    public Message customExecute(SendPhoto sendPhoto) throws TelegramApiException {
        return this.execute(sendPhoto);
    }
}
