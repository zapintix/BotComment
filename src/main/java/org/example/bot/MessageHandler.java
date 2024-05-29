package org.example.bot;

import org.example.db.PostgresDB;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MessageHandler {
    private final PostgresDB postgresDB;
    private final KeyboardBuilder keyboardBuilder;

    public MessageHandler() {
        this.postgresDB = new PostgresDB();
        this.keyboardBuilder = new KeyboardBuilder();
    }

    public void handleMessage(Message message, TelegramBot bot) {
        long chatId = message.getChatId();
        String messageText = message.getText();
        switch (messageText) {
            case "/posts" -> sendAllPosts(message, bot);
            case "/start" -> sendStartMessage(message, bot);
            case "/comments" -> sendPostsSelectionMenu(chatId, bot);
            case "/most_popular_post" -> sendMostPopularPost(message, bot);
            default -> handleReplyToMessage(message);
        }
    }

    private void sendAllPosts(Message message, TelegramBot bot) {
        List<String> allPosts = PostgresDB.getAllPosts();
        for (String post : allPosts) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText(post);

            try {
                bot.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStartMessage(Message message, TelegramBot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Привет! Введите команду!");
        sendMessage.setReplyMarkup(keyboardBuilder.getMainMenuKeyboard());

        try {
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPostsSelectionMenu(long chatId, TelegramBot bot) {
        List<String> allPosts = PostgresDB.getAllPosts();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Выберите пост для получения комментариев:");
        sendMessage.setReplyMarkup(keyboardBuilder.getInlineKeyboard(allPosts));

        try {
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMostPopularPost(Message message, TelegramBot bot) {
        String mostPopularPost = postgresDB.getMostPopularPost();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(mostPopularPost);

        try {
            bot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleReplyToMessage(Message message) {
        if (message.isReply() && message.getReplyToMessage().hasText()) {
            Message replyToMessage = message.getReplyToMessage();
            int postId = replyToMessage.getForwardFromMessageId();
            String commentText = message.getText();
            String commentName = message.getFrom().getUserName();

            postgresDB.saveComment(postId, commentName, commentText);
        }
    }
}
