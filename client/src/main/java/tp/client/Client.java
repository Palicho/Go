package tp.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    char signature;

    Client(int port) throws IOException {
        socket = new Socket("localhost", port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    private boolean gameCourse() throws IOException {
        String serverMsg;
        Scanner input = new Scanner(System.in);
        while (true) {
            serverMsg = in.readLine();
            if (serverMsg.equals("MOVE")) {
                //todo: move
                System.out.print("Your move ("+signature+" X Y): ");
                out.println(input.nextLine());
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

    public static void main(String[] args) {
        try {
            Client c = new Client(9100);
            boolean bot = Boolean.parseBoolean(args[0]);
            c.initializeGame(bot);
            c.gameCourse();
        } catch (IOException e) {
            System.exit(1);
        }
    }
}
