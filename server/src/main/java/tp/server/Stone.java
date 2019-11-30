package tp.server;

public class Stone extends Point {
    Color color;
    int groupId;
    boolean seki;

    Stone(Color color, int x, int y) {
        super(x, y);
        this.check();
        this.color = color;
        this.seki = false;
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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setSeki() {
        seki = true;
    }

    public boolean seki() {
        return seki;
    }
}
