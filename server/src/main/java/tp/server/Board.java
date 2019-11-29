package tp.server;

import java.util.LinkedList;

public class Board {
    int[][] colorBoard;
    int[][] groupBoard;

    private LinkedList<StoneGroup> groups;
    public static Board instance = new Board();

    private Board() {
        colorBoard = new int[19][19];
        groupBoard = new int[19][19];
        groups = new LinkedList<>();
        redrawGroupBoard();
    }

    public static Board getInstance() {
        return instance;
    }

    public LinkedList<StoneGroup> getGroups() {
        return groups;
    }

    /**
     * Checks if the move violates the rules
     * @param stone the stone to be placed in the move
     * @return true if the move is okay
     */
    boolean verifyMove(Stone stone) {
        if (colorBoard[stone.getX()][stone.getY()] != 0) return false;
        return true;
    }

    public boolean move(Color color, int x, int y) {
        //todo: make this for real
        Stone newStone = new Stone(color, x, y);
        if (verifyMove(newStone)) {
            addStone(newStone);
            return true;
        }
        else return false;
    }

    /**
     * Includes the new stone in colorBoard and groupBoard
     * @param newStone the stone to be included
     */
    private void addStone(Stone newStone) {
        colorBoard[newStone.getX()][newStone.getY()] = newStone.getColorValue();
        updateGroups(newStone);
    }


    /**
     * Merges groups after a new stone has been added
     * @param newStone the new stone
     */
    private void updateGroups(Stone newStone) {
        StoneGroup newGroup = new StoneGroup(newStone);
        int stoneX = newStone.getX();
        int stoneY = newStone.getY();
        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};
        StoneGroup neighbor;
        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                if (colorBoard[x[i]][y[i]] == newStone.getColorValue()) {
                    neighbor = groups.get(groupBoard[x[i]][y[i]]);
                    newGroup.addStones(neighbor);
                    groups.remove(neighbor);
                    redrawGroupBoard();
                }
            }
        }
        groups.add(newGroup);
        redrawGroupBoard();
    }

    /**
     * Updates the groupBoard to portray the groups that currently exist
     */
    private void redrawGroupBoard() {
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                groupBoard[i][j] = -1;
            }
        }

        for (StoneGroup group: groups) {
            int id = groups.indexOf(group);
            for (Stone stone: group.getStones()) {
                groupBoard[stone.getX()][stone.getY()] = id;
            }
        }
    }

    public Color getColorAt(int x, int y) {
        return Color.getColorByValue(colorBoard[x][y]);
    }

    public int getGroupIdAt(int x, int y) {
        return groupBoard[x][y];
    }

    public void reset() {
        instance = new Board();
    }

}
