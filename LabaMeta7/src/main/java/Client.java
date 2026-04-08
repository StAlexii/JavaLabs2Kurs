import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static java.lang.System.in;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Подключено к серверу. Вводите строки (Ctrl+D/Ctrl+C для выхода):");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput); // Отправляем серверу
                String response = in.readLine(); // Читаем ответ
                System.out.println("Ответ сервера: " + response);
            }
        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост: " + HOST);
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }
}