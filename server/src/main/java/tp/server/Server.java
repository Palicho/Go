package tp.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private Socket[] clientSockets;
    private PrintWriter[] out;
    private BufferedReader[] in;
    private GameLogic game = new GameLogic();
    private int gameID = 0;
    private int moveNumber = 0;
    private boolean load = false;
    private ArrayList<String> moves;
    private HibernateUtil hu;

    public Server(int port) {
        try {

            serverSocket = new ServerSocket(port);
            Socket startingClient = serverSocket.accept();
            BufferedReader startReader = new BufferedReader(new InputStreamReader(startingClient.getInputStream()));
            PrintWriter startWriter = new PrintWriter(startingClient.getOutputStream(), true);
            String line = startReader.readLine();
            hu = new HibernateUtil();

            moves= new ArrayList<String>();
            if (line.matches("START [12]")) {
                switch (line.charAt(6)) {
                    case '1':
                        clientSockets = new Socket[1];
                        out = new PrintWriter[1];
                        in = new BufferedReader[1];
                        clientSockets[0] = startingClient;
                        out[0] = startWriter;
                        in[0] = startReader;

                        startWriter.println("READY B");
                        break;
                    case '2':
                        clientSockets = new Socket[2];
                        out = new PrintWriter[2];
                        in = new BufferedReader[2];
                        clientSockets[0] = startingClient;
                        out[0] = startWriter;
                        in[0] = startReader;

                        startWriter.println("WAITING");
                        clientSockets[1] = serverSocket.accept();
                        out[1] = new PrintWriter(clientSockets[1].getOutputStream(), true);
                        in[1] = new BufferedReader(new InputStreamReader(clientSockets[1].getInputStream()));
                        in[1].readLine();

                        out[0].println("READY B");
                        out[1].println("READY W");

                        break;
                    default:
                        System.out.println("SWITCH ERROR");
                        break;
                }
            } else if (line.startsWith("LOAD ")) {
                clientSockets = new Socket[1];
                out = new PrintWriter[1];
                in = new BufferedReader[1];
                clientSockets[0] = startingClient;
                out[0] = startWriter;
                in[0] = startReader;

                while (!load) {
                    String[] command = line.split(" ");
                    try {
                        gameID = Integer.parseInt(command[1]);
                        // czy w bazie
                        load = true;
                        startWriter.println("OK");
                    } catch (Exception e) {
                        startWriter.println("BAD ID");
                        line = startReader.readLine();
                    }
                }
            }
            else startWriter.println("NO");
        } catch (IOException e) {
            System.out.println("IO ERROR");
            System.exit(1);
        }

    }

    boolean isLoaded() {
        return load;
    }

    void gameCourse() throws IOException {
        boolean pauseFlag = false;
        String line;
        String clientLine;

        while (true) {
            for (int i = 0; i < out.length; i++) {
                line = "MOVE";
                while (line.equals("MOVE")) {
                    out[i].println(line);
                    clientLine = in[i].readLine();
                    if (clientLine.matches("PAUSE [BW]")) {
                        if (pauseFlag || (in.length == 1 && clientLine.endsWith("B"))) {
                            line = game.endGame();
                            updateClients(line);
                            return;
                        }
                        else pauseFlag = true;
                        line = clientLine;
                    } else if (clientLine.matches("SURRENDER [BW]")) {
                        line = clientLine;
                        updateClients(line);
                        return;
                    } else line = game.move(clientLine);
                }
                updateClients(line);
                for (String msg: game.getRemoved()) updateClients(msg);
                if (!line.startsWith("PAUSE ")) pauseFlag = false;
                updateClients("EOF");
            }

            if (out.length == 1) {
                out[0].println(game.getBotMove());
                for (String msg: game.getRemoved()) {
                    updateClients(msg);
                }
                updateClients("EOF");
            }
        }
    }

    void gameEnd() throws IOException {
        for (int i = 0; i < in.length; i++) {
            in[i].close();
            out[i].close();
            clientSockets[i].close();
        }
        serverSocket.close();
    }

    void loadGame() {
        updateClients("B 1 1");
        updateClients("W 0 1");
        updateClients("SURRENDER W");
        /*String line = "";
        do {
            updateClients(line);
            //update line
        } while (!line.startsWith("END ") || line.startsWith("SURRENDER ")); */
    }

    void updateClients(String line) {
        for (PrintWriter out: out) out.println(line);
        if (!line.equals("EOF")) {
            //add to moves
            moveNumber++;
        }
    }

    public static void main(String[] args) {
        try {
            Server s = new Server(9100);
            if (s.isLoaded()) s.loadGame();
            else s.gameCourse();
            s.gameEnd();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
