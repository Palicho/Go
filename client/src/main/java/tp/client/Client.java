package tp.client;

import java.io.*;
import java.net.*;

import static java.lang.System.exit;

public class Client {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;

    private Client(int port) throws IOException {
        socket = new Socket("localhost", port);
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void initializeGame(boolean bot) throws IOException {
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
            exit(1);
        }
    }

    private boolean gameCourse() throws IOException {
        String serverMsg;
        while (true) {
            serverMsg = in.readLine();
            if (serverMsg.equals("MOVE")) {
                //todo: move
            } else if (serverMsg.startsWith("END")) {
                String[] results = serverMsg.split(" ");
                int[] scores = new int[2];
                scores[0] = Integer.parseInt(results[2]);
                scores[1] = Integer.parseInt(results[4]);
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
                return (loser != signature);
            } else if (serverMsg.startsWith("B ") || serverMsg.startsWith("W ")) {
                //todo: mark the move on your board
            }
        }
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(9100);
            c.initializeGame(true);
            //c.initializeGame(false);
            c.gameCourse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}