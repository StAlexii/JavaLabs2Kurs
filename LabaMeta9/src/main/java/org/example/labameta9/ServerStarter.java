package org.example.labameta9;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStarter {
    private static final int PORT = 8080;
    private static final GameLogic gameLogic = new GameLogic();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Main Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Для каждого клиента создаем отдельный обработчик в новом потоке
                new Thread(new ClientHandler(clientSocket, gameLogic)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}