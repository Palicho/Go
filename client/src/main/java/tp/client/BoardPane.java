package tp.client;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import static java.lang.StrictMath.min;

public class BoardPane extends Pane {
    protected VBox vBox;

    public BoardPane(Client client, double width, double height) {
        double size= min(width,height);
        double lineWidthSpace = (size - 50) / 18;
        double lineHeightSpace = (size - 50) / 18;
        vBox = new VBox();
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
                client.circles[i][j] = circle;
                getChildren().add(circle);
            }
        }

        client.textField.setTextAlignment(TextAlignment.CENTER);
        client.textField.setWrappingWidth(width-height-20);
        vBox.getChildren().add(client.textField);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(19*lineWidthSpace+25);
        vBox.setLayoutY(20);

        getChildren().add(vBox);
    }
}
