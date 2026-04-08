package org.example.labameta9;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyStarter {
    private static final int PROXY_PORT = 8081;     // Порт, который видит клиент
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8080;    // Порт реального сервера

    public static void main(String[] args) {
        try (ServerSocket proxySocket = new ServerSocket(PROXY_PORT)) {
            System.out.println("Proxy-сервер запущен на порту " + PROXY_PORT);
            System.out.println("Трафик перенаправляется на " + SERVER_HOST + ":" + SERVER_PORT);

            while (true) {
                // Ждем подключения клиента (Алисы или Боба)
                Socket clientSocket = proxySocket.accept();
                System.out.println("Новое подключение к прокси от: " + clientSocket.getInetAddress());

                try {
                    // Сразу открываем соединение к реальному серверу
                    Socket serverSocket = new Socket(SERVER_HOST, SERVER_PORT);

                    // Создаем два реле для двусторонней связи
                    TrafficRelay clientToServer = new TrafficRelay(clientSocket, serverSocket);
                    TrafficRelay serverToClient = new TrafficRelay(serverSocket, clientSocket);

                    // Запускаем их в отдельных потоках
                    new Thread(clientToServer).start();
                    new Thread(serverToClient).start();

                } catch (IOException e) {
                    System.err.println("Не удалось соединиться с основным сервером: " + e.getMessage());
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
