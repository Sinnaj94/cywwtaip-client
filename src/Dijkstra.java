import com.sun.corba.se.impl.orbutil.graph.Graph;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;

public class Dijkstra {
    private Node start;
    private Map<Integer, Node> graph;
    private int botType;
    // Temporary
    private PriorityQueue<Node> q;
    private PriorityQueue<Node> t;


    // New Dijkstra algorithm
    public Dijkstra(Map<Integer, Node> graph, int start, int botType) {
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
        double mil = System.currentTimeMillis();
        while(!q.isEmpty()) {
            // Getting the node with min distance
            Node u = q.poll();

            // Iterate through neighbours
            for(Node v:u.getNeighbours()) {
                if(q.contains(v)) {
                    distanceUpdate(u, v);
                }
            }
        }
        System.out.println(String.format("Distance update ready (%sms)", System.currentTimeMillis() - mil));
        debug();

    }

    private void debug() {
        // Sort the nodes
        List<Node> sorted = new ArrayList<>(graph.values());
        sorted.sort(Comparator.comparingDouble(Node::getDistance));
        for(Node n:sorted) {
            System.out.print(n.getDistance() + " ");
        }
    }

    public void shortestPath(Node goal) {
        List<Node> path = new ArrayList<>();
        path.add(goal);
    }

    private void distanceUpdate(Node u, Node v) {
        double alt = u.getDistance() + u.distanceTo(v);
        if(alt < v.getDistance()) {
            v.setDistance(alt);
            v.setPrevious(u);
            // TODO: Remove, because unnecessary
            //open.comparator();
            q.remove(v);
            q.add(v);
        }
    }

    private void initialize() {
        System.out.println("Initializing dijkstra");

        // Set the PriorityQueue with a Comparator, which returns the smallest Distance
        q = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistance));
        // Go through the whole graph and set the distances
        for(Node n:graph.values()) {
            // All node distances should be infinite
            n.setDistance(Double.POSITIVE_INFINITY);
            n.setPrevious(null);
            // if the vector is the start vector
            if(n == start) {
                n.setDistance(0);
            }
            q.add(n);
        }
        // TODO: am anfang nur startknoten

        // Start distance should be zero
        System.out.println("Initialization ready.");
    }

}
