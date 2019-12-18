package tp.client;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Client extends Application {

    MyCircle[][] circles = new MyCircle[19][19];
    static volatile boolean move = false;
    static volatile boolean draw = false;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;
    Color color;
    LinkedHashMap<MyCircle, Color> changedColor = new LinkedHashMap<>();

    static EventHandler<MouseEvent> clickHandler;
    static EventHandler<MouseEvent> afterClickHandler;

    void drawStones() {
        Color color;
        for (MyCircle circle : changedColor.keySet()) {
            color = changedColor.get(circle);
            circle.setColor(color);
            /*
            if (color == Color.TRANSPARENT) {
                circle.addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
                circle.addEventHandler(MouseEvent.MOUSE_CLICKED, afterClickHandler);
            }
            else {
                circle.removeEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
                circle.removeEventHandler(MouseEvent.MOUSE_CLICKED, afterClickHandler);
            } */
        }
        //changedColor.clear();
    }

    void updateStoneHandlers() {
        Color color;
        for (MyCircle circle : changedColor.keySet()) {
            color = changedColor.get(circle);
            if (color == Color.TRANSPARENT) {
                circle.addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
                circle.addEventHandler(MouseEvent.MOUSE_CLICKED, afterClickHandler);
            } else {
                circle.removeEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
                circle.removeEventHandler(MouseEvent.MOUSE_CLICKED, afterClickHandler);
            }
        }
        changedColor.clear();
    }

    void switchClicking(EventHandler<MouseEvent> handler) {
        for (int i=0; i<19; i++)
            for (int j=0; j<19; j++) circles[i][j].setOnMousePressed(handler);
    }

    void switchDrawing(EventHandler<MouseEvent> handler) {
        for (int i=0; i<19; i++)
            for (int j=0; j<19; j++) circles[i][j].setOnMouseReleased(handler);
    }

    void initializeGame(boolean bot) throws IOException {
        if (bot) out.println("START 1");
        else out.println("START 2");
        String response = in.readLine();
        while (response.equals("WAITING")) {
            //todo: change to if and go to loading screen
            response = in.readLine();
        }
        if (response.matches("READY [BW]")) {
            signature = response.charAt(6);
            color = signature == 'B' ? Color.BLACK : Color.WHITE;
            move = signature == 'B';
        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    public void waitForResponse() throws IOException {
        String serverMsg = in.readLine();
        String[] results = serverMsg.split(" ");
        if (serverMsg.equals("MOVE")) {
            //todo: move
            move = true;
            //System.out.print("Your move (" + signature + " X Y): ");
        } else if (serverMsg.startsWith("END")) {
            int[] scores = new int[2];
            scores[0] = Integer.parseInt(results[2]);
            scores[1] = Integer.parseInt(results[4]);
            in.close();
            out.close();
            socket.close();
            char winner;
            if (scores[0] == scores[1]) System.out.println("TIE (" + scores[0] + " points)");
            else {
                winner = scores[0] > scores[1] ? 'B' : 'W';
                if (winner == signature) System.out.println("YOU WON!");
                else System.out.println("YOU LOST!");
                System.out.println("black score: " + scores[0]);
                System.out.println("white score: " + scores[1]);
            }
        } else if (serverMsg.startsWith("SURRENDER")) {
            //char loser = serverMsg.charAt(10);
            char loser = results[1].charAt(0);
            if (loser == signature) System.out.println("YOU HAVE SURRENDERED!");
            else System.out.println("YOUR OPPONENT HAS SURRENDERED!");
            in.close();
            out.close();
            socket.close();
        } else if (serverMsg.startsWith("B ") || serverMsg.startsWith("W ")) {
            //todo: mark the move on your board
            System.out.println(serverMsg);
            Color c = results[0].equals("W") ? Color.WHITE : Color.BLACK;
            int x, y;
            x = Integer.parseInt(results[1]);
            y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], c);
            //circles[x][y].setColor(c);
            //circles[x][y].removeEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            waitForResponse();
        } else if (serverMsg.startsWith("PAUSE ")) {
            System.out.println(serverMsg);
        } else if (serverMsg.startsWith("REMOVE ")) {
            int x = Integer.parseInt(results[1]);
            int y = Integer.parseInt(results[2]);
            changedColor.remove(circles[x][y]);
            changedColor.put(circles[x][y], Color.TRANSPARENT);
            //circles[x][y].setColor(Color.CORAL);
            //circles[x][y].setOnMouseClicked(clickHandler);
            System.out.println(serverMsg);
            waitForResponse();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = new Socket("localhost", 9100);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        clickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
               if (move) {
                   move = false;
                //if (canMove()) {
                    //setMove(false);
                    //switchClicking(e -> {System.out.println("ZATRZYMANY");});
                    try {
                        MyCircle c = (MyCircle) mouseEvent.getSource();
                        out.println(signature + " " + c.getX() + " " + c.getY());
                        //move = false;
                        waitForResponse();
                        if (!move) {
                            drawStones();
                            draw = true;
                            //switchDrawing(afterClickHandler);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                //else System.out.println("Nie dalem zakolejkowac");
            }
        };
        afterClickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (draw) {
                    //setMove(false);
                    System.out.println(move);
                    draw = false;
                    //switchDrawing(null);
                    try {
                        waitForResponse();
                        drawStones();
                        updateStoneHandlers();
                        waitForResponse();
                        //switchClicking(clickHandler);
                        System.out.println("JUZNIE");
                    } catch (IOException e) {
                        System.exit(1);
                    }
                }
            }
        };
        GamePane gamePane = new GamePane(500, 500);
        Scene scene = new Scene(gamePane, 500, 500);
        scene.setFill(Color.INDIANRED);
        primaryStage.setScene(scene);
        primaryStage.show();
        /*
        canMoveProperty().addListener((observableValue, was, is) -> {
            if (!is) scene.addEventFilter(MouseEvent.ANY, Event::consume);
            else scene.removeEventFilter(MouseEvent.ANY, Event::consume);
        });
         */
        initializeGame(true);
        waitForResponse();
        if (!move) {
            drawStones();
            updateStoneHandlers();
            waitForResponse();
        }
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
                    //circle.addEventFilter(MouseEvent.MOUSE_PRESSED, clickHandler);
                    //circle.addEventFilter(MouseEvent.MOUSE_RELEASED, afterClickHandler);
                    circle.setOnMousePressed(clickHandler);
                    circle.setOnMouseReleased(afterClickHandler);
                    circles[i][j] = circle;
                    getChildren().add(circle);
                }
            }
        }
    }
}
