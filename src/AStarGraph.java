import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AStarGraph {
    private PriorityQueue<Integer> open;
    private Set<Integer> closed;
    private HashMap<Integer, Integer> graphNodeIntegerHashMap;

    private Map<GraphNode, GraphNode> cameFrom;

    private boolean finished;

    private float[] gScore;
    private float[] fScore;

    private LinkedList<GraphNode> route;

    private Queue<GraphNode> way;

    private GraphNode[] nodes;
    private int start;
    private int goal;

    private int botID;

    public GraphNode getNext() {
        return route.pollLast();
    }

    public int routeLength() {
        return route.size();
    }



    private int nearestNode(float[] pos, GraphNode[] nodes) {
        float score = Float.MAX_VALUE;
        int near = -1;
        for(int i = 0; i < nodes.length; i++) {
            float dist = distanceFloatGraph(pos, nodes[i]);
            if(dist < score) {
                score = dist;
                near = i;
            }
        }
        System.out.println("nearest node is " + near + " with distance " + score);
        return near;
    }

    private float distanceFloatGraph(float[] a, GraphNode b) {
        return (float)FastMath.sqrt(FastMath.pow(a[0] - b.x, 2) + FastMath.pow(a[1] - b.y, 2) + FastMath.pow(a[2] - b.z, 2));
    }

    public AStarGraph(GraphNode[] nodes, float[] start, int goal, int botID) {
        int nearest = nearestNode(start, nodes);
        aStar(nodes, nearest, nearest + 1, botID);
    }

    public AStarGraph(GraphNode[] nodes, float[] start, float[] goal, int botID) {
        int nearestStart = nearestNode(start, nodes);
        int nearestGoal = nearestNode(goal, nodes);
        aStar(nodes, nearestStart, nearestGoal, botID);
    }


    public AStarGraph(GraphNode[] nodes, int start, int goal, int botID) {
        aStar(nodes, start, goal, botID);
    }

    private void aStar(GraphNode[] nodes, int start, int goal, int botID) {
        double t = System.currentTimeMillis();
        this.nodes = nodes;
        this.start = start;
        this.goal = goal;
        this.botID = botID;
        finished = false;

        // initializing
        initialize();

        // Go through open while it is not empty
        int tries = 0;
        while(!open.isEmpty()) {
            // Getting object with min f
            int u = open.poll();

            // if the current node is the goal, the algorithm is done
            if(u == goal) {
                System.out.println("Found goal for " + botID + " in " + (System.currentTimeMillis() - t));
                // finally reconstruct the path
                reconstructPath();
                return;
            }
            // add it to the closed list
            closed.add(u);

            // Go through each node
            expandNode(u);
        }

        System.out.println("Conversion took " + (System.currentTimeMillis() - t) +  ". No goal found.");
        debug();
    }

    private void reconstructPath() {
        route = new LinkedList<>();
        GraphNode current = nodes[goal];
        // go through hashmap
        while(cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            route.add(current);
        }
        debugPath(route);
        finished = true;
    }

    private void debugPath(LinkedList<GraphNode> path) {
        try {
            FileWriter f = new FileWriter("./path.txt");
            GraphNode last = null;
            for(GraphNode cur:path) {
                f.write(cur.x + " " + cur.y + " " + cur.z + " " + cur.blocked + "\n");
            }
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double len(GraphNode a) {
        return FastMath.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
    }

    private void expandNode(int u) {
        // Go through each child
        for(GraphNode c:nodes[u].neighbors) {
            // conversion
            int child = nodeToID(c);

            // Stop if the neighbour is already discovered and proceed with next one
            if(closed.contains(child))
                continue;

            // calc g using distance
            float tent_g = gScore[u] + exactDistanceBetween(u, child);

            // stop if it is contained in the list and score is higher
            if(!open.contains(child))
                open.add(child);
            else if(tent_g >= gScore[child])
                continue;

            //previous[child] = nodes[u];
            cameFrom.put(nodes[child], nodes[u]);

            gScore[child] = tent_g;

            fScore[child] = gScore[child] + exactDistanceBetween(child, goal);

            if(open.contains(child)) {
                open.remove(child);
                open.add(child);
            } else {
                open.add(child);
            }
        }
    }

    private void debug() {
        //
        /*for(double i: fScore) {
            if(i!=0) {
                System.out.println(i);
            }
        }*/
        // Go through all array

    }

    private float distanceBetween(int a, int b) {
        GraphNode A = nodes[a];
        GraphNode B = nodes[b];
        // TODO: efficency?
        return (float)FastMath.sqrt(FastMath.pow(B.x - A.x, 2) + FastMath.pow(B.y - A.y, 2) + FastMath.pow(B.z - A.z, 2));
    }

    private float exactDistanceBetween(int a, int b) {
        //return(distanceBetween(a,b));
        GraphNode A = nodes[a];
        GraphNode B = nodes[b];
        // Distance is 1, so formula is easy.
        return (float)FastMath.acos((A.x * B.x + A.y * B.y + A.z * B.z));
    }

    private int nodeToID(GraphNode node) {
        return graphNodeIntegerHashMap.get(node.hashCode());
    }



    private void initialize() {
        // Initialize the gScore and previous
        // VARIABLES
        gScore = new float[nodes.length];
        fScore = new float[nodes.length];

        // Map for getting an id by the object
        graphNodeIntegerHashMap = new HashMap<>();

        // Map for neighbours
        cameFrom = new HashMap<>();
        closed = new HashSet<>();

        // Priority Queue that is sorted by its smallest f-Score
        open = new PriorityQueue<>((o1, o2) -> {
            float d1 = fScore[o1];
            float d2 = fScore[o2];
            return Float.compare(d1, d2);
        });

        // Go through all nodes and set gScore to infinity and previous to null
        for(int i = 0; i < nodes.length; i++) {
            gScore[i] = Float.POSITIVE_INFINITY;
            fScore[i] = Float.POSITIVE_INFINITY;
            // Build the hashmap (for neighbouring)
            graphNodeIntegerHashMap.put(nodes[i].hashCode(), i);
        }

        // cost from start to start is zero.
        gScore[start] = 0;
        // estimate the f score (min distance)
        fScore[start] = exactDistanceBetween(start, goal);

        // Only add "start" at the beginning
        open.add(start);
    }


    public boolean isFinished() {
        return finished;
    }
}
