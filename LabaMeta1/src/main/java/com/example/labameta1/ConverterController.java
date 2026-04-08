package com.example.labameta1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ConverterController implements Initializable {

    @FXML private TextField inputField;
    @FXML private ComboBox<String> unitCombo;
    @FXML private Label resFontSize;
    @FXML private Label resLineHeight;
    @FXML private Label resKerning;
    @FXML private Label previewLabel;
    @FXML private Label errorLabel;

    private final ConverterModel model = new ConverterModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        unitCombo.getItems().addAll("px", "pt", "em");
        unitCombo.setValue("px");

        errorLabel.setText("");
    }

    class ConverterModel {
        private final double PT_TO_PX = 1.3333;
        private final double GOLDEN_LINE_HEIGHT = 1.5;
        private final double STANDARD_KERNING = 0.05;

        public FontCalc CalcSize(double fontSize, String unit) {

            double sizeInPx = switch (unit) {
                case "pt" -> fontSize * PT_TO_PX;
                case "em" -> fontSize * 16.0;
                default -> fontSize;
            };

            double lineHeight = sizeInPx * GOLDEN_LINE_HEIGHT;
            double kerning = sizeInPx * STANDARD_KERNING;

            return new FontCalc(sizeInPx, lineHeight, kerning);
        }

        public record FontCalc(double fontSizePx, double lineHeightPx, double kerningPx) {}
    }

    @FXML
    private void handleConvert() {
        try {
            errorLabel.setText("");

            String rawInput = inputField.getText().replace(",", ".");
            if (rawInput.isEmpty()) {
                throw new Exception("Введите значение");
            }

            double value = Double.parseDouble(rawInput);
            String unit = unitCombo.getValue();

            ConverterModel.FontCalc profile = model.CalcSize(value, unit);

            resFontSize.setText(String.format("%.2f px", profile.fontSizePx()));
            resLineHeight.setText(String.format("%.2f px", profile.lineHeightPx()));
            resKerning.setText(String.format("%.2f px", profile.kerningPx()));

            double extraLineSpacing = profile.lineHeightPx() - profile.fontSizePx();

            String style = String.format(
                    "-fx-font-size: %.2fpx; -fx-line-spacing: %.2fpx; -fx-letter-spacing: %.2fpx;",
                    profile.fontSizePx(),
                    extraLineSpacing,
                    profile.kerningPx()
            );
            previewLabel.setStyle(style);

        } catch (NumberFormatException e) {
            errorLabel.setText("Ошибка: введите число");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}