package org.example.labameta8;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.*;

public class Laba8Controller {
    @FXML private TextField nickField, portField, UserIp, UserPort;
    @FXML private ListView<Peer> userListView;
    @FXML private Label statusLabel;

    private ObservableList<Peer> contactList = FXCollections.observableArrayList();
    private DiscoveryService discoveryService;
    private SignalingServer signalingServer;
    private AudioStreamer audioStreamer = new AudioStreamer();

    private Peer currentPeer;
    private boolean isConnected = false; // Добавили флаг

    @FXML
    public void initialize() {
        userListView.setItems(contactList);
        // Эхо-тест при запуске
        new Thread(() -> {
            Platform.runLater(() -> statusLabel.setText("Проверка звука..."));
            AudioTester.runEchoTest();
            Platform.runLater(() -> statusLabel.setText("Готов. Нажмите 'Запустить ожидание'"));
        }).start();

        loadLastPeer();
        startStatusChecker();
    }

    // 1. Метод для запуска звонка (исправлен)
    private void initiateCall(String remoteIp, int remotePort) {
        new Thread(() -> {
            try (Socket socket = new Socket()) {
                // Устанавливаем таймаут, чтобы программа не зависла на минуту
                socket.connect(new InetSocketAddress(remoteIp, remotePort), 3000);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Получаем свой реальный IP в локальной сети
                String myIp = getLocalIp();

                // Протокол: ТИП_КОМАНДЫ ; КТО_ЗВОНИТ ; НА_КАКОЙ_UDP_ПОРТ_ОТВЕЧАТЬ
                out.println("CALL_START;" + myIp + ";" + (Integer.parseInt(portField.getText()) + 1));

                Platform.runLater(() -> statusLabel.setText("Разговор с " + remoteIp));

                // Сразу включаем свой звук на порт собеседника (UDP)
                audioStreamer.startCall(remoteIp, remotePort + 1);

            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Не удалось дозвониться: " + e.getMessage()));
            }
        }).start();
    }

    private String getLocalIp() {
        try {
            try (Socket socket = new Socket()) {
                // Пытаемся "дотянуться" до внешнего адреса (неважно какого),
                // чтобы система выбрала активный сетевой интерфейс
                socket.connect(new InetSocketAddress("8.8.8.8", 80), 500);
                return socket.getLocalAddress().getHostAddress();
            }
        } catch (Exception e) {
            return "127.0.0.1"; // Если интернета нет вообще
        }
    }

    // Кнопка "Позвонить по IP"
    @FXML
    public void onManualCallClick() {
        String ip = UserIp.getText().trim();
        String portStr = UserPort.getText().trim();
        if (!ip.isEmpty() && !portStr.isEmpty()) {
            initiateCall(ip, Integer.parseInt(portStr));
        }
    }

    // Кнопка "Позвонить из списка"
    @FXML
    public void onCallButtonClick() {
        Peer selected = userListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            initiateCall(selected.getIp(), selected.getPort());
        }
    }

    // 2. Логика приема звонка (ИСПРАВЛЕНО - теперь включает звук)
    private void handleIncomingCall(String message) {
        Platform.runLater(() -> {
            try {
                if (message.startsWith("CALL_START")) {
                    // Парсим сообщение типа "CALL_START;192.168.1.5;5000"
                    String[] parts = message.split(";");
                    String callerIp = parts[1];
                    int callerPort = Integer.parseInt(parts[2]);

                    statusLabel.setText("Входящий от " + callerIp);

                    // ВКЛЮЧАЕМ ЗВУК ПРИЕМНИКА
                    // Шлем звук обратно звонящему на его UDP порт (его TCP + 1)
                    audioStreamer.startCall(callerIp, callerPort + 1);

                    isConnected = true;
                    // Сохраняем того, кто нам позвонил, чтобы мы могли нажать "Сброс"
                    currentPeer = new Peer("Caller", callerIp, callerPort);

                } else if (message.equals("CALL_END")) {
                    statusLabel.setText("Звонок завершен");
                    audioStreamer.stopCall();
                    isConnected = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void onStartWaiting() {
        try {
            int myTcpPort = Integer.parseInt(portField.getText());

            discoveryService = new DiscoveryService(newPeer -> {
                Platform.runLater(() -> {
                    if (!contactList.contains(newPeer)) {
                        newPeer.updateLastSeen();
                        contactList.add(newPeer);
                    } else {
                        contactList.get(contactList.indexOf(newPeer)).updateLastSeen();
                    }
                    userListView.refresh();
                });
            });
            discoveryService.start(nickField.getText(), myTcpPort);

            signalingServer = new SignalingServer(myTcpPort, this::handleIncomingCall);
            new Thread(signalingServer).start();

            statusLabel.setText("Ожидание на порту " + myTcpPort);
        } catch (Exception e) {
            statusLabel.setText("Ошибка запуска сервера!");
        }
    }

    @FXML
    public void handleHangUp() {
        if (currentPeer != null) {
            new Thread(() -> {
                try (Socket socket = new Socket(currentPeer.getIp(), currentPeer.getPort());
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println("CALL_END");
                } catch (Exception ignored) {}
            }).start();
        }
        audioStreamer.stopCall();
        isConnected = false;
        statusLabel.setText("Звонок завершен");
    }

    // Остальные методы (save/load/checker) остаются без изменений...
    private void saveLastPeer(Peer peer) { /* твой код */ }
    private void loadLastPeer() { /* твой код */ }
    private void startStatusChecker() { /* твой код */ }
}