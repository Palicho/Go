package tp.server;

import java.util.LinkedList;
import java.util.Random;

public class GameLogic {
    Board board = Board.getInstance();

    String move(String line) {
        String[] command = line.split(" ");
        Color c = command[0].equals("B") ? Color.BLACK : Color.WHITE;
        int x, y;
        try {
            x = Integer.parseInt(command[1]);
            y = Integer.parseInt(command[2]);
        } catch (Exception e) {
            return "MOVE";
        }

        if (board.move(c, x, y)) return line;
        else return "MOVE";
    }

    String getBotMove() {
        int x = 0, y = 0;
        Random random = new Random();
        while (!board.verifyMove(x, y, Color.WHITE)) {
            x = random.nextInt(18);
            y = random.nextInt(18);
        }
        return move("W " + x + " " + y);
    }

    LinkedList<String> getRemoved() {
        LinkedList<String> toRemove = new LinkedList<>();
        for (StoneGroup sg : board.getDeadGroups()) {
            for (Stone s : sg.getStones()) toRemove.add("REMOVE " + s.getX() + " " + s.getY());
        }
        return toRemove;
    }

    String endGame() {
        board.evaluateTerritoryPoints();
        return "END B " + board.getScore(Color.BLACK) + " W " + board.getScore(Color.WHITE);
    }

}
