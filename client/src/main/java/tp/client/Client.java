package tp.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Application {

    ArrayList<MyCircle> circles= new ArrayList<>();
    boolean move=false;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;


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
        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    public boolean gameCourse() throws IOException {
        String serverMsg;
        Scanner input = new Scanner(System.in);
        while (true) {
            serverMsg = in.readLine();
            if (serverMsg.equals("MOVE")) {
                //todo: move
                move=true;
                System.out.print("Your move ("+signature+" X Y): ");
            } else if (serverMsg.startsWith("END")) {
                String[] results = serverMsg.split(" ");
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
                    return (winner == signature);
                }
            } else if (serverMsg.startsWith("SURRENDER")) {
                char loser = serverMsg.charAt(10);
                if (loser == signature) System.out.println("YOU HAVE SURRENDERED!");
                else System.out.println("YOUR OPPONENT HAS SURRENDERED!");
                in.close();
                out.close();
                socket.close();
                return (loser != signature);
            } else if (serverMsg.startsWith("B ") || serverMsg.startsWith("W ")) {
                //todo: mark the move on your board
                System.out.println(serverMsg);
            } else if (serverMsg.startsWith("REMOVE ")) {
                //todo: remove the stone from your board
                System.out.println(serverMsg);
            } else if (serverMsg.startsWith("PAUSE ")) {
                System.out.println(serverMsg);
            }
        }
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = new Socket("localhost", 9100);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
/*
    public static void main(String[] args) {
        try {
            Client c = new Client(9100);
            boolean bot = Boolean.parseBoolean(args[0]);
            c.initializeGame(bot);
            c.gameCourse();
        } catch (IOException e) {
            System.exit(1);
        }
    }*/

    public class GamePane extends Pane {


        public GamePane(double width, double height) {

            double lineWidthSpace = (width-50)/18;
            double lineHeightSpace = (height-50)/18;

            for(int i =0; i<19; i++){
                Line line = new Line();
                line.setStartX(lineWidthSpace*i+25);
                line.setStartY(25);
                line.setEndX(lineWidthSpace*i+25);
                line.setEndY(height-25);
                getChildren().add(line);
            }

            for(int i =0; i<19; i++){
                Line line = new Line();
                line.setStartX(25);
                line.setStartY(lineHeightSpace*i+25);
                line.setEndX(width-25);
                line.setEndY(lineHeightSpace*i+25);
                getChildren().add(line);
            }


            for( int i= 0;i<19;i++){
                for(int j =0; j<19;j++){
                    MyCircle circle = new MyCircle(i,j);
                    circle.setCenterX(i*lineWidthSpace+25);
                    circle.setCenterY(j*lineHeightSpace+25);
                    circle.setRadius(10);
                    circle.setFill(Color.RED);
                    circle.setOnMouseClicked( new EventHandler<MouseEvent>(){
                        public void handle(MouseEvent e){
                            if(move){
                                out.print( circle.getX()+ " " + circle.getY());
                                move=false;
                            }

                        }
                    });
                    circles.add(circle);
                }
            }
            getChildren().addAll(circles);

        }
    }
}
