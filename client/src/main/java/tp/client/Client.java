package tp.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;


public class Client extends Application {

    private static Scene scene;
    private static GamePane gamePane;

    boolean localMove = false;
    boolean realMove = false;
    boolean canDraw = false;
    boolean gameEnded = false;

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;

    Color color;
    Text textField = new Text();
    LinkedHashMap<MyCircle, Color> changedColor = new LinkedHashMap<>();
    MyCircle[][] circles = new MyCircle[19][19];

    EventHandler<MouseEvent> clickHandler;
    EventHandler<MouseEvent> afterClickHandler;

    void drawStones() {
        Color color;
        for (MyCircle circle : changedColor.keySet()) {
            color = changedColor.get(circle);
            circle.setColor(color);
        }
        changedColor.clear();
    }

    void initializeGame(boolean bot) throws IOException {
        if (bot) out.println("START 1");
        else out.println("START 2");
        String response = in.readLine();

        if (response.equals("WAITING")) {
            response = in.readLine();

        }
        if (response.matches("READY [BW]")) {
            signature = response.charAt(6);
            color = signature == 'B' ? Color.BLACK : Color.WHITE;

        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    public void waitForResponse() throws IOException {
        String serverMsg = in.readLine();
        String[] results = serverMsg.split(" ");

        if (serverMsg.equals("MOVE")) {
            localMove = true;

        } else if (serverMsg.startsWith("END")) {
            int[] scores = new int[2];
            scores[0] = Integer.parseInt(results[2]);
            scores[1] = Integer.parseInt(results[4]);

            if (scores[0] == scores[1])
                textField.setText("TIE: " + scores[0] + " points");
            else {
                String finalMessage = "YOU ";
                char winner = scores[0] > scores[1] ? 'B' : 'W';
                if (winner == signature) finalMessage += "WON\n";
                else finalMessage += "LOST\n";
                finalMessage += ("black score: " + scores[0] + "\n");
                finalMessage += ("white score: " + scores[1] + "\n");
                textField.setText(finalMessage);
            }
            gameEnded = true;

        } else if (serverMsg.startsWith("SURRENDER")) {
            char loser = results[1].charAt(0);
            gameEnded = true;
            if (loser == signature) textField.setText("YOU HAVE SURRENDERED");
            else textField.setText("YOUR OPPONENT HAS SURRENDERED");

        } else if (serverMsg.startsWith("B ") || serverMsg.startsWith("W ")) {
            textField.setText(serverMsg);
            Color c = results[0].equals("W") ? Color.WHITE : Color.BLACK;
            int x, y;
            x = Integer.parseInt(results[1]);
            y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], c);
            waitForResponse();

        } else if (serverMsg.startsWith("PAUSE ")) {
            textField.setText(serverMsg);
            waitForResponse();

        } else if (serverMsg.startsWith("REMOVE ")) {
            int x = Integer.parseInt(results[1]);
            int y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], Color.TRANSPARENT);
            waitForResponse();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = new Socket("localhost", 9100);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        clickHandler = mouseEvent -> {
            localMove = realMove;
            if (localMove) {
                localMove = false;
                MyCircle circle = (MyCircle) mouseEvent.getSource();
                try {
                    if (circle.canClick()) {
                        out.println(signature + " " + circle.getX() + " " + circle.getY());
                        waitForResponse();
                        if (!localMove) {
                            drawStones();
                            canDraw = true;
                        }
                    } else localMove = true;
                    Platform.runLater(() -> realMove = localMove);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        afterClickHandler = mouseEvent -> {
            if (canDraw) {
                canDraw = false;
                try {
                    waitForResponse();
                    drawStones();
                    if (!gameEnded) waitForResponse();
                    Platform.runLater(() -> realMove = true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        scene = new Scene(loadFXML("mainMenu"), 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
/*
        gamePane = new GamePane(500, 500);
        Scene scene = new Scene(gamePane, 500, 550);
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                in.close();
                out.close();
                socket.close();
                Platform.exit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();*/
    }


    public static void main(String[] args) throws IOException {
        launch();
    }


    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public void newGame(){

        Stage stage= new Stage();
        gamePane = new GamePane(this, 500, 500);
        Scene scene = new Scene(gamePane, 500, 550);
        stage.setOnCloseRequest(windowEvent -> {
            try {
                in.close();
                out.close();
                socket.close();
                Platform.exit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.show();
    }

}
