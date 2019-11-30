package tp.server;

import java.util.LinkedHashSet;

public class EmptyCluster {


    LinkedHashSet<Point> points = new LinkedHashSet<>();
    boolean blackNeighbor = false;
    boolean whiteNeighbor = false;
    Color owner = null;

    public void addPoint(Point p) {
        points.add(p);
    }

    public void addCluster(EmptyCluster ec) {
        points.addAll(ec.getPoints());
        if(ec.whiteNeighbor) whiteNeighbor = true;
        if(ec.blackNeighbor) blackNeighbor = true;
    }

    public void setNeighbor(Color c) {
        if (c == Color.BLACK) blackNeighbor = true;
        else if (c == Color.WHITE) whiteNeighbor = true;
    }
    public LinkedHashSet<Point> getPoints() {
        return points;
    }
    public int size() {
        return points.size();
    }

    public void determineOwnership() {
        if (blackNeighbor && !whiteNeighbor) {
            owner = Color.BLACK;
        }
        else if (!blackNeighbor && whiteNeighbor) {
            owner = Color.WHITE;
        }
    }

    public int getOwner() {
        if (owner == null) return -1;
        return owner.getValue();
    }
}
