package tp.server;

import java.util.LinkedList;

public class Board {
    int[][] colorBoard = new int[19][19];
    int[][] groupBoard = new int[19][19];
    LinkedList<StoneGroup> blackGroups;
    LinkedList<StoneGroup> whiteGroups;

    boolean verifyMove(int color, int x, int y) {
        return true;
    }

    void move() {

    }

    void updateGroups() {

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
