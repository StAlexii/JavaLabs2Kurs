package org.example.labameta8;
import javax.sound.sampled.*;
import java.net.*;

public class AudioSender implements Runnable {

    private final String remoteIp;
    private final int remotePort;
    private volatile boolean running = true;

    public AudioSender(String remoteIp, int remotePort) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
    }

    @Override
    public void run() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
        try (DatagramSocket socket = new DatagramSocket()) {
            TargetDataLine line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();

            byte[] buffer = new byte[1024];
            InetAddress address = InetAddress.getByName(remoteIp);

            while (running) {
                int read = line.read(buffer, 0, buffer.length);
                DatagramPacket packet = new DatagramPacket(buffer, read, address, remotePort);
                socket.send(packet);
            }
            line.stop();
            line.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void stop() { running = false; }
}
