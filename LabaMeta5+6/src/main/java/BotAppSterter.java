import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Scanner;

class TextBotApplication {
    public static void main(String[] args) {

        try {
            System.out.println("Текущий IP: " +
                    new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(), "UTF-8")
                            .useDelimiter("\\A").next());
        } catch (Exception e) {
            System.out.println("Не удалось определить IP: " + e.getMessage());
        }

        try {
            // Создаем экземпляр бота
            TextProcessorBot quizBot = new TextProcessorBot(config.BotConfig.BOT_TOKEN);

            // Создаем приложение Long Polling
            TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();

            // Регистрируем бота
            botsApplication.registerBot(config.BotConfig.BOT_TOKEN, quizBot);

            System.out.println("✅ Бот успешно запущен!");
            System.out.println("🤖 Имя бота: @" + config.BotConfig.BOT_USERNAME);
            System.out.println("📊 Ожидание сообщений...");

            // Бесконечный цикл для работы приложения
            Thread.currentThread().join();

        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка Telegram API: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("👋 Бот остановлен");
        } catch (Exception e) {
            System.err.println("❌ Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}