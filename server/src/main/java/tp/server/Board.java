package tp.server;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.LinkedList;

public class Board {

    Point[][] board;
    int[] score;


    private LinkedList<StoneGroup> groups;
    private LinkedList<StoneGroup> deadGroups;
    private LinkedHashSet<Point> koFlaggedTiles;
    public static Board instance = new Board();

    private Board() {
        board = new Point[19][19];
        score = new int[2];
        groups = new LinkedList<>();
        deadGroups = new LinkedList<>();
        koFlaggedTiles = new LinkedHashSet<>();
        redrawBoard();
    }

    public static Board getInstance() {
        return instance;
    }

    public LinkedList<StoneGroup> getGroups() {
        return groups;
    }

    public LinkedHashSet<Point> getKoFlaggedTiles() {
        return koFlaggedTiles;
    }

    public LinkedList<StoneGroup> getDeadGroups() {
        return deadGroups;
    }

    /**
     * Checks if the move violates the rules
     * @param stone stone to be added in a mv
     * @return true if the move is okay
     */
    public boolean verifyMove(Stone stone) {

        if ( isPositionFree(stone)){
            if( haveLiberties(stone)){
                return true;
            }
            else{
                if(willJoin(stone)){
                    return true;
                }

                if(willKill(stone)){
                    if (isKo(stone)) {
                        return false;
                    }
                    return true;

                }

            }
        }
        return false;

    }

    boolean isPositionFree(int x, int y) {
        if (x<0 || x>18 || y<0 || y>18 || getColorAt(x, y) != null) return false;
        return true;
    }

    boolean isPositionFree(Stone stone) {
        return isPositionFree(stone.getX(), stone.getY());
    }




