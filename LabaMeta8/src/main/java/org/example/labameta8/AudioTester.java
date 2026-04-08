package org.example.labameta8;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class AudioTester {
    public static AudioFormat getAudioFormat() {
        // 8000.0 Гц, 16 бит, 1 канал (моно), signed, little-endian
        return new AudioFormat(8000.0f, 16, 1, true, false);
    }

    public static void runEchoTest() {
        System.out.println("Запуск эхо-теста. Говорите в микрофон 2 секунды...");
        try {
            AudioFormat format = getAudioFormat();

            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(targetInfo);
            microphone.open(format);
            microphone.start();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 2000) {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count > 0) {
                    outStream.write(buffer, 0, count);
                }
            }
            microphone.stop();
            microphone.close();

            System.out.println("Запись завершена. Воспроизведение...");

            byte[] audioData = outStream.toByteArray();
            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            speakers.open(format);
            speakers.start();

            speakers.write(audioData, 0, audioData.length);
            speakers.drain();
            speakers.stop();
            speakers.close();

            System.out.println("Эхо-тест успешно завершен!");

        } catch (LineUnavailableException e) {
            System.err.println("Ошибка аудиоустройства: " + e.getMessage());
        }
    }
}
