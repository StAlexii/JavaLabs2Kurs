package com.example.labameta4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Laba4Starter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Laba4Starter.class.getResource("Laba4-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 540);
        stage.setTitle("LabaMeta4");
        stage.setScene(scene);
        stage.show();
    }
}
