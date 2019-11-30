package tp.server;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Board {
    Point[][] board;
    int[] score;

    private LinkedList<StoneGroup> groups;
    public static Board instance = new Board();

    private Board() {
        board = new Point[19][19];
        score = new int[2];
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
     *
     * @param c the color of the new stone
     * @param x the x coordinate
     * @param y the y coordinate
     * @return false if it does
     */
    boolean verifyMove(Color c, int x, int y) {
        return !(board[x][y] instanceof Stone);
    }

    public boolean move(Color color, int x, int y) {
        //todo: make this for real

        if (verifyMove(color, x, y)) {
            updateGroups(color, x, y);
            return true;
        } else return false;
    }


    /**
     * Adds a new stone and merges groups
     *
     * @param color  the color of the new stone
     * @param stoneX the x coordinate of the new stone
     * @param stoneY the y coordinate of the new stone
     */
    private void updateGroups(Color color, int stoneX, int stoneY) {
        StoneGroup newGroup = new StoneGroup(color, stoneX, stoneY);
        int[] x = {stoneX + 1, stoneX, stoneX - 1, stoneX};
        int[] y = {stoneY, stoneY + 1, stoneY, stoneY - 1};
        StoneGroup neighbor;

        for (int i = 0; i < 4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                Point element = board[x[i]][y[i]];
                if (element instanceof Stone && ((Stone) element).getColor() == color) {
                    neighbor = groups.get(((Stone) element).getGroupId());
                    newGroup.addStones(neighbor);
                    groups.remove(neighbor);

                    for (StoneGroup sg : groups) {
                        sg.setId(groups.indexOf(sg));
                    }
                    redrawBoard();
                }
            }
        }

        groups.add(newGroup);
        newGroup.setId(groups.indexOf((newGroup)));
        redrawBoard();
    }

    /**
     * Modifies the board array to represent the current groups
     */
    private void redrawBoard() {
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                board[i][j] = new Point(i, j);
            }
        }

        for (StoneGroup group : groups) {
            for (Stone stone : group.getStones()) {
                board[stone.getX()][stone.getY()] = stone;
            }
        }
    }

    /**
     * Provides the color of the stone on the specified tile
     * @param x x coordinate
     * @param y y coordinate
     * @return  the color, or null if the tile is empty
     */
    public Color getColorAt(int x, int y) {
        if (board[x][y] instanceof Stone) {
            return ((Stone) board[x][y]).getColor();
        } else return null;
    }

    /**
     * Provides the id of the group that the stone on the specified tile belongs to
     * @param x x coordinate
     * @param y y coordinate
     * @return the id of the group, or -1 if the tile is empty
     */
    public int getGroupIdAt(int x, int y) {
        if (board[x][y] instanceof Stone) {
            return ((Stone) board[x][y]).getGroupId();
        } else return -1;
    }

    /**
     * Resets the board instance
     */
    public void reset() {
        instance = new Board();
    }

    /**
     * Provides the border of the specified set of points
     * @param set the set to find the border of
     * @return border a set of points that belong to the border
     */
    public LinkedHashSet<Point> getBorder(LinkedHashSet<Point> set) {
        int x, y;
        LinkedHashSet<Point> border = new LinkedHashSet<>();

        for (Point p : set) {

            x = p.getX();
            y = p.getY();

            border.addAll(getTileBorder(x,y));
        }
        return border;
    }

    public LinkedHashSet<Point> getTileBorder(int x, int y) {
        LinkedHashSet<Point> border = new LinkedHashSet<>();

        if (x - 1 >= 0) {
            border.add(board[x - 1][y]);
        }
        if (y - 1 >= 0) {
            border.add(board[x][y - 1]);
        }
        if (x + 1 < 19) {
            border.add(board[x + 1][y]);
        }
        if (y + 1 < 19) {
            border.add(board[x][y + 1]);
        }

        return border;
    }

    public void evaluateTerritoryPoints() {
        LinkedHashSet<Point> emptyPoints = new LinkedHashSet<>();
        for (Point[] p : board) for (Point point : p) if (!(point instanceof Stone)) emptyPoints.add(point);

        LinkedList<EmptyCluster> emptyClusters = new LinkedList<>();
        for (Point p : emptyPoints) if (p.notChecked()) emptyClusters.add(getCluster(p.getX(), p.getY()));
        for (EmptyCluster ec : emptyClusters) {
            ec.determineOwnership();
            if (ec.getOwner() == -1) {
                for (Point p : getBorder(ec.getPoints())) {
                    if (p instanceof Stone) ((Stone) board[p.getX()][p.getY()]).setSeki();
                }
            }
        }
        boolean sekiSafe;
        for (EmptyCluster ec : emptyClusters) {
            if (ec.getOwner() != -1) {
                sekiSafe = true;
                for (Point p : getBorder(ec.getPoints())) {
                    if (p instanceof Stone && ((Stone) p).seki()) sekiSafe = false;
                }
                if (sekiSafe) {
                    score[ec.getOwner() - 1] += ec.size();
                }
            }
        }
    }

    public EmptyCluster getCluster(int x, int y) {
        EmptyCluster cluster = new EmptyCluster();
        cluster.addPoint(board[x][y]);
        board[x][y].check();
        for (Point p : getTileBorder(x, y)) {
            if (!(p instanceof Stone)) {
                if (p.notChecked()) cluster.addCluster(getCluster(p.getX(), p.getY()));
            } else cluster.setNeighbor(((Stone) p).getColor());
        }
        return cluster;
    }

    public int getScore(Color color) {
        int c = color.getValue();
        return score[c - 1];
    }

}
