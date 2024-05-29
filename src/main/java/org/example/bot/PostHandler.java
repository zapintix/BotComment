package org.example.bot;

import org.example.db.PostgresDB;
import org.telegram.telegrambots.meta.api.objects.Message;

public class PostHandler {
    private final PostgresDB postgresDB;

    public PostHandler() {
        this.postgresDB = new PostgresDB();
    }

    public void handleChannelPost(Message channelMessage) {
        String channelUsername = "BOT";
        String text = channelMessage.getText();
        int postId = channelMessage.getMessageId();

        postgresDB.savePost(channelUsername, text, postId);
        System.out.println(channelUsername);
        System.out.println(postId);
    }
}
