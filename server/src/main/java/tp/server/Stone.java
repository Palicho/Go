package tp.server;

public class Stone extends Point {
    Color color;

    Stone(Color color, int x, int y) {
        super(x, y);
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
}
