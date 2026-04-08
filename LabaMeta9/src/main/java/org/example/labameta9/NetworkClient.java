package org.example.labameta9;

import java.io.*;
import java.net.Socket;
import com.game.network.GameProtocol;

public class NetworkClient {
    private String host;
    private int port;
    private int currentLevel;

    public void setConfig(int level) {
        this.currentLevel = level;
        // Если уровень 1 или 3 — идем на прокси (8081), иначе — на сервер (8080)
        boolean useProxy = (level == 1 || level == 3);
        this.host = "127.0.0.1";
        this.port = useProxy ? 8081 : 8080;
    }

    public String sendCommand(int level, GameProtocol.GameMessage.CommandType type, String sender, String target, String text) {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            if (level < 2) {
                // УРОВЕНЬ 0 или 1: ТЕКСТ
                out.write(0); // Маркер текста
                // Превращаем Enum обратно в строку для текстового протокола
                String cmdStr = type.name() + "|" + sender + "|" + target + "|" + text + "\n";
                out.write(cmdStr.getBytes());
            } else {
                // УРОВЕНЬ 2 или 3: PROTOBUF
                out.write(1); // Маркер ProtoBuf
                GameProtocol.GameMessage message = GameProtocol.GameMessage.newBuilder()
                        .setCommand(type)
                        .setSender(sender != null ? sender : "")
                        .setTarget(target != null ? target : "")
                        .setText(text != null ? text : "")
                        .build();
                message.writeDelimitedTo(out);
            }
            out.flush();

            // Читаем ответ от сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.readLine(); // Сервер должен прислать строку + \n

        } catch (IOException e) {
            return "ERROR|" + e.getMessage();
        }
    }
}