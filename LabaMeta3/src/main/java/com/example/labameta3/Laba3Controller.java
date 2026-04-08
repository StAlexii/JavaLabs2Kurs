package com.example.labameta3;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
//наши методы
import com.example.labameta3.ImageCollection;
import com.example.labameta3.ImageLoader;
import com.example.labameta3.Aggregate;
import com.example.labameta3.Iterator;


public class Laba3Controller {
    @FXML private ImageView mainImageView;
    @FXML private Label counterLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private Button auto;
    @FXML private Button prop; //info
    @FXML private Button butFirst;
    @FXML private Button butPrev;
    @FXML private Button butNext;
    @FXML private Button butLast;

    private ImageCollection collection;
    private Iterator iterator;
    private Timeline timeline;
    private File currentDirectory = new File("photos");

    private void loadCollection(File dir) {
        String SelExt = filterCombo.getValue();

        if (SelExt == null) SelExt = "jpg";

        this.collection = new ImageCollection(dir, SelExt);

        this.iterator = collection.getIterator();

        // Проверяем, есть ли файлы
        if (collection.size() > 0) {
            showImage((File) iterator.next(), "Fade");
        } else {
            mainImageView.setImage(null);
            counterLabel.setText("0 из 0");
        }
    }

    @FXML
    public void initialize() {
        filterCombo.getItems().addAll("jpg", "png", "all");
        filterCombo.setValue("jpg");

        filterCombo.setOnAction(e -> loadCollection(currentDirectory));

        timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> handleNext()));
        timeline.setCycleCount(Animation.INDEFINITE);

    }

    @FXML
    private void handleAutoAction() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        } else {
            timeline.play();
        }
    }

    @FXML
    private void handleFirst() {
        if (collection.size() > 0) {
            iterator.reset();
            showImage((File) iterator.next(), "Fade");
        }
    }

    @FXML
    private void handleLast() {
        if (collection.size() > 0) {
            File last = null;
            while (iterator.getCurrentIndex() < collection.size() - 1) {
                last = (File) iterator.next();
            }
            showImage(last, "Fade");
        }
    }

    @FXML
    private void handleNext() {
        if (iterator != null && iterator.hasNext()) {
            showImage((File) iterator.next(), "Blur");
        }
    }

    @FXML
    private void handlePreview() {
        if (iterator != null && iterator.haspreview()) {
            showImage((File) iterator.preview(), "Blur");
        }
    }



    private void showImage(File file, String effectType) {
        if (file == null) return;

        if ("Fade".equals(effectType)) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), mainImageView);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.2);
            fadeOut.setOnFinished(e -> {
                Image newImg = ImageLoader.loadFromFile(file);
                mainImageView.setImage(newImg);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), mainImageView);
                fadeIn.setFromValue(0.2);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            Image newImg = ImageLoader.loadFromFile(file);
            mainImageView.setImage(newImg);
        }

        if ("Blur".equals(effectType)) {
            GaussianBlur blur = new GaussianBlur(0);
            mainImageView.setEffect(blur);

            Timeline blurIn = new Timeline(new KeyFrame(Duration.millis(100),
                    new KeyValue(blur.radiusProperty(), 20)));

            blurIn.setOnFinished(e -> {
                Image newImg = ImageLoader.loadFromFile(file);
                mainImageView.setImage(newImg);
                Timeline blurOut = new Timeline(new KeyFrame(Duration.millis(400),
                        new KeyValue(blur.radiusProperty(), 0)));
                blurOut.play();
            });
            blurIn.play();

        } else {
            FadeTransition fade = new FadeTransition(Duration.millis(400), mainImageView);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                Image newImg = ImageLoader.loadFromFile(file);
                mainImageView.setImage(newImg);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), mainImageView);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fade.play();
        }

        int current = iterator.getCurrentIndex() + 1;
        counterLabel.setText(current + " из " + collection.size());
    }

    @FXML
    private void handleInfo() {
        int index = iterator.getCurrentIndex();

        File file = collection.getFile(index);

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("Файл: ").append(file.getName()).append("\n\n");

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    if (tagName.contains("Make") || tagName.contains("Model") ||
                            tagName.contains("Date/Time") || tagName.contains("Exposure Time")) {
                        infoBuilder.append(tagName).append(": ").append(tag.getDescription()).append("\n");
                    }
                }
            }

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Метаданные изображения");
            alert.setHeaderText("Информация EXIF");

            String result = infoBuilder.toString();
            alert.setContentText(result.length() > 5 ? result : "Метаданные для этого файла не найдены.");

            alert.showAndWait();

        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

