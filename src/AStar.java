import java.util.*;

public class AStar {
    private Node start;
    private Node goal;
    private PriorityQueue<Node> open;
    private Set<Node> closed;

    public AStar(Node start, Node goal) {
        this.start = start;
        this.goal = goal;
        aStar();
    }


    public void aStar() {
        closed = new HashSet<>();
        open = new PriorityQueue<Node>(Comparator.comparing(Node::getDistance));

        // Put only start node in open
        open.offer(start);



    }
}
