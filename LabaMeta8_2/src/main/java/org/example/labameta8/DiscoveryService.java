package org.example.labameta8_2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.example.labameta8_2.Peer.parsePeer;

public class DiscoveryService {
    private final Consumer<Peer> onPeerFound;
    private volatile boolean running = true;

    private static final String GROUP_IP = "230.0.0.1";
    private static final int PORT = 8888;

    public DiscoveryService(Consumer<Peer> onPeerFound) {
        this.onPeerFound = onPeerFound;
    }

    public void start(String myNick, int myTcpPort) {
        // Поток прослушивания (Receiver)
        new Thread(() -> {
            try (MulticastSocket socket = new MulticastSocket(8888)) {
                socket.joinGroup(InetAddress.getByName("230.0.0.1"));
                byte[] buf = new byte[256];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength());
                    // Парсим строку "HELLO Name IP Port"
                    Peer peer = parsePeer(data);
                    onPeerFound.accept(peer);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress group = InetAddress.getByName(GROUP_IP);
                String msg = "HELLO " + myNick + " " + InetAddress.getLocalHost().getHostAddress() + " " + myTcpPort;
                byte[] buf = msg.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, PORT);

                while (running) {
                    socket.send(packet);
                    Thread.sleep(3000); // Раз в 3 секунды
                }
            } catch (Exception e) { if (running) e.printStackTrace(); }
        }).start();
    }

    public void stop() { running = false; }

        // Поток рассылки (Announcer) — аналогично через Thread.sleep(3000)
    }


