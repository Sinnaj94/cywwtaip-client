import lenz.htw.cywwtaip.world.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int id;
    private List<Node> neighbours;
    private float[] position;
    private double distance;

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    private Node previous;

    public Node(float[] pos, int id) {
        neighbours = new ArrayList<>();
        position = pos;
        this.id = id;
    }

    public float[] getPosition() {
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
        return String.format("Node with ID %s at Position %f, %f, %f has %d neighbours.", id, position[0], position[1], position[2], neighbours.size());
    }

    public double distanceTo(Node other) {
        // TODO: improve (should fit a sphere)
        float[] otherPos = other.getPosition();
        double sum = 0;
        // euclidian distance
        for(int i = 0; i < position.length; i++) {
            sum += Math.pow(position[i] - otherPos[i], 2);
        }
        return Math.sqrt(sum);
        //return 1;
    }
}
