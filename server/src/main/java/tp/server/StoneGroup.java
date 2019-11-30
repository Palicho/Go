package tp.server;

import java.util.LinkedHashSet;

public class StoneGroup {
    LinkedHashSet<Stone> stones = new LinkedHashSet<Stone>();
    LinkedHashSet<Point> border = new LinkedHashSet<Point>();
    Color color;
    int liberties;

    /**
     * @param stone the stone that starts the group
     */
    StoneGroup(Color color, int x, int y) {
        Stone stone = new Stone(color, x, y);
        stones.add(stone);
        this.color = color;
    }

    /**
     * Connects this group with the provided one
     * @param group the group to add
     */
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

    void setLiberties(int liberties) {
        this.liberties= liberties;
    }

    public void setId(int id) {
        for (Stone s: stones) s.setGroupId(id);
    }
}
