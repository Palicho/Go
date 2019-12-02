package tp.server;

import java.util.LinkedHashSet;

public class StoneGroup {
    LinkedHashSet<Stone> stones = new LinkedHashSet<Stone>();
    LinkedHashSet<Point> border = new LinkedHashSet<Point>();
    Color color;
    int liberties;

    /**
     * Create a group with the parameters of its first stone
     *
     * @param color the color of the group
     * @param x     the x coordinate of the stone
     * @param y     the y coordinate of the stone
     */
    StoneGroup(Color color, int x, int y) {
        Stone stone = new Stone(color, x, y);
        stones.add(stone);
        this.color = color;
    }

    StoneGroup(Stone stone) {
        stones.add(stone);
        this.color= stone.getColor();
    }

    /**
     * Connects this group with the provided one
     *
     * @param group the group to add
     */
    void addStones(StoneGroup group) {
        stones.addAll(group.getStones());
        border.removeAll(stones);
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
        int l = 0;
        for (Point p: border) {
            if (!(p instanceof Stone)) l++;
        }
        liberties = l;
    }

    public void setId(int id) {
        for (Stone s : stones) s.setGroupId(id);
    }

    public boolean isAlive() {
        //todo: make this return true only if liberties > 1
        return true;
    }

    public void setBorder(LinkedHashSet<Point> border) {
        //border.removeAll(stones);
        this.border = border;
    }
}
