package org.example.labameta8;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class DiscoveryService {
    private final Consumer<Peer> onPeerFound;
    private volatile boolean running = true;
    private static final String GROUP_IP = "230.0.0.1";
    private static final int PORT = 8888;

    public DiscoveryService(Consumer<Peer> onPeerFound) {
        this.onPeerFound = onPeerFound;
    }

    public void start(String myNick, int myUdpPort) {
        running = true;

        // 1. Поток прослушивания (Receiver)
        new Thread(() -> {
            try {
                // Важно: создаем сокет без привязки к порту сразу
                MulticastSocket socket = new MulticastSocket(null);
                socket.setReuseAddress(true); // Разрешаем совместное использование порта
                socket.bind(new InetSocketAddress(PORT));

                InetAddress group = InetAddress.getByName(GROUP_IP);
                socket.joinGroup(group);

                byte[] buf = new byte[1024];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    if (data.startsWith("HELLO")) {
                        String[] parts = data.split(" ");
                        if (parts.length >= 4) {
                            String name = parts[1];
                            String ip = parts[2];
                            int port = Integer.parseInt(parts[3]);

                            // Фильтруем себя: на одном ПК IP совпадет, поэтому проверяем и порт
                            String myIp = InetAddress.getLocalHost().getHostAddress();
                            if (ip.equals(myIp) && port == myUdpPort) {
                                continue;
                            }

                            Peer peer = new Peer(name, ip, port);
                            onPeerFound.accept(peer);
                        }
                    }
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        }).start();

        // 2. Поток рассылки (Announcer)
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress group = InetAddress.getByName(GROUP_IP);
                String myIp = InetAddress.getLocalHost().getHostAddress();

                while (running) {
                    // Формат должен быть идентичен: HELLO Nick IP Port
                    String msg = "HELLO " + myNick + " " + myIp + " " + myUdpPort;
                    byte[] buf = msg.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, PORT);

                    socket.send(packet); // ИСПРАВЛЕНО: Теперь отправляем, а не получаем
                    Thread.sleep(3000); // Пауза между рассылками
                }
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        running = false;
    }
}