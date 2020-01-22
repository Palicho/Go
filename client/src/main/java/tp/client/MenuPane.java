package tp.client;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPane extends VBox {

    private Button newGameButton, loadGameButton, exitButton;
    private Text text;
    private Label label;
    private Client client;

    public MenuPane(Client client, double width, double height){

        this.client= client;

        label= new Label("GO");

        label.setPrefSize(width*0.75, height*0.3);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(100));
        newGameButton= new Button("NEW GAME");
        newGameButton.setPrefSize(width*0.75, height*0.1);
        loadGameButton = new Button("LOAD GAME");
        loadGameButton.setPrefSize(width*0.75, height*0.1);

        exitButton = new Button("EXIT");
        exitButton.setPrefSize(width*0.75, height*0.1);

        setPrefSize(width,height);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        getChildren().addAll(label,newGameButton,loadGameButton,exitButton);
        newGameButton.setOnMouseClicked(mouseEvent -> {
            getScene().setRoot(new ModePane(client,width,height));
        });

        loadGameButton.setOnMouseClicked(mouseEvent -> {
            try {
                getScene().setRoot(new BoardPane(client,width,height));
                client.loadGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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

