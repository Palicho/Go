package tp.client;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MyCircle extends Circle {
    private int x;
    private int y;
    Color color;

    public MyCircle(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.TRANSPARENT;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setColor(Color c) {
        this.color = c;
        this.setFill(c);
    }

    public boolean canClick() {
        return color == Color.TRANSPARENT;
    }

}
