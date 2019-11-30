package tp.server;

import java.util.LinkedList;

public class StoneGroup {
    int id;
    LinkedList<Stone> stones;
    int color;
    int liberties;
    LinkedList<Point> border;
    Status status;
    enum Status {alive,dead}

    void addStone(int x, int y) {
        Stone stone = new Stone(x,y);
        stones.add(stone);
    }

    void setLiberties(int liberties) {
        this.liberties= liberties;

    }

    void setStatus(Status status) {
        this.status= status;

    }

    public void setBorder(LinkedList<Point> border) {
        this.border = border;
    }

    public int getId() {
        return id;
    }


    public LinkedList<Stone> getStones() {
        return stones;
    }
}
