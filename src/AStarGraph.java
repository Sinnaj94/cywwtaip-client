import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

public class AStarGraph {
    private PriorityQueue<Integer> open;
    private Set<Integer> closed;
    private HashMap<GraphNode, Integer> graphNodeIntegerHashMap;

    private double[] gScore;
    private double[] fScore;
    private double[] hScore;

    private GraphNode[] previous;

    private GraphNode[] nodes;
    private int start;
    private int goal;


    public AStarGraph(GraphNode[] nodes, int start, int goal) {
        double t = System.currentTimeMillis();
        this.nodes = nodes;
        this.start = start;
        this.goal = goal;

        // initializing
        initialize();

        // Go through open while it is not empty
        int tries = 0;
        while(!open.isEmpty()) {
            // Getting object with min f
            int u = open.poll();

            // if the current node is the goal, the algorithm is done
            if(u == goal) {
                return;
            }
            // add it to the closed list
            closed.add(u);

            // Go through each node
            expandNode(u);
            System.out.println(open.size());
        }

        System.out.println("Conversion took " + (System.currentTimeMillis() - t));
        debug();
    }

    private void expandNode(int u) {
        // Go through each child
        for(GraphNode c:nodes[u].neighbors) {
            // conversion
            int child = nodeToID(c);

            // Stop if the neighbour is already discovered and proceed with next one
            if(closed.contains(child)) {
                break;
            }

            // calc g using distance
            double tent_g = gScore[u] + distanceBetween(child, u);

            // stop if it is contained in the list and score is higher
            if(open.contains(child)) {
                if(tent_g >= gScore[child]) {
                    break;
                }
            }

            previous[child] = nodes[u];
            gScore[child] = tent_g;


            // calc h (child to end node heuristic)
            hScore[child] = distanceBetween(child, goal);

            // set the f distance
            double f = tent_g + hScore[child];

            if(open.contains(child)) {
                // update score
                fScore[child] = f;
                open.remove(child);
                open.add(child);
            } else {
                open.add(child);
            }
        }
    }

    private void debug() {
        //
        for(double i: fScore) {
            if(i!=0) {
                System.out.println(i);
            }
        }
    }

    private void distanceUpdate(int u, int v) {
        double alt = gScore[u] + distanceBetween(u, v);
        if(alt < gScore[v]) {
            gScore[v] = alt;
            previous[v] = nodes[u];

            open.remove(v);
            open.add(v);
        }
    }

    // TODO: Calculate with radius
    private double distanceBetween(int a, int b) {
        GraphNode gA = nodes[a];
        GraphNode gB = nodes[b];
        // TODO: efficency?
        return FastMath.sqrt(FastMath.pow(gA.x + gB.x, 2) + FastMath.pow(gA.y + gB.y, 2) + FastMath.pow(gA.z + gB.z, 2));
    }

    private int nodeToID(GraphNode node) {
        return graphNodeIntegerHashMap.get(node);
    }



    private void initialize() {
        // Initialize the gScore and previous
        // VARIABLES
        gScore = new double[nodes.length];
        fScore = new double[nodes.length];
        hScore = new double[nodes.length];
        previous = new GraphNode[nodes.length];
        graphNodeIntegerHashMap = new HashMap<>();
        closed = new HashSet<>();

        // Priority Queue that is sorted by its smallest f-Score
        open = new PriorityQueue<Integer>((o1, o2) -> (int)FastMath.signum(fScore[o1] - fScore[o2]));

        // Go through all nodes and set gScore to infinity and previous to null
        for(int i = 0; i < nodes.length; i++) {
            previous[i] = null;
            gScore[i] = Double.POSITIVE_INFINITY;
            // Build the hashmap (for neighbouring)
            graphNodeIntegerHashMap.put(nodes[i], i);
        }

        // Set f-Score to start to zero and putting in array again
        fScore[start] = 0;

        // Only add "start" at the beginning
        open.add(start);
    }


}
