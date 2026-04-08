package org.example.labameta8;

public class AudioStreamer {
    private AudioSender sender;
    private AudioReceiver receiver;

    // Просто открываем порт на прием (чтобы слышать других)
    public void startListening(int myPort) {
        if (receiver != null) receiver.stop();
        receiver = new AudioReceiver(myPort);
        new Thread((Runnable) receiver).start();
    }

    // Начинаем отправлять звук собеседнику
    public void startSending(String remoteIp, int remotePort) {
        if (sender != null) sender.stop();
        sender = new AudioSender(remoteIp, remotePort);
        new Thread((Runnable) sender).start();
    }

    public void stopCall() {
        if (sender != null) sender.stop();
        if (receiver != null) receiver.stop();
    }
}