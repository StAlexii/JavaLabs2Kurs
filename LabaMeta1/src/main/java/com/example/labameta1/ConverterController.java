package com.example.labameta1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ConverterController implements Initializable {

    // Ссылки на элементы из FXML (имена должны совпадать с fx:id)
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
        // Настраиваем выпадающий список
        unitCombo.getItems().addAll("px", "pt", "em");
        unitCombo.setValue("px");

        // Очищаем текст ошибки при запуске
        errorLabel.setText("");
    }

    @FXML
    private void handleConvert() {
        try {
            // 1. Сброс предыдущих ошибок
            errorLabel.setText("");

            // 2. Получение и валидация данных
            String rawInput = inputField.getText().replace(",", ".");
            if (rawInput.isEmpty()) {
                throw new Exception("Введите значение");
            }

            double value = Double.parseDouble(rawInput);
            String unit = unitCombo.getValue();

            // 3. Обращение к модели за расчетами
            ConverterModel.FontCalc profile = model.CalcSize(value, unit);

            // 4. Обновление интерфейса результатами
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