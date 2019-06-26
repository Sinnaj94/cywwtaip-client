import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    int id;
    private Set<Node> neighbours;
    private Vector3D position;
    private double distance;
    private boolean blocked;

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    private double f;
    private double g;
    private double h;

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    private Node previous;

    public Node(Vector3D pos, int id, boolean blocked) {
        neighbours = new HashSet<>();
        position = pos;
        this.id = id;
        this.blocked = blocked;
    }

    public Vector3D getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public void addNeighbour(Node n) {
        neighbours.add(n);
    }

    public Set<Node> getNeighbours() {
        return neighbours;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Node getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        return String.format("Node %s has distance of %f", position.toString(), distance);
    }

    public double distanceTo(Node other) {
        // TODO: Check bot type
        /*if(blocked) {
            return Double.POSITIVE_INFINITY;
        }*/
        return 1;
        //return position.distance(other.getPosition());
    }
}
