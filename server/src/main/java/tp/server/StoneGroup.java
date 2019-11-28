package tp.server;

import java.util.LinkedHashSet;

public class StoneGroup {
    LinkedHashSet<Stone> stones = new LinkedHashSet<Stone>();
    LinkedHashSet<Point> border = new LinkedHashSet<Point>();
    Color color;
    int liberties;

    StoneGroup(Stone stone) {
        stones.add(stone);
        color = stone.getColor();
    }

    void addStones(StoneGroup group) {
        stones.addAll(group.getStones());
        border.addAll(group.getBorder());
    }

    public LinkedHashSet<Stone> getStones() {
        return stones;
    }

    public LinkedHashSet<Point> getBorder() {
        return border;
    }

    public Color getColor() {
        return color;
    }

    public int getLiberties() {
        return liberties;
    }

    void setLiberties() {

    }
}
