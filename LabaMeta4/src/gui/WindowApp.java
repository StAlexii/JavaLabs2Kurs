package gui;

import core.DataProcessor;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WindowApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Введите числа через пробел...");

        TextField shiftField = new TextField("3"); // k по умолчанию
        shiftField.setPromptText("Сдвиг (k)");

        Button processBtn = new Button("Обработать");
        ListView<String> outputList = new ListView<>();

        processBtn.setOnAction(e -> {
            String text = inputArea.getText();
            String[] tokens = text.split("\\s+");
            int k = Integer.parseInt(shiftField.getText());

            String[] result = DataProcessor.processPipeline(tokens, k);

            outputList.getItems().clear();
            outputList.getItems().addAll(result);
        });

        VBox root = new VBox(10, new Label("Входные данные:"), inputArea,
                new Label("Сдвиг:"), shiftField,
                processBtn, new Label("Результат:"), outputList);
        root.setPadding(new Insets(15));

        primaryStage.setTitle("Data Processor Lab");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}