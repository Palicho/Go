package tp.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ModeMenuController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    public void startSignleplayer(ActionEvent actionEvent) {

    }

    public void startMultiplayer(ActionEvent actionEvent) {

    }

    public void returnToMainMenu(ActionEvent actionEvent) throws IOException {
        Client.setRoot("mainMenu");
    }

}
