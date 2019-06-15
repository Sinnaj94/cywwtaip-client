import com.sun.corba.se.impl.orbutil.graph.Graph;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;
import java.util.stream.Stream;

public class Dijkstra {
    private Node start;
    private List<Node> graph;
    // Temporary
    private List<Node> q;

    public Dijkstra(List<Node> graph, Node start) {
        this.start = start;
        this.graph = graph;
        dijkstra();
    }


    public void dijkstra() {
        initialize();
        // iterate through g until it is empty
        Node u;
        while(!q.isEmpty()) {
            u = smallestNode();
            q.remove(u);
        }
    }

    // Returns smallest Node or null if there is no node
    private Node smallestNode() {
        return q.stream().min(Comparator.comparing(Node::getDistance)).orElse(null);
    }

    private void initialize() {
        // Go through the whole graph and set the distances
        for(Node n:graph) {
            if(n == start) {
                // The start node should be 0
                n.setDistance(0);
            } else {
                // All other nodes should be infinity
                n.setDistance(Float.POSITIVE_INFINITY);
            }
        }
        // build a temporary list
        q = new ArrayList<>(graph);
    }
}
