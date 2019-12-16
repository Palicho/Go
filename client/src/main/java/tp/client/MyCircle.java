package tp.client;

import javafx.scene.shape.Circle;

import java.util.EventListener;

public class MyCircle extends Circle  {
    private int x;
    private int y;

    public MyCircle(int x, int y){
        this.x=x;
        this.y=y;

    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
