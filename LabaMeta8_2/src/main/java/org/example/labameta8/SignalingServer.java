package org.example.labameta8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class SignalingServer implements Runnable{
    private final int port;
    private final Consumer<String> onMessageReceived;
    private volatile boolean running = true;
    public SignalingServer(int port, Consumer<String> onMessageReceived) {
        this.port = port;
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String message = in.readLine();
                    if (message != null) {
                        onMessageReceived.accept(message);
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при приеме соединения: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }

    public void stop() { running = false; }
}
