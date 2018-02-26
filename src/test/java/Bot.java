import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.Random;
import java.util.logging.Level;

import static org.telegram.telegrambots.logging.BotLogger.log;

/**
 * Created by Fufaev M.V.
 */

@Version(version = "0.0.1")
public class Bot extends TelegramLongPollingBot {
    private String patt = "Сыграй со мной в игру. Я загадал число от 0 до 1000. Попробуй отгадать. " +
            "\n /start - начать игру;" +
            "\n /stop - выйти из игры";
    private int secret = setSecretNumber();
    private int counter = 0;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Служебный метод для связи бота с сервером
     * @return токен бота
     */
    @Override
    public String getBotToken() {
        return "530563747:AAFEpJDWBBr9pEI-cCcPJR6TfQHnQy-lIKI";
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимо отправить в качестве сообщения.
     */
    private void sendMsg(Long chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    /**
     * Метод для настройки фото и ее отправки
     * @param chatId id чата
     * @param url Адрес фото
     */
    private void sendPht(Long chatId, String url) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.enableNotification();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(url);

        try {
            sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    /**
     * Возращает имя бота, указанное при регистрации
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "MishaGameBot";
    }

    /**
     * Обрабатывает событие, спровоцированное пользователем
     * @param update - событие (сообщение)
     */
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        int receivedNumber;

        if (message.getText().equals("/start")) {
            sendMsg(message.getChatId(), getPatt());
            counter = 0;
            secret = setSecretNumber();
        } else if (message.getText().equals("/stop")) {
            sendMsg(message.getChatId(), "Вы остановили игру!");
            counter = 0;
            secret = setSecretNumber();
        } else {
            ++counter;

            if (counter <= 10) {
                receivedNumber = Integer.parseInt(message.getText());

                if (receivedNumber > secret) {
                    sendMsg(message.getChatId(), "Ваше число больше загаданного");
                } else if (receivedNumber == secret) {
                    sendPht(message.getChatId(), "http://idea-s.ru/wp-content/uploads/2015/05/pobeda.png");

                    sendMsg(message.getChatId(), getPatt());

                    counter = 0;
                    secret = setSecretNumber();
                } else {
                    sendMsg(message.getChatId(), "Ваше число меньше загаданного");
                }
            } else {
                sendPht(message.getChatId(), "https://avatanplus.com/files/resources/mid/5979c7fcc1dfe15d83b5337a.png");

                sendMsg(message.getChatId(), getPatt());
                counter = 0;
                secret = setSecretNumber();
            }
        }
    }

    /**
     * Метод устанавливает секретный номер, который пользователь должен угадать
     * @return возвращает его
     */
    private int setSecretNumber() {
        Random random = new Random();
        return random.nextInt(1000);
    }

    /**
     * @return Шаблон
     */
    public String getPatt() {
        return patt;
    }
}
