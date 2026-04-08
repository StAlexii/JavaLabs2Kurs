
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;



import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import java.net.InetSocketAddress;
import java.net.Proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import config.BotConfig;

import java.net.InetSocketAddress;

public class TextProcessorBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final SessionManager sessionManager = new SessionManager();
    private final QuizGame quizGame = new QuizGame();

    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10808));

    private final Map<Long, String> userStates = new ConcurrentHashMap<>(); //запомнить выбор
    private static final String STATE_WAITING_FOR_CAMEL = "WAITING_FOR_CAMEL";
    private static final String STATE_IDLE = "IDLE";
    private static final String STATE_QUIZ = "QUIZ_ACTIVE";

    private ReplyKeyboardMarkup getReplyKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/stats");
        row.add("/reset");

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)   // Кнопки станут компактными
                .oneTimeKeyboard(false) // Клавиатура не исчезнет после нажатия
                .build();
    }

    public TextProcessorBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(config.BotConfig.BOT_TOKEN);
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        UserSession session = sessionManager.getSession(chatId);

        if ("scenario_camel".equals(data)) {
            userStates.put(chatId, STATE_WAITING_FOR_CAMEL);
            sendText(chatId, "Ок! Теперь пришли мне фразу, и я превращу её в camelCase.");
        } else if ("scenario_quiz".equals(data)) {
            userStates.put(chatId, STATE_QUIZ);
            session.setQuizActive(true);
            sendText(chatId, "Начинаем викторину! Угадывай песню или исполнителя.");
            startNewRound(chatId, session);
        }
    }

    private void handleMessage(Message message) {
        String text = message.getText();
        long chatId = message.getChatId();
        UserSession session = sessionManager.getSession(chatId);
        String currentState = userStates.getOrDefault(chatId, STATE_IDLE);



        // 1. Обработка глобальных команд
        if (text.equals("/start")) {
            userStates.put(chatId, STATE_IDLE);
            session.setQuizActive(false);
            sendText(chatId, "Клавиатура команд.");

            SendMessage welcome = SendMessage.builder()
                    .chatId(chatId)
                    .text("Привет, выбери сценарий работы: (+ /stats /reset)")
                    .replyMarkup(getScenariosKeyboard())
                    .build();

            try {
                telegramClient.execute(welcome);
            } catch (TelegramApiException e) { e.printStackTrace(); }
            return;
        }

        if (text.equals("/stats")) {
            sendText(chatId, "📊 Твоя статистика:\nПравильных ответов: " + session.getScore() +  "\nВсего попыток: " + session.getTotalAttempts() + "\nКоличество игр: " + session.getGamesPlayed());
            return;
        }

        if (text.equals("/reset")) {
            session.reset();
            sendText(chatId, "Статистика сброшена");
            return;
        }

        if (text.equals("/quiz")) {
            userStates.put(chatId, STATE_QUIZ);
            session.setQuizActive(true);
            startNewRound(chatId, session);
            return;
        }

        // 2. Логика в зависимости от активного сценария
        switch (currentState) {
            case STATE_WAITING_FOR_CAMEL:
                sendText(chatId, "Результат: " + convertToCamelCase(text));
                break;

            case STATE_QUIZ:
                processAnswer(chatId, session, text);
                break;

            default:
                sendText(chatId, "Пожалуйста, выберите режим в меню /start или используйте /help");
                break;
        }
    }

    private void sendText(long chatId, String s) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(s)
                .replyMarkup(getReplyKeyboard())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    private String convertToCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String[] words = input.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();

            if (i == 0) {
                // первое слово маленькое
                result.append(word);
            } else {
                //  у остальных первая буква заглавная
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return result.toString();
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/start");
        row1.add("/help");

        keyboardRows.add(row1);

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    private InlineKeyboardMarkup getScenariosKeyboard() {
        InlineKeyboardButton camelButton = InlineKeyboardButton.builder()
                .text("Текст в camelCase")
                .callbackData("scenario_camel")
                .build();

        InlineKeyboardButton quizButton = InlineKeyboardButton.builder()
                .text("Музыкальная угадайка") // Вместо цифры 2
                .callbackData("scenario_quiz")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(camelButton, quizButton))
                .build();

//        InlineKeyboardRow row = new InlineKeyboardRow(camelButton, quizButton);
//        return new InlineKeyboardMarkup(List.of(row));
    }

    private void startNewRound(long chatId, UserSession session) {
        QuizQuestion next = quizGame.generateQuestion(session);
        if (next == null) {
            sendText(chatId, "🏆 Вопросы закончились! Твой итог: " + session.getScore());
            session.setQuizActive(false);
            session.getAskedQuestions().clear();
            //session.reset(); // cброс для новой игры
            userStates.put(chatId, STATE_IDLE);
            session.incrementGamesPlayed();
            return;
        }
        session.setCurrentQuestion(next);
        session.setQuizActive(true);
        sendText(chatId, "Угадай " + (next.getType() == QuestionType.GUESS_ARTIST ? "исполнителя" : "песню") +
                " по строчке:\n\n\"" + next.getSong().getLyricsSnippet() + "\"");
    }

    private void processAnswer(long chatId, UserSession session, String answer) {
        session.setTotalAttempts(session.getTotalAttempts() + 1);
        if (session.getCurrentQuestion().isCorrect(answer)) {
            session.setScore(session.getScore() + 1);
            sendText(chatId, "✅ Верно! Продолжаем?");
        } else {
            sendText(chatId, "❌ Ошибка! Правильный ответ: " + session.getCurrentQuestion().getCorrectAnswer());
        }
        startNewRound(chatId, session); // Сразу следующий вопрос
    }

}