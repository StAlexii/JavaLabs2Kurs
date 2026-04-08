package org.example.labameta8;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioReceiver implements Runnable {

    private final int port;
    private volatile boolean running = true;
    private DatagramSocket socket;

    public AudioReceiver(int port) { this.port = port; }

    @Override
    public void run() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
        try {
            socket = new DatagramSocket(port);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();

            byte[] buffer = new byte[1024];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                line.write(packet.getData(), 0, packet.getLength());
            }
            line.drain();
            line.close();
        } catch (Exception e) { if (running) e.printStackTrace(); }
    }

    public void stop() {
        running = false;
        if (socket != null) socket.close(); // Прерывает блокирующий receive()
    }
}
