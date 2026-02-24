package com.example.labameta2;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.Bindings;

public class Laba2Controller {
    @FXML private Pane gamePane;
    @FXML private Circle ball;

    @FXML private BallModel model;
    @FXML private AnimationTimer timer;

    @FXML private boolean paused = false;

    @FXML private Label scoreLabel;

    @FXML private Label pauseLabel;

    private IntegerProperty score = new SimpleIntegerProperty(0);

    public void initialize() {

        model = new BallModel(100, 100, ball.getRadius());
        // Шарик в центре панели
        ball.centerXProperty().bind(model.xProperty());
        ball.centerYProperty().bind(model.yProperty());

        scoreLabel.textProperty().bind(
                Bindings.concat("Счет: ", score)
        );

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!paused) {
                    model.update(
                            gamePane.getWidth(),
                            gamePane.getHeight()
                    );
                }
            }
        };

        timer.start();

        gamePane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                paused = !paused;

                pauseLabel.setVisible(paused);
            }
        });

        // --- Наведение = замедление ---
        ball.setOnMouseEntered(event -> model.slowDown());

        ball.setOnMouseExited(event -> model.normalSpeed());
    }


    @FXML
    private void handleMouseClick(MouseEvent event) {
        score.set(score.get() + 1);
        // Ппроверка попадания
        double dx = event.getX() - ball.getCenterX();
        double dy = event.getY() - ball.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= ball.getRadius()) {
            // При попадании Перемещаем шарик
            moveBallToRandomPosition();
        }
    }

    private void moveBallToRandomPosition() {
        Random random = new Random();
        double newX = ball.getRadius() +
                random.nextDouble() * (gamePane.getWidth() - 2 * ball.getRadius());
        double newY = ball.getRadius() +
                random.nextDouble() * (gamePane.getHeight() - 2 * ball.getRadius());

        ball.setCenterX(newX);
        ball.setCenterY(newY);
    }

}


