package tp.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
        setSpacing(10);

        singleplayerButton.setOnMousePressed(mouseEvent -> {
            try {
                newGameWindow(client, width, height);

                client.initializeGame(true);

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

            newGameWindow(client,width,height);
            try {
                client.initializeGame(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        multiplayerButton.setOnMouseReleased(mouseEvent -> {
            try {

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

    public void waiting(){
        getScene().setRoot(new Pane());
    }

}
