package com.karinaco.prio;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class PrioBot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            react(update, update.getCallbackQuery().getData());
        } else {
            if ("new".equals(update.getMessage().getText().toLowerCase()) || "جدید".equals(update.getMessage().getText())) {
                begin(update);
            } else {
                sendMessage(update, "لطفا جهت شروع مجدد عبارت \"new\" یا \"جدید\" را وارد کنید.");
            }
        }
    }

    public String getBotUsername() {
        return "Karina Priority Bot";
    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    private InlineKeyboardMarkup createMarkup(String confirmText, String denyText) {
        InlineKeyboardMarkup inlineMessageKeyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        inlineKeyboardButtons.add(new InlineKeyboardButton().setText("بله").setCallbackData(confirmText));
        inlineKeyboardButtons.add(new InlineKeyboardButton().setText("خیر").setCallbackData(denyText));

        return inlineMessageKeyboard.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
    }

    private void sendMessage(Update update, String text) {
        SendMessage sendMessage = new SendMessage().setReplyToMessageId(update.getMessage().getMessageId()).setText(text);
        sendMessage(sendMessage);
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void begin(Update update) {
        InlineKeyboardMarkup inlineMessageKeyboard = createMarkup("main-function", "low");

        SendMessage sendMessage = new SendMessage().setText("آیا کار مورد نظر جزء عملکردهای اصلی شرکت است؟").setReplyToMessageId(update.getMessage().getMessageId()).setReplyMarkup(inlineMessageKeyboard);
        sendMessage(sendMessage);
    }

    private void askForCoupling(Update update) {
        InlineKeyboardMarkup inlineMessageKeyboard = createMarkup("has-coupling", "medium");

        SendMessage sendMessage = new SendMessage().setText("آیا برروی موارد دیگر تاثیر میگذارد؟").setReplyToMessageId(update.getMessage().getMessageId()).setReplyMarkup(inlineMessageKeyboard);
        sendMessage(sendMessage);
    }

    private void askForDeadline(Update update) {
        InlineKeyboardMarkup inlineMessageKeyboard = createMarkup("affects-deadline", "high");

        SendMessage sendMessage = new SendMessage().setText("آیا  باعث نرسیدن به مهلت سررسید می شود؟").setReplyToMessageId(update.getMessage().getMessageId()).setReplyMarkup(inlineMessageKeyboard);
        sendMessage(sendMessage);

    }

    private void askForSeverFault(Update update) {
        InlineKeyboardMarkup inlineMessageKeyboard = createMarkup("blocker", "highest");

        SendMessage sendMessage = new SendMessage().setText("آیا باعث مختل شدن کار کاربر می‌ شود؟").setReplyToMessageId(update.getMessage().getMessageId()).setReplyMarkup(inlineMessageKeyboard);
        sendMessage(sendMessage);
    }

    private void react(Update update, String message) {
        switch (message) {
            case "low":
                sendMessage(update, "low");
                break;
            case "medium":
                sendMessage(update, "medium");
                break;
            case "high":
                sendMessage(update, "high");
                break;
            case "highest":
                sendMessage(update, "highest");
                break;
            case "blocker":
                sendMessage(update, "blocker");
                break;
            case "is-main-function":
                sendMessage(update, "medium");
                break;
            case "main-function":
                askForCoupling(update);
                break;
            case "has-coupling":
                askForDeadline(update);
                break;
            case "affects-deadline":
                askForSeverFault(update);
                break;
            default:
                begin(update);
        }
    }
}
