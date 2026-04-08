package com.example.labameta3;

import java.io.File;
import java.io.FilenameFilter;
//import java.util.NoSuchElementException;

public class ImageCollection implements Aggregate {
    private File[] files;

    public ImageCollection(File directory, String extension) {
        if (directory != null && directory.isDirectory()) {
            this.files = directory.listFiles((dir, name) ->{
                String lowerName = name.toLowerCase();
                if ("all".equalsIgnoreCase(extension)) {
                    return lowerName.endsWith(".jpg") ||
                            lowerName.endsWith(".jpeg") ||
                            lowerName.endsWith(".png") ||
                            lowerName.endsWith(".gif");
                } else {
                    // Фильтруем по конкретному расширению
                    return lowerName.endsWith("." + extension.toLowerCase());
                }
            });
        }

        if (this.files == null) {
            this.files = new File[0];
        }
    }


    @Override
    public Iterator getIterator() {

        return new ImageFileIterator();
    }

    // Метод для получения файла по индексу (для навигации)
    public File getFile(int index) {
        if (index >= 0 && index < files.length) return files[index];
        return null;
    }

    public int size() {

        return files.length;
    }

    // Внутренний класс итератора
    private class ImageFileIterator implements Iterator {
        private int currentIndex = -1; //первый next даст 0

        @Override
        public boolean hasNext() {
            return files.length > 0;
        }

        @Override
        public Object next() {
            if (files.length == 0) return null;
            currentIndex++;
            if (currentIndex >= files.length) {
                currentIndex = 0; //круговой переход к началу
            }
            return files[currentIndex];
             // Возвращать объект File следующий
        }

        @Override
        public Object preview() {
            if (files.length == 0) return null;
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = files.length - 1; // Круговой переход к концу
            }
            return files[currentIndex];
            // Возвращать объект File предыдущий
        }

        // Дополнительные методы для навигации
        public boolean haspreview() {
            return files.length > 0;
        }


        public void reset() {
            currentIndex = -1;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }


    }
}

