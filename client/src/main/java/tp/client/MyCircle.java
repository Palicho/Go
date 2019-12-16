package tp.client;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.EventListener;

public class MyCircle extends Circle  {
    private int x;
    private int y;
    Color color;


    public MyCircle(int x, int y){
        this.x=x;
        this.y=y;
        this.color = null;
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

    public Color getColor() {
        return color;
    }

    public void makeInvisible() {
        this.color = Color.TRANSPARENT;

    }
}
