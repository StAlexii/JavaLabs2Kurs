package org.example.labameta9;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class TrafficRelay implements Runnable {
    private final InputStream input;
    private final OutputStream output;
    private final Socket sourceSocket;
    private final Socket destSocket;

    public TrafficRelay(Socket source, Socket dest) throws IOException {
        this.sourceSocket = source;
        this.destSocket = dest;
        this.input = source.getInputStream();
        this.output = dest.getOutputStream();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
            // Ошибка чтения/записи обычно означает закрытие соединения
            System.out.println("Соединение разорвано: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }

    private void closeConnections() {
        try {
            if (!sourceSocket.isClosed()) sourceSocket.close();
            if (!destSocket.isClosed()) destSocket.close();
        } catch (IOException ignored) {}
    }
}
