package tp.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    public void switchToLoadMenu(ActionEvent actionEvent) throws IOException {

    }

    public void switchToModeMenu(ActionEvent actionEvent) throws IOException {
        Client.setRoot("modeMenu");
    }

    public void exit(ActionEvent actionEvent) {
    }
}