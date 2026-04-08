package org.example.labameta8;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Laba8Controller {
    @FXML private TextField nickField, portField, UserIp, UserPort;
    @FXML private ListView<Peer> userListView;
    @FXML private Label statusLabel;

    private ObservableList<Peer> contactList = FXCollections.observableArrayList();
    private DiscoveryService discoveryService;
    private AudioStreamer audioStreamer = new AudioStreamer();

    @FXML
    public void initialize() {
        userListView.setItems(contactList);
        statusLabel.setText("Готов к работе. Введите данные.");
    }

    // Кнопка: "Запустить ожидание"
    @FXML
    public void onStartWaiting() {
        try {
            int myPort = Integer.parseInt(portField.getText()); // Это ваш UDP порт для звука
            String myNick = nickField.getText().isEmpty() ? "User2" : nickField.getText();

            discoveryService = new DiscoveryService(newPeer -> {
                Platform.runLater(() -> {
                    if (!contactList.contains(newPeer)) {
                        contactList.add(newPeer);
                        statusLabel.setText("В сети: " + contactList.size());
                    }
                });
            });

            // Запускаем рассылку с вашим UDP портом
            discoveryService.start(myNick, myPort);

            // Включаем прослушивание входящего звука на этом же порту
            audioStreamer.startListening(myPort);

            statusLabel.setText("Ожидание на порту " + myPort);
        } catch (Exception e) {
            statusLabel.setText("Ошибка запуска!");
        }
    }

    // Общий метод для начала передачи голоса
    private void startTalking(String ip, int port) {
        try {
            // Мы не ждем ответа по TCP, а просто начинаем слать UDP, как первая программа
            audioStreamer.startSending(ip, port);
            statusLabel.setText("Передача звука на " + ip + ":" + port);
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    // Кнопка: "Позвонить" (из списка)
    @FXML
    public void onCallButtonClick() {
        Peer selected = userListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            startTalking(selected.getIp(), selected.getPort());
        } else {
            statusLabel.setText("Сначала выберите пользователя!");
        }
    }

    // Кнопка: "Позвонить по IP" (ручной ввод)
    @FXML
    public void onManualCallClick() {
        String ip = UserIp.getText().trim();
        String portStr = UserPort.getText().trim();

        if (!ip.isEmpty() && !portStr.isEmpty()) {
            startTalking(ip, Integer.parseInt(portStr));
        } else {
            statusLabel.setText("Введите IP и Порт!");
        }
    }

    @FXML
    public void handleHangUp() {
        audioStreamer.stopCall();
        statusLabel.setText("Передача звука остановлена.");
    }
}