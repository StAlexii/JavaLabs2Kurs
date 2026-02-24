package com.example.labameta1;

import javafx.application.Application;

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

    // Вспомогательный класс для хранения результатов
    public record FontCalc(double fontSizePx, double lineHeightPx, double kerningPx) {}
}


public class Launcher
{
    public static void main(String[] args)
    {
        Application.launch(Converter.class, args);

    }
}



