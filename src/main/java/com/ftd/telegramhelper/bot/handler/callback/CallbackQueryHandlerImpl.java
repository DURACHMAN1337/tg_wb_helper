package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
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

    private final ResponseHelper responseHelper;
    private final TelegramUserService telegramUserService;

    @Autowired
    public CallbackQueryHandlerImpl(ResponseHelper responseHelper, TelegramUserService telegramUserService) {
        this.responseHelper = responseHelper;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery)
            throws TelegramApiException, TelegramUserNotExistException, IncorrectFeedbackChannelPostException {
        TelegramUser telegramUser = getSuitableTelegramUser(callbackQuery, callbackQuery.getMessage());
        String chatId = String.valueOf(telegramUser.getChatId());
        String callbackData = callbackQuery.getData();

        if (Callback.DENIED.equals(callbackData)) {
            disableManagerActions(callbackQuery.getMessage(), false);
            return responseHelper.createDeniedMessage(chatId);

        } else if (Callback.SUCCESS.equals(callbackData)) {
            disableManagerActions(callbackQuery.getMessage(), true);
            return responseHelper.createSuccessMessage(chatId);

        } else if (Callback.BACK.equals(callbackData)) {
            setSuitableState(UserStates.IN_MENU, telegramUser);
            return null;
            // return responseHelper.recreateMainMenu(chatId, messageId);
        } else if (Callback.START_CHATTING.equals(callbackData)) {
            setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
            return responseHelper.createChatWithManager(chatId);
        } else if (Callback.Faq.KNOWN_FAQ_CALLBACKS.contains(callbackData)) {
            return responseHelper.processFaqCallback(callbackQuery);
        } else {
            responseHelper.handleError(Long.valueOf(chatId));
            return null;
        }
    }

    private void disableManagerActions(Message message, boolean status) throws TelegramApiException {
        EditMessageReplyMarkup editMessageText = InlineKeyboardMarkupBuilder.create(String.valueOf(message.getChatId()), status ? Callback.SUCCESS : Callback.DENIED).rebuildAsEditMessageReplyMarkup(message.getMessageId(), null);
        editMessageText.setReplyMarkup(null);
        responseHelper.execute(editMessageText);
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
