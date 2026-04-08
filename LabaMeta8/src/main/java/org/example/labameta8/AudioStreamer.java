package org.example.labameta8;

public class AudioStreamer {
    private AudioSender sender;
    private AudioReceiver receiver;

    public void startCall(String remoteIp, int remoteUdpPort) {
        sender = new AudioSender(remoteIp, remoteUdpPort);
        receiver = new AudioReceiver(remoteUdpPort); // Слушаем свой порт

        new Thread((Runnable) sender).start();
        new Thread((Runnable) receiver).start();
    }

    public void stopCall() {
        if (sender != null) sender.stop();
        if (receiver != null) receiver.stop();
    }
}
