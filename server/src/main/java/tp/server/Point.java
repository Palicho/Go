package tp.server;

public class Point {
    int x;
    int y;
    boolean isChecked;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
        this.isChecked = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean notChecked() {
        return !isChecked;
    }

    public void check() {
        this.isChecked = true;
    }
}
