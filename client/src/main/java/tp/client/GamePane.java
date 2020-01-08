package tp.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class GamePane extends Pane {

    private final Client client;

    public GamePane(Client client, double width, double height) {
        this.client = client;

        double lineWidthSpace = (width - 50) / 18;
        double lineHeightSpace = (height - 50) / 18;

        for (int i = 0; i < 19; i++) {
            Line line = new Line();
            line.setStartX(lineWidthSpace * i + 25);
            line.setStartY(25);
            line.setEndX(lineWidthSpace * i + 25);
            line.setEndY(height - 25);
            getChildren().add(line);
        }

        for (int i = 0; i < 19; i++) {
            Line line = new Line();
            line.setStartX(25);
            line.setStartY(lineHeightSpace * i + 25);
            line.setEndX(width - 25);
            line.setEndY(lineHeightSpace * i + 25);
            getChildren().add(line);
        }


        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                MyCircle circle = new MyCircle(i, j);
                circle.setCenterX(i * lineWidthSpace + 25);
                circle.setCenterY(j * lineHeightSpace + 25);
                circle.setRadius(10);
                circle.setFill(Color.TRANSPARENT);
                circle.setOnMousePressed(client.clickHandler);
                circle.setOnMouseReleased(client.afterClickHandler);
                client.circles[i][j] = circle;
                getChildren().add(circle);
            }
        }

        ButtonBar buttonBar = new ButtonBar();

        Button pass = new Button("PASS");
        pass.setOnMousePressed(mouseEvent -> {
            client.localMove = client.realMove;
            if (client.localMove) {
                client.out.println("PAUSE " + client.signature);
                try {
                    client.waitForResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.canDraw = !client.gameEnded;
                Platform.runLater(() -> client.realMove = false);
            }
        });
        pass.setOnMouseReleased(client.afterClickHandler);

        Button surrender = new Button("SURRENDER");
        surrender.setOnMousePressed(mouseEvent -> {
            client.localMove = client.realMove;
            if (client.localMove) {
                client.out.println("SURRENDER " + client.signature);
                try {
                    client.waitForResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> client.realMove = false);
            }
        });

        Button singleplayer = new Button("SINGLE");
        singleplayer.setOnMousePressed(mouseEvent -> {
            try {
                client.initializeGame(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        singleplayer.setOnMouseReleased(mouseEvent -> {
            try {
                client.waitForResponse();
                buttonBar.getButtons().clear();
                buttonBar.getButtons().addAll(pass, surrender, client.textField);
                Platform.runLater(() -> client.realMove = true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button multiplayer = new Button("MULTI");
        multiplayer.setOnMousePressed(mouseEvent -> {
            try {
                client.initializeGame(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        multiplayer.setOnMouseReleased(mouseEvent -> {
            try {
                client.waitForResponse();
                if (!client.localMove) {
                    client.drawStones();
                    client.waitForResponse();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            buttonBar.getButtons().clear();
            buttonBar.getButtons().addAll(pass, surrender, client.textField);
            Platform.runLater(() -> client.realMove = true);
        });

        client.textField.setTextAlignment(TextAlignment.CENTER);

        buttonBar.getButtons().add(singleplayer);
        buttonBar.getButtons().add(multiplayer);
        buttonBar.setLayoutY(19 * lineHeightSpace + 25);
        getChildren().add(buttonBar);
        setStyle("-fx-background-color: indianred;");
    }
}
