package com.example.labameta4;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import core.DataProcessor;

public class Laba4Controller {

    @FXML private TextArea inputTextArea;
    @FXML private TextArea resultsListView;
    @FXML private TextField shiftField;

    @FXML
    private void handleProcess() {
        try {
            String rawText = inputTextArea.getText();
            String[] tokens = rawText.split("\\s+");
            int k = Integer.parseInt(shiftField.getText());

            String[] result = DataProcessor.processPipeline(tokens, k);

            String resultText = String.join(" ", result);
            resultsListView.setText(resultText);

        } catch (NumberFormatException e) {
            showError("Введите целое число");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}

