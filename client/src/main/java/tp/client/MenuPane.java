package tp.client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPane extends VBox {

    private Button newGameButton, loadGameButton, exitButton;
    private Client client;

    public MenuPane(Client client, double width, double height){

        this.client= client;

        newGameButton= new Button("NEW GAME");
        newGameButton.setPrefSize(width*0.75, height*0.1);
        loadGameButton = new Button("LOAD GAME");
        loadGameButton.setPrefSize(width*0.75, height*0.1);

        exitButton = new Button("EXIT");
        exitButton.setPrefSize(width*0.75, height*0.1);

        setPrefSize(width,height);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        getChildren().addAll(newGameButton,loadGameButton,exitButton);
        newGameButton.setOnMouseClicked(mouseEvent -> {
            getScene().setRoot(new ModePane(client,width,height));
        });

        /*
        loadGameButton.setOnMouseClicked(mouseEvent -> {
            try {
                client.initializeGame(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        */

        exitButton.setOnMouseClicked(mouseEvent -> {
            try {
                client.close();
                Stage stage =(Stage)getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

