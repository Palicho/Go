package tp.server;

public class Stone extends Point {
    Color color;
    int groupId;

    Stone(Color color, int x, int y) {
        super(x, y);
        this.setOwned();
        this.check();
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public int getColorValue() {
        return color.getValue();
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
}
