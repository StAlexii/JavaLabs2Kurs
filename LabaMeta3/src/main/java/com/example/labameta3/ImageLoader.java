package com.example.labameta3;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    // Загрузка изображения из файла
    public static Image loadFromFile(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return new Image(is);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    // Загрузка из ресурсов (для тестирования)
    public static Image loadFromResource(String path) {
        return new Image(ImageLoader.class.getResourceAsStream(path));
    }
}
