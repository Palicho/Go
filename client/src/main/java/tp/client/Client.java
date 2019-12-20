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

    MyCircle[][] circles = new MyCircle[19][19];
    boolean move = false;
    boolean realMove = false;
    boolean draw = false;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;
    Color color;
    Text field = new Text();
    LinkedHashMap<MyCircle, Color> changedColor = new LinkedHashMap<>();

    static EventHandler<MouseEvent> clickHandler;
    static EventHandler<MouseEvent> afterClickHandler;

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
            //todo: change to if and go to loading screen
            response = in.readLine();
        }
        if (response.matches("READY [BW]")) {
            signature = response.charAt(6);
            color = signature == 'B' ? Color.BLACK : Color.WHITE;
            //move = signature == 'B';
        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    public void waitForResponse() throws IOException {
        String serverMsg = in.readLine();
        String[] results = serverMsg.split(" ");
        if (serverMsg.equals("MOVE")) {
            move = true;

        } else if (serverMsg.startsWith("END")) {
            int[] scores = new int[2];
            scores[0] = Integer.parseInt(results[2]);
            scores[1] = Integer.parseInt(results[4]);
            char winner;
            if (scores[0] == scores[1]) field.setText("TIE: "+scores[0] + " points");//System.out.println("TIE (" + scores[0] + " points)");
            else {
                winner = scores[0] > scores[1] ? 'B' : 'W';
                if (winner == signature) field.setText("YOU WON");//System.out.println("YOU WON!");
                else field.setText("YOU LOST");//System.out.println("YOU LOST!");
                System.out.println("black score: " + scores[0]);
                System.out.println("white score: " + scores[1]);
            }

        } else if (serverMsg.startsWith("SURRENDER")) {
            char loser = results[1].charAt(0);
            if (loser == signature) field.setText(serverMsg);//System.out.println("YOU HAVE SURRENDERED!");
            else field.setText(serverMsg);//System.out.println("YOUR OPPONENT HAS SURRENDERED!");

        } else if (serverMsg.startsWith("B ") || serverMsg.startsWith("W ")) {
            System.out.println(serverMsg);
            field.setText(serverMsg);
            Color c = results[0].equals("W") ? Color.WHITE : Color.BLACK;
            int x, y;
            x = Integer.parseInt(results[1]);
            y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], c);
            waitForResponse();

        } else if (serverMsg.startsWith("PAUSE ")) {
            System.out.println(serverMsg);
            field.setText(serverMsg);
            waitForResponse();

        } else if (serverMsg.startsWith("REMOVE ")) {
            int x = Integer.parseInt(results[1]);
            int y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], Color.TRANSPARENT);
            System.out.println(serverMsg);
            waitForResponse();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = new Socket("localhost", 9100);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        clickHandler = mouseEvent -> {
            move = realMove;
            System.out.println(move);
            if (move) {
                move = false;
                MyCircle circle = (MyCircle) mouseEvent.getSource();
                try {
                    if (circle.canClick()) {
                        out.println(signature + " " + circle.getX() + " " + circle.getY());
                        waitForResponse();
                        if (!move) {
                            drawStones();
                            draw = true;
                        }
                    } else move = true;
                    Platform.runLater(() -> realMove = move);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else System.out.println("Nie dalem zakolejkowac");
        };

        afterClickHandler = mouseEvent -> {
            if (draw) {
                draw = false;
                try {
                    waitForResponse();
                    drawStones();
                    waitForResponse();
                    Platform.runLater(() -> realMove = true);
                } catch (IOException e) {
                    System.exit(1);
                }
            }
        };

        GamePane gamePane = new GamePane(500, 500);
        Scene scene = new Scene(gamePane, 500, 600);
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
                move = realMove;
                if (move) {
                    out.println("PAUSE "+signature);
                    try {
                        waitForResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    draw = true;
                    Platform.runLater(() -> realMove = false);
                }
            });
            pass.setOnMouseReleased(afterClickHandler);
            Button surrender = new Button("SURRENDER");
            surrender.setOnMousePressed(mouseEvent -> {
                move = realMove;
                if (move) {
                    out.println("SURRENDER "+signature);
                    try {
                        waitForResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> realMove = false);
                }
            });
            Button single = new Button("SINGLE");
            single.setOnMousePressed(mouseEvent -> {
                try {
                    initializeGame(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            single.setOnMouseReleased(mouseEvent -> {
                try {
                    waitForResponse();
                    buttonBar.getButtons().clear();
                    buttonBar.getButtons().addAll(pass,surrender,field);
                    Platform.runLater(() -> realMove = true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Button start = new Button("MULTI");
            start.setOnMousePressed(mouseEvent -> {
                try {
                    initializeGame(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            start.setOnMouseReleased(mouseEvent -> {
                try {
                    waitForResponse();
                    if (!move) {
                        drawStones();
                        waitForResponse();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonBar.getButtons().clear();
                buttonBar.getButtons().addAll(pass,surrender,field);
                Platform.runLater(() -> realMove = true);
            });
            field.setTextAlignment(TextAlignment.CENTER);
            buttonBar.getButtons().add(single);
            buttonBar.getButtons().add(start);
            buttonBar.setLayoutY(19 * lineHeightSpace + 25);
            getChildren().add(buttonBar);
            setStyle("-fx-background-color: indianred;");
        }
    }
}
