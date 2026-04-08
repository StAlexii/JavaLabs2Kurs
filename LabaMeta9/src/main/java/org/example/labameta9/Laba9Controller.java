package org.example.labameta9;

import com.game.network.GameProtocol;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;

public class Laba9Controller {
    @FXML private TextField txtNickname, txtTarget;
    @FXML private TextArea txtMessage;
    @FXML private ListView<String> listInbox;
    @FXML private Label lblStatus;

    @FXML private RadioButton rbLevel0, rbLevel1, rbLevel2, rbLevel3;

    private final NetworkClient networkClient = new NetworkClient();

    @FXML
    public void initialize() {

    }

    private int getSelectedLevel() {
        if (rbLevel1.isSelected()) return 1;
        if (rbLevel2.isSelected()) return 2;
        if (rbLevel3.isSelected()) return 3;
        return 0;
    }

    @FXML
    private void onRegisterClick() {
        String name = txtNickname.getText();
        executeNetworkTask(() -> {
            networkClient.setConfig(getSelectedLevel());
            return networkClient.sendCommand(getSelectedLevel(), GameProtocol.GameMessage.CommandType.REGISTER, name, "", "");
        });
    }

    @FXML
    private void onSendHintClick() {
        String target = txtTarget.getText();
        String message = txtMessage.getText();
        String myName = txtNickname.getText(); // Кто отправляет (опционально)

        executeNetworkTask(() -> {
            int level = getSelectedLevel();
            networkClient.setConfig(level);

            // Передаем GameProtocol.GameMessage.CommandType.HINT
            return networkClient.sendCommand(
                    level,
                    GameProtocol.GameMessage.CommandType.HINT,
                    myName,  // sender
                    target,  // target
                    message  // text
            );
        });
    }

    @FXML
    private void onRefreshInboxClick() {
        String name = txtNickname.getText();
        executeNetworkTask(() -> {
            networkClient.setConfig(getSelectedLevel());
            String response = networkClient.sendCommand(getSelectedLevel(), GameProtocol.GameMessage.CommandType.INBOX, name, "", "");

            // Обновляем список сообщений в GUI
            Platform.runLater(() -> {
                listInbox.getItems().clear();
                if (response != null && response.startsWith("INBOX|")) {
                    String cleanData = response.replace("INBOX|", "");
                    String[] hints = cleanData.split(";"); // Используй символ, который ставишь на сервере
                    for (String hint : hints) {
                        if (!hint.isBlank()) listInbox.getItems().add(hint);
                    }
                }
            });
            return "Обновлено";
        });
    }

    private void executeNetworkTask(java.util.function.Supplier<String> task) {
        new Thread(() -> {
            try {
                String result = task.get();
                Platform.runLater(() -> lblStatus.setText(result));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onGuessClick() {
        String target = txtTarget.getText(); // Тот, кого мы пытаемся разоблачить
        String myGuess = txtMessage.getText(); // Наше предположение
        String myName = txtNickname.getText();

        executeNetworkTask(() -> {
            int level = getSelectedLevel();
            networkClient.setConfig(level);

            // Передаем GameProtocol.GameMessage.CommandType.GUESS
            return networkClient.sendCommand(
                    level,
                    GameProtocol.GameMessage.CommandType.GUESS,
                    myName,
                    target,
                    myGuess
            );
        });
    }
}

