package tp.server;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Board {
    Point[][] board;

    private LinkedList<StoneGroup> groups;
    public static Board instance = new Board();

    private Board() {
        board = new Point[19][19];
        groups = new LinkedList<>();
        redrawBoard();
    }

    public static Board getInstance() {
        return instance;
    }

    public LinkedList<StoneGroup> getGroups() {
        return groups;
    }

    /**
     * Checks if the move violates the rules
     * @param c the color of the new stone
     * @param x the x coordinate
     * @param y the y coordinate
     * @return false if it does
     */
    boolean verifyMove(Color c, int x, int y) {
        if (board[x][y] instanceof Stone) return false;
        return true;
    }

    public boolean move(Color color, int x, int y) {
        //todo: make this for real

        if (verifyMove(color, x, y)) {
            updateGroups(color, x, y);
            return true;
        }
        else return false;
    }


    /**
     * Adds a new stone and merges groups
     * @param color the color of the new stone
     * @param stoneX the x coordinate of the new stone
     * @param stoneY the y coordinate of the new stone
     */
    private void updateGroups(Color color, int stoneX, int stoneY) {
        StoneGroup newGroup = new StoneGroup(color, stoneX, stoneY);
        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};
        StoneGroup neighbor;
        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                Point element = board[x[i]][y[i]];
                if (element instanceof Stone && ((Stone) element).getColor() == color) {
                    neighbor = groups.get(((Stone) element).getGroupId());
                    newGroup.addStones(neighbor);
                    groups.remove(neighbor);
                    redrawBoard();
                }
            }
        }
        groups.add(newGroup);
        newGroup.setId(groups.indexOf(newGroup));
        redrawBoard();
    }

    private void redrawBoard() {
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                board[i][j] = new Point(i,j);
            }
        }

        for (StoneGroup group: groups) {
            for (Stone stone: group.getStones()) {
                board[stone.getX()][stone.getY()] = stone;
            }
        }
    }

    public Color getColorAt(int x, int y) {
        if (board[x][y] instanceof Stone) {
             return ((Stone) board[x][y]).getColor();
        }
        else return null;
    }

    public int getGroupIdAt(int x, int y) {
        if (board[x][y] instanceof Stone) {
            return ((Stone) board[x][y]).getGroupId();
        }
        else return -1;
    }

    public void reset() {
        instance = new Board();
    }

    /**
     *
     * @param stoneGroup
     * @return border
     *
     */
    public LinkedHashSet<Point> getGroupBorder(StoneGroup stoneGroup){
        int x, y;
        LinkedHashSet<Point> border= new LinkedHashSet<>();

        for (Stone stone : stoneGroup.getStones()) {

          x = stone.getX();
          y = stone.getY();

          //top
            if(x-1 >= 0){
                border.add(board[x-1][y]);
            }
            //left
            if(y-1 >= 0){
                border.add(board[x][y-1]);
            }

            //bottom
            if(x+1<19){
                border.add(board[x+1][y]);
            }

            //right
            if(y+1<19){
                border.add(board[x][y+1]);
            }

        }

        return border;
    }

    public LinkedHashSet<Point> getTileBorder(int x, int y){
        LinkedHashSet<Point> border= new LinkedHashSet<>();

            //top
            if(x-1 >= 0){
                border.add(board[x-1][y]);
            }
            //left
            if(y-1 >= 0){
                border.add(board[x][y-1]);
            }

            //bottom
            if(x+1<19){
                border.add(board[x+1][y]);
            }

            //right
            if(y+1<19){
                border.add(board[x][y+1]);
            }

        return border;
    }


}
