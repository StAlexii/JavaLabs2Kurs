package org.example.labameta9;

import java.io.*;
import java.net.Socket;
import java.util.List;

import com.game.network.GameProtocol; // Твой сгенерированный ProtoBuf класс

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameLogic gameLogic;

    public ClientHandler(Socket socket, GameLogic gameLogic) {
        this.socket = socket;
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            // Читаем первый байт-маркер протокола
            int protocolType = in.read();

            if (protocolType == 0) {
                // ТЕКСТОВЫЙ РЕЖИМ (Уровни 0 и 1)
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String request = reader.readLine();
                String response = handleTextCommand(request);
                out.write((response + "\n").getBytes());

            } else if (protocolType == 1) {
                // PROTOBUF РЕЖИМ (Уровни 2 и 3)
                GameProtocol.GameMessage msg = GameProtocol.GameMessage.parseDelimitedFrom(in);
                String response = handleProtoCommand(msg);
                out.write((response + "\n").getBytes());
            }

        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }

    private String handleTextCommand(String raw) {
        if (raw == null || raw.isEmpty()) return "ERROR|Пустой запрос";

        // Разделяем: REGISTER|Алиса или HINT|Боб|Алиса|Текст
        String[] parts = raw.split("\\|");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "REGISTER":
                    // parts[1] - nickname
                    return gameLogic.register(parts[1]);

                case "HINT":
                    // parts[1] - sender, parts[2] - target, parts[3] - text
                    return gameLogic.addHint(parts[1], parts[2], parts[3]);

                case "GUESS":
                    // parts[1] - myName, parts[2] - targetUser, parts[3] - guessedName
                    return gameLogic.checkGuess(parts[1], parts[2], parts[3]);

                case "INBOX":
                    // parts[1] - nickname
                    java.util.List<String> messages = gameLogic.getInbox(parts[1]);
                    return "INBOX|" + String.join(";", messages);

                default:
                    return "ERROR|Неизвестная команда: " + command;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERROR|Недостаточно параметров для команды " + command;
        }
    }

    private String handleProtoCommand(GameProtocol.GameMessage msg) {
        switch (msg.getCommand()) {
            case REGISTER:
                return gameLogic.register(msg.getSender());

            case HINT:
                return gameLogic.addHint(msg.getSender(), msg.getTarget(), msg.getText());

            case GUESS:
                return gameLogic.checkGuess(msg.getSender(), msg.getTarget(), msg.getText());

            case INBOX:
                java.util.List<String> messages = gameLogic.getInbox(msg.getSender());
                return "INBOX|" + String.join(";", messages);

            default:
                return "ERROR|Неизвестная Proto-команда";
        }
    }
}
