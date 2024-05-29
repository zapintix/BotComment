package org.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private final PostHandler postHandler;
    private final CommentHandler commentHandler;
    private final MessageHandler messageHandler;

    public TelegramBot() {
        this.postHandler = new PostHandler();
        this.commentHandler = new CommentHandler();
        this.messageHandler = new MessageHandler();
    }

    @Override
    public String getBotUsername() {
        return "exapleVuz_bot";
    }

    @Override
    public String getBotToken() {
        return "7099556660:AAGBNUPXze53VRLnD2_mLBVcV-4zQV3Fm6Q";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasChannelPost()) {
            postHandler.handleChannelPost(update.getChannelPost());// получение постов
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            messageHandler.handleMessage(update.getMessage(), this);
        } else if (update.hasCallbackQuery()) {
            commentHandler.handleCallbackQuery(update.getCallbackQuery(), this);
        }
    }

    public Message sendMessage(SendMessage sendMessage) throws TelegramApiException {
        return execute(sendMessage);
    }
}
