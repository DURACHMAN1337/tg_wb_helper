package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ftd.telegramhelper.util.message.MessageUtils.getTelegramUserIdFromComment;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final FeedbackService feedbackService;
    private final ResponseHelper responseHelper;
    private final TelegramUserService telegramUserService;

    @Autowired
    public CallbackQueryHandlerImpl(
            FeedbackService feedbackService,
            ResponseHelper responseHelper,
            TelegramUserService telegramUserService
    ) {
        this.feedbackService = feedbackService;
        this.responseHelper = responseHelper;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException, TelegramUserNotExistException, IncorrectFeedbackChannelPostException {
        TelegramUser telegramUser = getSuitableTelegramUser(callbackQuery, callbackQuery.getMessage());
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = String.valueOf(telegramUser.getChatId());
        String callbackData = callbackQuery.getData();

        if (Callback.FIRST.equals(callbackData)) {
            return processFirstCallback(callbackQuery, telegramUser, chatId, messageId);
        } else if (Callback.SECOND.equals(callbackData)) {
            setSuitableState(UserStates.IN_PROGRESS, telegramUser);
            return responseHelper.createInfoPage(chatId, messageId);
        } else if (Callback.THIRD.equals(callbackData)) {
            setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
            return responseHelper.createHelpPage(chatId, messageId);
        } else if (Callback.DENIED.equals(callbackData)) {
            disableManagerActions(callbackQuery.getMessage(), false);
            setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
            return responseHelper.createDeniedMessage(chatId);
        } else if (Callback.SUCCESS.equals(callbackData)) {
            disableManagerActions(callbackQuery.getMessage(), true);
            setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
            return responseHelper.createSuccessMessage(chatId);
        } else if (Callback.BACK.equals(callbackData)) {
            setSuitableState(UserStates.IN_PROGRESS, telegramUser);
            return responseHelper.recreateMainMenu(chatId, messageId);
        } else {
            responseHelper.handleError(Long.valueOf(chatId));
            return null;
        }
    }

    private void disableManagerActions(Message message, boolean status) throws TelegramApiException {
        EditMessageReplyMarkup editMessageText = InlineKeyboardMarkupBuilder
                .create(String.valueOf(message.getChatId()), status ? Callback.SUCCESS : Callback.DENIED)
                .rebuildAsEditMessageReplyMarkup(message.getMessageId(), null);
        editMessageText.setReplyMarkup(null);
        responseHelper.execute(editMessageText);
    }

    private PartialBotApiMethod<?> processFirstCallback(
            CallbackQuery callbackQuery,
            TelegramUser telegramUser,
            String chatId,
            int messageId
    ) {
        try {
            feedbackService.updateFeedback(telegramUser, "");
            setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
            return responseHelper.createInstructionMessage(chatId, messageId);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(callbackQuery);
        }
    }

    private void setSuitableState(UserStates state, TelegramUser forUser) {
        forUser.setState(state);
        telegramUserService.save(forUser);
    }

    private TelegramUser getSuitableTelegramUser(CallbackQuery callbackQuery, Message message) throws IncorrectFeedbackChannelPostException, TelegramUserNotExistException {
        if (callbackQuery.getData().equals(Callback.SUCCESS) || callbackQuery.getData().equals(Callback.DENIED)) {
            TelegramUser telegramUser;
            Long telegramUserIdFromComment = getTelegramUserIdFromComment(message);

            if (telegramUserIdFromComment == null) {
                throw new IncorrectFeedbackChannelPostException();
            }

            telegramUser = telegramUserService.findBy(telegramUserIdFromComment);
            if (telegramUser == null) {
                throw new TelegramUserNotExistException();
            }

            return telegramUser;
        } else {
            return telegramUserService.findBy(callbackQuery.getFrom().getId());
        }
    }
}
