package org.example.bot;

import org.example.db.PostgresDB;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentHandler {
    private final List<Integer> sentMessageIds;

    public CommentHandler() {
        this.sentMessageIds = new ArrayList<>();
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        try {
            int postId = Integer.parseInt(data);
            sendAllComments(postId, chatId, bot);
        } catch (NumberFormatException e) {
            sendErrorMessage(chatId, bot);
        }
    }

    private void sendAllComments(int postId, long chatId, TelegramBot bot) {
        deletePreviousMessages(chatId, bot);

        List<String> allComments;
        try {
            allComments = PostgresDB.getAllComments(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (String comment : allComments) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(comment);

            try {
                Message sentMessage = bot.sendMessage(sendMessage);
                sentMessageIds.add(sentMessage.getMessageId()); // Сохраняем идентификатор нового сообщения
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePreviousMessages(long chatId, TelegramBot bot) {
        for (Integer messageId : sentMessageIds) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(chatId));
            deleteMessage.setMessageId(messageId);
            try {
                bot.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        sentMessageIds.clear();
    }

    private void sendErrorMessage(long chatId, TelegramBot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Ошибка при обработке ID поста.");

        try {
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
