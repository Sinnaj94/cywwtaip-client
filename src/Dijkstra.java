import com.sun.corba.se.impl.orbutil.graph.Graph;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;

public class Dijkstra {
    private Node start;
    private Map<Integer, Node> graph;
    // Temporary
    private PriorityQueue<Node> q;


    // New Dijkstra algorithm
    public Dijkstra(Map<Integer, Node> graph, int start) {
        this.start = graph.get(start);
        this.graph = graph;
        dijkstra();
    }

    public Map<Integer, Node> getResult() {
        return graph;
    }


    private void dijkstra() {
        initialize();
        // iterate through g until it is empty
        Node u;
        double mil = System.currentTimeMillis();
        while(!q.isEmpty()) {

            u = q.poll();
            q.remove(u);
            // Iterate through neighbours
            for(Node neighbour:u.getNeighbours()) {
                distanceUpdate(u, neighbour);
            }
        }
        System.out.println();
        System.out.println(String.format("Distance update ready (%sms)", System.currentTimeMillis() - mil));
    }

    private void distanceUpdate(Node u, Node v) {
        double alt = u.getDistance() + u.distanceTo(v);
        if(alt < v.getDistance()) {
            v.setDistance(alt);
            v.setPrevious(u);
        }
    }

    private void initialize() {
        System.out.println("Initializing dijkstra");
        // Go through the whole graph and set the distances
        q = new PriorityQueue<>(Comparator.comparing(Node::getDistance));
        for(Node n:graph.values()) {
            // All node distances should be infinite
            n.setDistance(Double.POSITIVE_INFINITY);
            n.setPrevious(null);
            q.add(n);
        }
        // Start distance should be zero
        start.setDistance(0);
        System.out.println("Initialization ready.");
    }
}
