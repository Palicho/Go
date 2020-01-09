package tp.client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.ref.Cleaner;

public class ModePane extends GridPane {
    private Button singleplayerButton , multiplayerButton, returnButton;

    public ModePane(Client client, double width, double height){
        singleplayerButton=new Button("SINGLEPLAYER");
        multiplayerButton = new Button("MULTIPLAYER");
        returnButton = new Button("RETURN");

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

        add(singleplayerButton, 0,0);
        add(multiplayerButton,0,1);

    }

    public void newGameWindow(Client client, double width, double height){

        getScene().setRoot(new GamePane(client, width, height));
    }

}
