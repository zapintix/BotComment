package org.example.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/posts"));
        row1.add(new KeyboardButton("/comments"));
        row1.add(new KeyboardButton("/most_popular_post"));

        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboard(List<String> allPosts) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String post : allPosts) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(post.split("\n")[1]); // Текст поста
            button.setCallbackData(post.split("\n")[0].split("/")[4]); // ID поста

            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }
}
