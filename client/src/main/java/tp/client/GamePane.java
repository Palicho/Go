package tp.client;

import javafx.application.Platform;
import javafx.scene.control.Button;

import java.io.IOException;

public class GamePane extends BoardPane {

    public GamePane(Client client, double width, double height) {
        super(client,width,height);

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                client.circles[i][j].setOnMousePressed(client.clickHandler);
                client.circles[i][j].setOnMouseReleased(client.afterClickHandler);
            }
        }

        Button pass = new Button("PASS");
        pass.setPrefSize(width-height-20,40);
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
        surrender.setPrefSize(width-height-20,40);
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

        this.vBox.getChildren().remove(client.textField);
        this.vBox.getChildren().addAll(pass,surrender,client.textField);
    }
}