    boolean haveLiberties(Stone stone){

        int stoneX = stone.getX();
        int stoneY = stone.getY();

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};


        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                if( getColorAt(x[i],y[i] ) == null) {
                    return true;
                }

            }

        }

        return false;
    }



    LinkedList<StoneGroup> getGroupsToJoin(Stone stone){
        LinkedList<StoneGroup> stoneGroups= new LinkedList<>();
        int stoneX = stone.getX();
        int stoneY = stone.getY();
        int newLiberties = 0;
        LinkedHashSet<Point> tileBorder = getTileBorder(stoneX,stoneY);
        for (Point p: tileBorder) if (!(p instanceof Stone)) newLiberties++;

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};


        StoneGroup stoneGroup;
        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                stoneGroup=getGroupById(getGroupIdAt(x[i],y[i]));
                if(stoneGroup != null){

                    if( stoneGroup.getColor()==stone.getColor() &&  stoneGroup.getLiberties() + newLiberties -1 != 0){
                        stoneGroups.add(stoneGroup);
                    }
                }

            }
        }

        return stoneGroups;
    }

    boolean willJoin(Stone stone){
        if( getGroupsToJoin(stone).isEmpty()){
            return false;
        }
        return true;
    }


    LinkedList<StoneGroup> getGroupsToKill(Stone stone){
        LinkedList<StoneGroup> stoneGroups= new LinkedList<>();
        int stoneX = stone.getX();
        int stoneY = stone.getY();

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};

        StoneGroup stoneGroup;

        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                stoneGroup=getGroupById(getGroupIdAt(x[i],y[i]));
                if(stoneGroup!=null){

                    if( stoneGroup.getColor()!=stone.getColor() && stoneGroup.getLiberties()-1 == 0){
                        stoneGroups.add(stoneGroup);
                    }
                }

            }
        }
        return  stoneGroups;

    }

    public boolean isKo(Stone stone) {
        boolean ko = false;

        int x=stone.getX();
        int y= stone.getY();
        if (koFlaggedTiles.isEmpty()) {
            for (Point p: getTileBorder(x,y)) {
                LinkedHashSet<Point> border = getTileBorder(p.getX(),p.getY());
                border.remove(board[x][y]);
                boolean willBeKo = true;
                for (Point bp: border)
                    if (!(bp instanceof Stone) || ((Stone) bp).getColor() != stone.getColor()) {
                        willBeKo = false;
                        break;
                    }
                if (willBeKo) koFlaggedTiles.add(p);
            }
        }
        else {
            for (Point koPoint: koFlaggedTiles) {
                if (koPoint.getX() == x && koPoint.getY() == y) {
                    ko = true;
                    break;
                }
    }
            koFlaggedTiles = new LinkedHashSet<>();
        }
        return ko;
    }

    boolean willKill(Stone stone){

        if(getGroupsToKill(stone).isEmpty()){
            return false;
        }
        return  true;
    }


    public StoneGroup getGroupById(int id) {
        if (id>= 0 && id < groups.size()) {
            return groups.get(id);
        }

        return null;
    }


    public boolean move(Color color, int x, int y) {
        Stone newStone = new Stone(color, x, y);
        if (verifyMove(newStone)) {
            updateGroups(newStone);
            return true;
        }
        else return false;
    }


    /**
     * Adds a new stone and merges groups
     *
     * @param newStone stone to be added
     */
    private void updateGroups(Stone newStone) {



        StoneGroup newGroup = new StoneGroup(newStone);
        newGroup.setBorder(getTileBorder(newStone.getX(), newStone.getY()));

        LinkedList<StoneGroup> groupsToJoin = getGroupsToJoin(newStone);
        deadGroups = getGroupsToKill(newStone);

        for(StoneGroup stoneGroup : groupsToJoin){
            newGroup.addStones(stoneGroup);
            removeGroup(stoneGroup);
        }


        for(StoneGroup stoneGroup : deadGroups){
            score[newStone.getColor().getValue()] += stoneGroup.getStones().size();
            removeGroup(stoneGroup);
        }

        groups.add(newGroup);
        newGroup.setId(groups.indexOf((newGroup)));
        redrawBoard();
        newGroup.setBorder(getBorder(newGroup.getStones()));

    }

    private void removeGroup(StoneGroup group) {
        groups.remove(group);
        for (StoneGroup sg : groups) {
            sg.setId(groups.indexOf(sg));
        }
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

        for (StoneGroup group: groups) {
            group.setBorder(getBorder(group.getStones()));
            group.setLiberties();
        }
    }

    /**
     * Provides the color of the stone on the specified tile
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the color, or null if the tile is empty
     */
    public Color getColorAt(int x, int y) {
        if (board[x][y] instanceof Stone) {
            return ((Stone) board[x][y]).getColor();
        } else return null;
    }

    /**
     * Provides the id of the group that the stone on the specified tile belongs to
     *
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
     * Provides the border of the specified set of tiles
     *
     * @param set the set to find the border of
     * @return border a set of points that belong to the border
     */
    public <Tile extends Point> LinkedHashSet<Point> getBorder(LinkedHashSet<Tile> set) {
        int x, y;
        LinkedHashSet<Point> border = new LinkedHashSet<>();

        for (Tile p : set) {
            x = p.getX();
            y = p.getY();

            border.addAll(getTileBorder(x, y));
        }
        border.removeAll(set);

        return border;
    }


    /**
     * Provides a set of tiles around the specified point
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return a set of tiles around the point that fit onto the board
     */
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

    /**
     * Adds territory points to the score array
     */
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
        boolean validTerritory;
        for (EmptyCluster ec : emptyClusters) {
            if (ec.getOwner() != -1) {
                validTerritory = true;
                for (Point p : getBorder(ec.getPoints())) {
                    if (p instanceof Stone) {
                        Stone s = (Stone) p;
                        if (!(groups.get(s.getGroupId()).isAlive())) {
                            validTerritory = false;
                            break;
                        } else if (s.isSeki()) {
                            validTerritory = false;
                            break;
                        }
                    }
                }
                if (validTerritory) {
                    score[ec.getOwner()] += ec.size();
                }
            }
        }
    }

    /**
     * Gets a cluster of empty tiles starting with the provided point
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return a set of empty tiles along with info about their stone neighbors
     */
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

    /**
     * Provides the score of the specified player
     * @param color the player represented by the color they use
     * @return the score
     */
    public int getScore(Color color) {
        int c = color.getValue();
        return score[c];
    }

}
