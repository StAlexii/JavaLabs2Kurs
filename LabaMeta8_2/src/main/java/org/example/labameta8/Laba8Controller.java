package org.example.labameta8;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Laba8Controller {
    // Добавь эти поля, чтобы брать данные из интерфейса
    @FXML private TextField nickField;
    @FXML private TextField portField;
    @FXML private ListView<Peer> userListView;
    @FXML private Label statusLabel;
    @FXML private TextField UserIp;
    @FXML private TextField UserPort;

    private ObservableList<Peer> contactList = FXCollections.observableArrayList();
    private DiscoveryService discoveryService;
    private SignalingServer signalingServer;
    private AudioStreamer audioStreamer = new AudioStreamer();

    // Переменная для хранения текущего собеседника (нужна для сброса)
    private Peer currentPeer;

    @FXML
    public void initialize() {
        userListView.setItems(contactList);

        new Thread(() -> {
            Platform.runLater(() -> statusLabel.setText("Проверка звука (эхо-тест 2 сек)..."));
            AudioTester.runEchoTest(); // Тот самый метод из твоего индивидуального задания
            Platform.runLater(() -> statusLabel.setText("Звук проверен. Готов к работе."));
        }).start();

        loadLastPeer(); // Загружаем старого знакомого из файла
        startStatusChecker(); // Запускаем проверку "живой или нет"

        statusLabel.setText("Введите данные и нажмите 'Запустить ожидание'");
    }

    private void initiateCall(Peer target) {
        currentPeer = target; // Сохраняем, чтобы потом можно было сбросить звонок (handleHangUp)

        new Thread(() -> {
            try (Socket socket = new Socket(target.getIp(), target.getPort());
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // 1. Отправляем сигнал
                out.println("CALL_START");

                Platform.runLater(() -> statusLabel.setText("Звоним " + target.getIp() + "..."));

                // 2. Запускаем аудио (UDP)
                int remoteAudioPort = target.getPort() + 1;
                audioStreamer.startCall(target.getIp(), remoteAudioPort);

                Platform.runLater(() -> statusLabel.setText("Разговор с " + target.getNickname()));

                // Если используешь сохранение последнего пира, раскомментируй:
                // saveLastPeer(target);

            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Ошибка связи: " + e.getMessage()));
            }
        }).start();
    }



    // Кнопка 2: "Позвонить по IP" (НОВАЯ КНОПКА)
    @FXML
    public void onManualCallClick() {
        String targetIp = UserIp.getText().trim();
        String portStr = UserPort.getText().trim();

        if (targetIp.isEmpty() || portStr.isEmpty()) {
            statusLabel.setText("Введите IP и порт собеседника");
            return;
        }

        try {
            int targetPort = Integer.parseInt(portStr);
            // Создаем "виртуального" пира. Ник мы его не знаем, поэтому пишем "Unknown" или IP
            Peer manualPeer = new Peer("Unknown", targetIp, targetPort);

            initiateCall(manualPeer);

        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка: порт должен быть числом");
        }
    }

    // 1. Метод запуска сервисов (по нажатию кнопки)
    @FXML
    public void onStartWaiting() {
        try {
            String myNick = nickField.getText().isEmpty() ? "User" : nickField.getText();
            int myTcpPort = Integer.parseInt(portField.getText());

            // Инициализируем поиск
            discoveryService = new DiscoveryService(newPeer -> {
                Platform.runLater(() -> {
                    // 1. Ищем, есть ли уже такой пользователь в нашем списке (по IP/порту)
                    int index = contactList.indexOf(newPeer);

                    if (index != -1) {
                        // Если нашли (например, это сохраненный из файла А)
                        Peer existing = contactList.get(index);
                        existing.updateLastSeen(); // Ставим online = true и обновляем время
                    } else {
                        // Если это абсолютно новый пользователь (Б)
                        newPeer.updateLastSeen();
                        contactList.add(newPeer);
                    }

                    // 2. КРИТИЧЕСКИ ВАЖНО: заставить ListView перерисоваться
                    userListView.refresh();
                });
            });

            discoveryService.start(myNick, myTcpPort);

            // Запускаем TCP-сервер
            signalingServer = new SignalingServer(myTcpPort, this::handleIncomingCall);
            new Thread(signalingServer).start();

            statusLabel.setText("Ожидание звонков на порту " + myTcpPort);
        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка: введите корректный порт");
        }
    }

    // 2. Логика обработки входящего сообщения (Сигнализация)
    private void handleIncomingCall(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("CALL_START")) {
                statusLabel.setText("Разговор идет...");
                // В реальной лабе тут нужно достать IP звонящего.
                // Для простоты: если получили сигнал, запускаем прием аудио
                // Порт для аудио = наш TCP порт + 1
                int myAudioPort = Integer.parseInt(portField.getText()) + 1;

                // ВАЖНО: Входящий звонок тоже требует запуска аудио-стримера
                // Чтобы слышать и говорить
                // audioStreamer.startCall(remoteIp, remoteUdpPort);
            } else if (message.equals("CALL_END")) {
                statusLabel.setText("Собеседник завершил звонок");
                audioStreamer.stopCall();
            }
        });
    }

    // 3. Логика нажатия кнопки "Позвонить"
    @FXML
    public void onCallButtonClick() {
        currentPeer = userListView.getSelectionModel().getSelectedItem();
        if (currentPeer == null) {
            statusLabel.setText("Выберите кого-нибудь в списке!");
            return;
        }

        // Запускаем сетевой запрос в отдельном потоке, чтобы UI не завис
        new Thread(() -> {
            try (Socket socket = new Socket(currentPeer.getIp(), currentPeer.getPort());
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Шлем сигнал начала звонка
                out.println("CALL_START");

                Platform.runLater(() -> statusLabel.setText("Звоним " + currentPeer.getNickname() + "..."));

                // Запускаем аудио: шлем на (порт пира + 1), слушаем на (наш порт + 1)
                int remoteAudioPort = currentPeer.getPort() + 1;
                audioStreamer.startCall(currentPeer.getIp(), remoteAudioPort);

                Platform.runLater(() -> statusLabel.setText("Разговор с " + currentPeer.getNickname()));

            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Ошибка связи: " + e.getMessage()));
            }
        }).start();
    }

    // 4. Логика завершения звонка
    @FXML
    public void handleHangUp() {
        if (currentPeer != null) {
            new Thread(() -> {
                try (Socket socket = new Socket(currentPeer.getIp(), currentPeer.getPort());
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println("CALL_END");

                    saveLastPeer(currentPeer);

                } catch (Exception e) { /* игнорируем ошибки при закрытии */ }
            }).start();
        }

        audioStreamer.stopCall();
        statusLabel.setText("Вы завершили звонок");
    }

    private void saveLastPeer(Peer peer) {
        try (PrintWriter out = new PrintWriter("last_peer.txt")) {
            out.println(peer.getNickname() + ";" + peer.getIp() + ";" + peer.getPort());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startStatusChecker() {
        Thread checker = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Проверяем каждые 5 сек
                    long now = System.currentTimeMillis();

                    Platform.runLater(() -> {
                        boolean changed = false;
                        for (Peer p : contactList) {
                            // Если от пользователя не было HELLO больше 12 секунд
                            if (p.isOnline() && (now - p.getLastSeen() > 12000)) {
                                p.setOnline(false);
                                changed = true;
                            }
                        }
                        if (changed) {
                            userListView.refresh(); // Перерисовываем список, если кто-то "ушел"
                        }
                    });
                } catch (InterruptedException e) { break; }
            }
        });
        checker.setDaemon(true);
        checker.start();
    }

    private void loadLastPeer() {
        File file = new File("last_peer.txt");
        if (!file.exists()) return;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = in.readLine();
            if (line != null) {
                String[] p = line.split(";");
                Peer savedPeer = new Peer(p[0], p[1], Integer.parseInt(p[2]));
                savedPeer.setOnline(false); // По умолчанию считаем, что он оффлайн
                contactList.add(savedPeer);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}