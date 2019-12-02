package tp.server;

import java.util.ArrayList;
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
     * @param stone stone to be added in a mve
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

    private boolean isKo(Stone stone) {
        return false;
    }

    boolean isPositionFree(Stone stone){
        if (colorBoard[stone.getX()][stone.getY()] != -1) return false;
        return true;
    }




    boolean haveLiberties(Stone stone){

        int stoneX = stone.getX();
        int stoneY = stone.getY();

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};


        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                if( colorBoard[  x[i] ] [ y[i] ] == -1){
                    return true;
                }

            }
        }

        return  false;
    }


    ArrayList<Integer> getGroupsIdToJoin(Stone stone){
        ArrayList<Integer> ids= new ArrayList<Integer>();
        int stoneX = stone.getX();
        int stoneY = stone.getY();

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};


        StoneGroup stoneGroup;
        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                stoneGroup=getGroupById(getGroupIdAt(x[i],y[i]));
                if( stoneGroup.getColor()==stone.getColor() &&  stoneGroup.getLiberties()-1 != 0){
                    ids.add(getGroupIdAt(x[i],y[i]));
                }

            }
        }

        return ids;
    }

    boolean willJoin(Stone stone){
        if( getGroupsIdToJoin(stone)== null){
            return true;
        }
        return false;
    }


    ArrayList<Integer> getGroupsIdToKill(Stone stone){
        ArrayList<Integer> ids= new ArrayList<Integer>();
        int stoneX = stone.getX();
        int stoneY = stone.getY();

        int[] x = {stoneX+1, stoneX, stoneX-1, stoneX};
        int[] y = {stoneY, stoneY+1, stoneY, stoneY-1};

        StoneGroup stoneGroup;

        for (int i=0; i<4; i++) {
            if (x[i] >= 0 && x[i] <= 18 && y[i] >= 0 && y[i] <= 18) {
                stoneGroup=getGroupById(getGroupIdAt(x[i],y[i]));
                if( stoneGroup.getColor()!=stone.getColor() && stoneGroup.getLiberties()-1 == 0){
                    ids.add(getGroupIdAt(x[i],y[i]));
                }

            }
        }
        return  ids;

    }

    boolean willKill(Stone stone){

        if(getGroupsIdToKill(stone) != null){
            return true;
        }
        return  false;
    }



    public StoneGroup getGroupById( int id){
        for ( StoneGroup stoneGroup : groups){
            if(stoneGroup.getId()== id){
                return stoneGroup;
            }
        }
        return null;
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

        ArrayList<Integer> groupsToJoin = getGroupsIdToJoin(newStone);
        ArrayList<Integer> groupsToKill = getGroupsIdToJoin(newStone);

        StoneGroup newGroup = new StoneGroup(newStone);


        for(int id : groupsToJoin){
            newGroup.addStones(getGroupById(id));
            groups.remove(getGroupById(id));
        }
        groups.add(newGroup);


        for(int id : groupsToKill){
            groups.remove(getGroupById(id));
        }

        colorBoard[newStone.getX()][newStone.getY()] = newStone.getColorValue();
        updateGroups(newStone);
    }


    /**
     * Merges groups after a new stone has been added
     * @param newStone the new stone
     */
    private void updateGroups(Stone newStone) {


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

    /**
     *
     * @param stoneGroup
     * @return border
     *
     */
    public LinkedList<Point> getGroupBorder(StoneGroup stoneGroup){

        int x, y;
        LinkedList<Point> border= new LinkedList<Point>();

        for( Stone stone : stoneGroup.getStones()){

          x=stone.getX();
          y=stone.getY();

          //top
            if(x-1 >= 0){
                if( colorBoard[x-1][y] == -1){
                    border.add(new Point(x-1, y));
                }
            }
            //left
            if(y-1 >= 0){
                if( colorBoard[x][y-1] == -1){
                    border.add(new Point(x, y-1));
                }
            }

            //bottom
            if(x+1<19){
                if( colorBoard[x+1][y] == -1){
                    border.add(new Point(x+1, y));
                }
            }

            //right
            if(y+1<19){
                if( colorBoard[x][y+1] == -1){
                    border.add(new Point(x, y+1));
                }
            }


        }

        return border;
    }
}
