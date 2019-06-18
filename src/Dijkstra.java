import com.sun.corba.se.impl.orbutil.graph.Graph;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;
import java.util.stream.Stream;

public class Dijkstra {
    private Node start;
    private Map<Integer, Node> graph;
    // Temporary
    private List<Node> q;


    public Dijkstra(Map<Integer, Node> graph, Node start) {
        this.start = start;
        this.graph = graph;
        dijkstra();
    }


    private void dijkstra() {
        initialize();
        // iterate through g until it is empty
        Node u;
        while(!q.isEmpty()) {
            u = smallestNode();
            q.remove(u);
            System.out.println(q.size());
            // Iterate through neighbours
            for(Node neighbour:u.getNeighbours()) {
                if(q.contains(neighbour)) {
                    distanceUpdate(u, neighbour);
                }
            }
        }
        System.out.println("Distance update ready");
        for(Node n:graph.values()) {
            System.out.println(n.getDistance());
        }
    }

    private void distanceUpdate(Node u, Node v) {
        double alt = u.getDistance() + u.distanceTo(v);
        if(alt < v.getDistance()) {
            v.setDistance(alt);
            v.setPrevious(u);
        }
    }

    // Returns smallest Node or null if there is no node
    private Node smallestNode() {
        return q.stream().min(Comparator.comparing(Node::getDistance)).orElse(null);
    }

    private void initialize() {
        System.out.println("Initializing dijkstra");
        // Go through the whole graph and set the distances
        for(Node n:graph.values()) {
            if(n == start) {
                // The start node should be 0
                n.setDistance(0);
            } else {
                // All other nodes should be infinity
                n.setDistance(Double.POSITIVE_INFINITY);
            }
            n.setPrevious(null);
        }
        // build a temporary list
        q = new ArrayList<>(graph.values());
        System.out.println("Initialization ready");
    }
}
