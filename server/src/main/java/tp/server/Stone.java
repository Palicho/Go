package tp.server;

public class Stone extends Point {
    private Color color;
    private int groupId;
    private boolean seki;

    Stone(Color color, int x, int y) {
        super(x, y);
        this.check();
        this.color = color;
        this.seki = false;
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
        this.seki = true;
    }

    public boolean isSeki() {
        return seki;
    }
}
