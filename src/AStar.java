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


    public boolean aStar() {
        closed = new HashSet<>();
        open = new PriorityQueue<Node>(Comparator.comparing(Node::getDistance));

        // Put only start node in open
        open.add(start);

        while(!open.isEmpty()) {
            Node current = open.poll();
            if(current == goal) {
                return true;
            }
        }
        return false;
    }

    private void expandNode(Node found) {
        // Go through each neighbour
        for(Node neighbour:found.getNeighbours()) {
            if(closed.contains(neighbour)) {
                return;
            }
        }
    }
}
