package tp.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.ref.Cleaner;

public class ModePane extends VBox {
    private Button singleplayerButton , multiplayerButton, returnButton;

    public ModePane(Client client, double width, double height){
        singleplayerButton=new Button("SINGLEPLAYER");
        singleplayerButton.setPrefSize(width*0.75,height*0.1);

        multiplayerButton = new Button("MULTIPLAYER");
        multiplayerButton.setPrefSize(width*0.75, height*0.1);
        returnButton = new Button("RETURN");
        returnButton.setPrefSize(width*0.75, height*0.1);
        setAlignment(Pos.CENTER);
        setSpacing(20);

        singleplayerButton.setOnMousePressed(mouseEvent -> {
            try {
                client.initializeGame(true);
                newGameWindow(client, width, height);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        singleplayerButton.setOnMouseReleased(mouseEvent -> {
            try {
                client.waitForResponse();
                Platform.runLater(() -> client.realMove = true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        multiplayerButton.setOnMousePressed(mouseEvent -> {
            try {
                client.initializeGame(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        multiplayerButton.setOnMouseReleased(mouseEvent -> {
            try {
                newGameWindow(client,width,height);
                client.waitForResponse();
                if (!client.localMove) {
                    client.drawStones();
                    client.waitForResponse();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> client.realMove = true);
        });

        returnButton.setOnMouseClicked(event -> {
            getScene().setRoot(new MenuPane(client, height,width));
        });

        getChildren().addAll(singleplayerButton, multiplayerButton, returnButton);
    }

    public void newGameWindow(Client client, double width, double height){

        getScene().setRoot(new GamePane(client, width, height));
    }

}
