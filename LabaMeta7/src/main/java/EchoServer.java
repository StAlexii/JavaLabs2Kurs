import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EchoServer {
    private static final int PORT = 12345; // Порт для прослушивания


    public static void main(String[] args) {
        System.out.println("Сервер запущен на порту " + PORT);

        // try-with-resources для гарантированного закрытия ServerSocket

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Бесконечный цикл для приема клиентов (пока сервер работает)
            while (true) {
                // accept() блокирует, пока клиент не подключится
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключился клиент: " + clientSocket.getInetAddress());

                // ПОКА Обрабатываем клиента в этом же потоке
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера: " + e.getMessage());
        }
    }

    private static String reverseWords(String input) {
        if (input == null || input.isBlank()) return input;

        String[] words = input.trim().split("\\s+");
        List<String> wordList = Arrays.asList(words);
        Collections.reverse(wordList);

        return String.join(" ", wordList);
    }

    // метод обработки сообщений
    private static String handleClient(Socket clientSocket) {
        // Передаем clientSocket в try-with-resources (фишка Java 9+)
        // Все вложенные потоки также будут закрыты автоматически
        try (clientSocket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено: " + inputLine);

                String response = reverseWords(inputLine);

                out.println(response);
            }
            System.out.println("Клиент отключился.");


        } catch (IOException e) {
            System.err.println("Ошибка при общении с клиентом: " + e.getMessage());
        }
        return null;
    }
}

//запустить это, в cmd telnet localhost 12345 фим рим тим