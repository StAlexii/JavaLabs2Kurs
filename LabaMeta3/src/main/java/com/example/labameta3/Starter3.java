package com.example.labameta3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Starter3 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Starter3.class.getResource("Laba3view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 640);
        stage.setTitle("Laba3");
        stage.setScene(scene);
        stage.show();
    }
}
