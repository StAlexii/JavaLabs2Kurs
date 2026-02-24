package com.example.labameta1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Converter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Converter-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("StadnikLaba1");
        stage.setScene(scene);
        stage.show();
    }
}
