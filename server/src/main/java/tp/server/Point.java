package tp.server;

public class Point {
    protected int x;
    protected int y;
    Status status;

    enum Status {
        alive, dead, seki
    }
}
