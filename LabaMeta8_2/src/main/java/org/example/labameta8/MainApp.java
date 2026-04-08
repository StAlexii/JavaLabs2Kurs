package org.example.labameta8;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Сначала запускаем твой индивидуальный эхо-тест
        AudioTester.runEchoTest();

        // 2. Загружаем GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("P2P Voice Call");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
