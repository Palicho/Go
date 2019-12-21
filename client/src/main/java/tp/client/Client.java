package tp.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;


public class Client extends Application {

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

        GamePane gamePane = new GamePane(500, 500);
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
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
        launch();
    }

    public class GamePane extends Pane {

        public GamePane(double width, double height) {

            double lineWidthSpace = (width - 50) / 18;
            double lineHeightSpace = (height - 50) / 18;

            for (int i = 0; i < 19; i++) {
                Line line = new Line();
                line.setStartX(lineWidthSpace * i + 25);
                line.setStartY(25);
                line.setEndX(lineWidthSpace * i + 25);
                line.setEndY(height - 25);
                getChildren().add(line);
            }

            for (int i = 0; i < 19; i++) {
                Line line = new Line();
                line.setStartX(25);
                line.setStartY(lineHeightSpace * i + 25);
                line.setEndX(width - 25);
                line.setEndY(lineHeightSpace * i + 25);
                getChildren().add(line);
            }


            for (int i = 0; i < 19; i++) {
                for (int j = 0; j < 19; j++) {
                    MyCircle circle = new MyCircle(i, j);
                    circle.setCenterX(i * lineWidthSpace + 25);
                    circle.setCenterY(j * lineHeightSpace + 25);
                    circle.setRadius(10);
                    circle.setFill(Color.TRANSPARENT);
                    circle.setOnMousePressed(clickHandler);
                    circle.setOnMouseReleased(afterClickHandler);
                    circles[i][j] = circle;
                    getChildren().add(circle);
                }
            }

            ButtonBar buttonBar = new ButtonBar();

            Button pass = new Button("PASS");
            pass.setOnMousePressed(mouseEvent -> {
                localMove = realMove;
                if (localMove) {
                    out.println("PAUSE " + signature);
                    try {
                        waitForResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    canDraw = !gameEnded;
                    Platform.runLater(() -> realMove = false);
                }
            });
            pass.setOnMouseReleased(afterClickHandler);

            Button surrender = new Button("SURRENDER");
            surrender.setOnMousePressed(mouseEvent -> {
                localMove = realMove;
                if (localMove) {
                    out.println("SURRENDER " + signature);
                    try {
                        waitForResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> realMove = false);
                }
            });

            Button singleplayer = new Button("SINGLE");
            singleplayer.setOnMousePressed(mouseEvent -> {
                try {
                    initializeGame(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            singleplayer.setOnMouseReleased(mouseEvent -> {
                try {
                    waitForResponse();
                    buttonBar.getButtons().clear();
                    buttonBar.getButtons().addAll(pass, surrender, textField);
                    Platform.runLater(() -> realMove = true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Button multiplayer = new Button("MULTI");
            multiplayer.setOnMousePressed(mouseEvent -> {
                try {
                    initializeGame(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            multiplayer.setOnMouseReleased(mouseEvent -> {
                try {
                    waitForResponse();
                    if (!localMove) {
                        drawStones();
                        waitForResponse();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonBar.getButtons().clear();
                buttonBar.getButtons().addAll(pass, surrender, textField);
                Platform.runLater(() -> realMove = true);
            });

            textField.setTextAlignment(TextAlignment.CENTER);

            buttonBar.getButtons().add(singleplayer);
            buttonBar.getButtons().add(multiplayer);
            buttonBar.setLayoutY(19 * lineHeightSpace + 25);
            getChildren().add(buttonBar);
            setStyle("-fx-background-color: indianred;");
        }
    }
}
