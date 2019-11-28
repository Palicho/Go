module tp.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens tp.client to javafx.fxml;
    exports tp.client;
}