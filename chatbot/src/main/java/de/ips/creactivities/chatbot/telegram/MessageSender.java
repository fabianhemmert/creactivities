package de.ips.creactivities.chatbot.telegram;

import de.ips.creactivities.chatbot.cms.dm.Challenge;
import de.ips.creactivities.chatbot.cms.dm.TemplateSolution;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.i18n.I18n;
import de.ips.creactivities.chatbot.i18n.I18nService;
import de.ips.creactivities.chatbot.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MessageSender {

    private UserRepository userRepo;

    private I18nService i18n;

    public void sendSolutionWithMarkup(String chatId, String userId, String callbackMessage, SolutionEntity solution, Optional<Challenge> challenge)
            throws TelegramApiException, IOException {

        String languageCode = userRepo.findById(userId).get().getLanguageId();

        String question = getQuestion(languageCode, challenge);
        sendSolutionToChat(chatId, solution, null);
        InlineKeyboardMarkup replyMarkup = generateMarkup(languageCode, callbackMessage);
        sendTextMessage(chatId, replyMarkup, question, null);
    }

    public void sendSolutionWithMarkup(String chatId, String callbackMessage, SolutionEntity solution, Optional<Challenge> challenge)
            throws TelegramApiException, IOException {
        sendSolutionWithMarkup(chatId, chatId, callbackMessage, solution, challenge);
    }

    private String getQuestion(String languageCode, Optional<Challenge> challenge) {
        String question = i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SOLUTION);// Fallback question in case it is not set in the challenge
        if (challenge.isPresent()) {

            if (hasQuestion(challenge.get())) {
                question = challenge.get().getEvaluationQuestion();
            }

        }
        return question;
    }

    private InlineKeyboardMarkup generateMarkup(String languageCode, String callbackMessage) {

        List<InlineKeyboardButton> row0 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_FIVE))
                        .callbackData(callbackMessage + "_5").build());

        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_FOUR))
                        .callbackData(callbackMessage + "_4").build(),
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_THREE))
                        .callbackData(callbackMessage + "_3").build());

        List<InlineKeyboardButton> row2 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_TWO))
                        .callbackData(callbackMessage + "_2").build(),
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_ONE))
                        .callbackData(callbackMessage + "_1").build());

        List<InlineKeyboardButton> row3 = List.of(
                InlineKeyboardButton.builder()
                        .text(i18n.localize(languageCode, I18n.MESSAGE_EVALUATION_SCORE_REPORT))
                        .callbackData(callbackMessage + "_0").build());


        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(row0).keyboardRow(row1).keyboardRow(row2).keyboardRow(row3).build();
        return replyMarkup;
    }

    private boolean hasQuestion(Challenge challenge) {
        return challenge != null && challenge.getEvaluationQuestion() != null
                && !challenge.getEvaluationQuestion().isEmpty()
                && !"false".equals(challenge.getEvaluationQuestion());
    }

    /**
     * This method sends a solution to a user. The InlineKeyboardMarkup can be used to ask for evaluation or to allow a mod to flag (and remove) a solution.
     *
     * @param chatId      the chatid to send the solution to
     * @param solution    the solution to send.
     * @param replyMarkup the keyboard to append.
     * @throws TelegramApiException
     */
    public void sendSolutionToChat(String chatId, SolutionEntity solution, InlineKeyboardMarkup replyMarkup) throws TelegramApiException, IOException {

        List<String> values = solution.getSolutionValues();

        if (values != null) {
            log.info("The solution consists of " + values.size() + " elements and will be sent to chat '" + chatId + "'.");

            // Currently we are only supporting one image with one caption
            String caption = null;
            String image = null;
            for (String value : values) {
                if (value.startsWith("__photo__")) {
                    image = value;
                } else {
                    caption = value;
                }
            }

            if (image != null && !image.isEmpty()) {
                sendImage(chatId, replyMarkup, caption, image);
            } else {
                sendTextMessage(chatId, replyMarkup, caption);
            }
        }
    }

    public void sendTextMessage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String text) throws TelegramApiException {
        sendTextMessage(chatId, replyMarkup, text, null);
    }

    public void sendTextMessage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String text, @Nullable Integer replyToId)
            throws TelegramApiException {
        SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(chatId).replyToMessageId(replyToId).text(text);
        if (replyMarkup != null) {
            builder.replyMarkup(replyMarkup);
        }
        SendMessage message = builder.build();

        CreactivitiesBot.getInstance().execute(message);
    }

    public void sendImage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String caption, String image, @Nullable Integer replyToId) throws TelegramApiException, IOException {
        InputFile photo = null;

        if (image.startsWith("__photo__")) {
            photo = new InputFile(image.replace("__photo__", ""));
        } else {
            photo = new InputFile(new URL(image).openStream(), "image.jpg");
        }
        SendPhoto message = SendPhoto.builder().chatId(chatId).caption(caption)
                .photo(photo)
                .replyToMessageId(replyToId)
                .replyMarkup(replyMarkup)
                .build();
        CreactivitiesBot.getInstance().customExecute(message);
    }


    public void sendImage(String chatId, @Nullable InlineKeyboardMarkup replyMarkup, String caption, String image) throws TelegramApiException, IOException {
        sendImage(chatId, replyMarkup, caption, image, null);
    }

    public void sendTemplateSolution(String chatId, Challenge challenge, String callbackMessage) throws TelegramApiException, IOException, InterruptedException {

        String languageCode = userRepo.findById(chatId).get().getLanguageId();

        String question = getQuestion(languageCode, Optional.ofNullable(challenge));
        Thread.sleep(1500);
        TemplateSolution solution = challenge.getTemplateSolutions().get(0);
        String image = solution.getImage();
        String caption = solution.getText();

        if (image != null && !image.isEmpty() && !"false".equals(image)) {
            sendImage(chatId, null, caption, image);
        } else {
            sendTextMessage(chatId, null, caption);
        }

        sendTextMessage(chatId, generateMarkup(languageCode, callbackMessage), question, null);
    }

    public void setAction(String chatId, DecorationAction action) throws TelegramApiException {
        SendChatAction sca = SendChatAction.builder().chatId(chatId).action(action.getAction()).build();
        CreactivitiesBot.getInstance().execute(sca);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setI18nService(I18nService i18nService) {
        this.i18n = i18nService;
    }
}
