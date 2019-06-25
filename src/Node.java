import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int id;
    private List<Node> neighbours;
    private Vector3D position;
    private double distance;

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    private Node previous;

    public Node(Vector3D pos, int id) {
        neighbours = new ArrayList<>();
        position = pos;
        this.id = id;
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

    public List<Node> getNeighbours() {
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
        return position.toString();
    }

    public double distanceTo(Node other) {
        return position.distance(other.getPosition());
    }
}
