package tp.client;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPane extends GridPane {

    private Button newGameButton, loadGameButton, exitButton;
    private VBox vBox;
    private Client client;

    public MenuPane(Client client, double width, double height){

        this.client= client;

        newGameButton= new Button("NEW GAME");
        loadGameButton = new Button("LOAD GAME");
        exitButton = new Button("EXIT");

        setPrefSize(width,height);
        add(newGameButton,0,0);
        add(loadGameButton,0,1);
        add(exitButton,0,2);
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
