package com.example.labameta2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BallModel {

    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    private double dx = 3;
    private double dy = 3;

    private double speedMultiplier = 0.8;

    private final double radius;

    public BallModel(double startX, double startY, double radius) {
        this.x.set(startX);
        this.y.set(startY);
        this.radius = radius;
    }

    public void update(double width, double height) {

        x.set(x.get() + dx * speedMultiplier);
        y.set(y.get() + dy * speedMultiplier);

        if (x.get() <= radius || x.get() >= width - radius) {
            dx = -dx;
        }

        if (y.get() <= radius || y.get() >= height - radius) {
            dy = -dy;
        }
    }

    public void slowDown() {
        speedMultiplier = 0.4;
    }

    public void normalSpeed() {
        speedMultiplier = 0.8;
    }

    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }

    public double getRadius() { return radius; }
}