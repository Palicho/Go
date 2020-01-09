package tp.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

import static java.lang.StrictMath.min;

public class GamePane extends Pane {

    private final Client client;
    private VBox vBox;

    public GamePane(Client client, double width, double height) {


        this.client = client;

        double size= min(width,height);
        double lineWidthSpace = (size - 50) / 18;
        double lineHeightSpace = (size - 50) / 18;
        vBox= new VBox();

        Rectangle rectangle = new Rectangle(lineWidthSpace*19,lineWidthSpace*19);

        rectangle.setX(12.5);
        rectangle.setY(12.5);
        rectangle.setFill(Color.INDIANRED);
        getChildren().add(rectangle);

        for (int i = 0; i < 19; i++) {
            Line line = new Line();
            line.setStartX(lineWidthSpace * i + 25);
            line.setStartY(25);
            line.setEndX(lineWidthSpace * i + 25);
            line.setEndY(size - 25);
            getChildren().add(line);
        }

        for (int i = 0; i < 19; i++) {
            Line line = new Line();
            line.setStartX(25);
            line.setStartY(lineHeightSpace * i + 25);
            line.setEndX(size - 25);
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



        Button pass = new Button("PASS");
        pass.setPrefSize(width-height-20,20);
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
        surrender.setPrefSize(width-height-20,20);
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
        client.textField.setTextAlignment(TextAlignment.CENTER);
        vBox.getChildren().addAll(pass,surrender, client.textField);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(19*lineWidthSpace+30);
        vBox.setLayoutY(20);

        getChildren().add(vBox);
        //setStyle("-fx-background-color: indianred;");
    }
}
