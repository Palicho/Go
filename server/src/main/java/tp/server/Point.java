package tp.server;

public class Point {
    int x;
    int y;
    boolean isOwned;
    boolean isChecked;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
        this.isOwned = false;
        this.isChecked = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setOwned() {
        isOwned = true;
    }

    public void check() {
        this.isChecked = true;
    }
}
