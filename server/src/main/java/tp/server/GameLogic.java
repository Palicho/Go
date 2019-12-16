package tp.server;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class GameLogic {
    Board board = Board.getInstance();

    String move(String line) {
        String[] command = line.split(" ");
        Color c;
        if (command[0].equals("B")) c = Color.BLACK;
        else if (command[0].equals("W")) c = Color.WHITE;
        else return "MOVE";
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
        LinkedList<StoneGroup> opponentGroups = new LinkedList<>();
        for (StoneGroup sg : board.getGroups()) {
            if (sg.getColor() == Color.BLACK) opponentGroups.add(sg);
        }
        LinkedHashSet<Point> movePool;
        int min;
        while (!opponentGroups.isEmpty()) {
            StoneGroup target = opponentGroups.poll();
            movePool = target.getBorder();
            min = target.getLiberties();
            for (StoneGroup sg : opponentGroups) {
                if (sg.getLiberties() < min) {
                    min = sg.getLiberties();
                    movePool = sg.getBorder();
                    opponentGroups.remove(sg);
                }
            }
            int x, y;
            for (Point p : movePool) {
                x = p.getX();
                y = p.getY();
                if (board.verifyMove(x, y, Color.WHITE)) return move("W " + x + " " + y);
            }
        }
        return "SURRENDER W";
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
