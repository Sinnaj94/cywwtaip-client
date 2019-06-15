import lenz.htw.cywwtaip.world.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Node> neighbours;
    float[] position;

    public void setDistance(float distance) {
        this.distance = distance;
        neighbours = new ArrayList<>();
    }


    public void addNeighbour(Node n) {
        neighbours.add(n);
    }

    public List<Node> getNeighbours() {
        return neighbours;
    }

    private float distance;

    public float getDistance() {
        return distance;
    }

    public Node getPrevious() {
        return previous;
    }

    private Node previous;

    public Node(float[] pos) {
        position = pos;
    }
}
