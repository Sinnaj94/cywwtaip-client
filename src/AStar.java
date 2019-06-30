import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A Star class for pathfinding
 */
public class AStar {
    private PriorityQueue<Integer> open;
    private Set<Integer> closed;
    private int playerID;

    private Map<GraphNode, GraphNode> cameFrom;

    private boolean finished;

    private float[] gScore;
    private float[] fScore;

    private LinkedList<GraphNode> route;


    private GraphNode[] nodes;
    private int start;
    private int goal;

    private int botID;

    /**
     * AStar algorithm
     * @param nodes The given Graph
     * @param start Start as float array (x,y,z)
     * @param goal Goal as float array (x,y,z)
     * @param botID Given Bot ID
     * @param playerID Given PlayerID
     */
    public AStar(GraphNode[] nodes, float[] start, float[] goal, int botID, int playerID) {
        int nearestStart = nearestNode(start, nodes);
        int nearestGoal = nearestNode(goal, nodes);
        this.playerID = playerID;
        aStar(nodes, nearestStart, nearestGoal, botID);
    }

    /**
     * AStar algorithm
     * @param nodes The given Graph
     * @param start Start as float array (x,y,z)
     * @param goal Goal as index
     * @param botID Given Bot ID
     * @param playerID Given PlayerID
     */
    public AStar(GraphNode[] nodes, float[] start, int goal, int botID, int playerID) {
        int nearestStart = nearestNode(start, nodes);
        this.playerID = playerID;
        aStar(nodes, nearestStart, goal, botID);
    }

    /**
     * Get the next Node of the calculated route
     * @return Next node of calculated route
     */
    public GraphNode getNext() {
        return route.pollLast();
    }

    /**
     * Calculate the nearest Node-Index of a position in a graph
     * @param pos float[] with x,y,z
     * @param nodes Graph with multiple nodes
     * @return Nearest node-index
     */
    private int nearestNode(float[] pos, GraphNode[] nodes) {
        float score = Float.MAX_VALUE;
        int near = -1;
        // Go through each node in the graph
        for (int i = 0; i < nodes.length; i++) {
            // Check, which distance is the smallest
            float dist = CustomMath.dist(pos, nodes[i]);
            if (dist < score) {
                score = dist;
                near = i;
            }
        }
        return near;
    }


    /**
     * A Star algorithm, that does the operations
     * @param nodes Graph
     * @param start Start node as id
     * @param goal End node as id
     * @param botID Given Bot id
     */
    private void aStar(GraphNode[] nodes, int start, int goal, int botID) {
        double t = System.currentTimeMillis();
        this.nodes = nodes;
        this.start = start;
        this.goal = goal;
        this.botID = botID;
        finished = false;

        // Return, if the start is blocked
        if (nodes[start].blocked) {
            System.out.println("Start is blocked.");
            return;
        }

        // Check if goal is blocked and chose neighbour that is not blocked
        if (nodes[goal].blocked) {
            System.out.println("Start or end is blocked. Choosing neighbour.");
            for (int i = 0; i < nodes[goal].neighbors.length; i++) {
                // Look for not blocked neighbours
                if (!nodes[goal].neighbors[i].blocked) {
                    goal = nodeToID(nodes[goal].neighbors[i]);
                    break;
                }
            }
        }

        // initialize
        initialize();

        // Go through open while it is not empty
        while (!open.isEmpty()) {
            // Getting object with min f using .poll()
            int u = open.poll();

            // if the current node is the goal, the algorithm is done
            if (u == goal) {
                // finally reconstruct the path
                reconstructPath();
                return;
            }
            // add it to the closed list
            closed.add(u);

            // Go through each node
            expandNode(u);
        }
        // No node was found.
    }

    /**
     * Reconstruct the path via the cameFrom Map
     */
    private void reconstructPath() {
        // the route as linked list
        route = new LinkedList<>();
        GraphNode current = nodes[goal];

        // go through hashmap and build the route
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            route.add(current);
        }

        // set finished = true, so the bot knows the algorithm is done
        finished = true;
    }


    /**
     * Method for debugging the path
     * @param path given path as linkedlist
     */
    private void debugPath(LinkedList<GraphNode> path) {
        try {
            FileWriter f = new FileWriter("./path.txt");
            GraphNode last = null;
            for (GraphNode cur : path) {
                f.write(cur.x + " " + cur.y + " " + cur.z + " " + cur.blocked + "\n");
            }
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Expand the node
     * @param u Node id in graph
     */
    private void expandNode(int u) {
        // Go through each child
        for (GraphNode c : nodes[u].neighbors) {
            // conversion
            int child = nodeToID(c);

            // Stop if the neighbour is already discovered and proceed with next one
            if (closed.contains(child))
                continue;

            // Stop if it is  blocked path anyways (except it is bot 1)
            if (c.blocked && botID != 1)
                continue;

            // calc g using distance
            float tent_g = gScore[u] + exactDistanceBetween(u, child);

            // stop if it is contained in the list and score is higher
            if (!open.contains(child))
                open.add(child);
            else if (tent_g >= gScore[child])
                continue;

            //previous[child] = nodes[u];
            cameFrom.put(nodes[child], nodes[u]);

            gScore[child] = tent_g;

            fScore[child] = gScore[child] + exactDistanceBetween(child, goal);

            if (open.contains(child)) {
                open.remove(child);
                open.add(child);
            } else {
                open.add(child);
            }
        }
    }


    /**
     * Distance between two points on a sphere. Also modifies it based on owner and blocked
     *
     * @param a Start
     * @param b Goal
     * @return Distance
     */
    private float exactDistanceBetween(int a, int b) {
        float score = 0;
        GraphNode A = nodes[a];
        GraphNode B = nodes[b];
        // Distance is 1, so formula can be improved.
        score = (float) FastMath.acos((A.x * B.x + A.y * B.y + A.z * B.z));
        if (B.owner != 0) {
            if (B.owner != playerID + 1) {
                // it is good, if it is other player
                score *= .5;
            } else if (B.owner == playerID + 1) {
                // it is very bad, if it is own player
                score *= 2;
            }
        }
        // If the direct neighbours are blocked, the bot sometimes hangs, so try to avoid
        for (int i = 0; i < B.neighbors.length; i++) {
            if (B.neighbors[i].blocked) {
                score *= 2;
            }
        }
        return score;
    }

    /**
     * Convert a GraphNode to an id via hashcode
     *
     * @param node Node to be converted
     * @return Id of the array
     */
    private int nodeToID(GraphNode node) {
        return Main.graphNodeIntegerHashMap.get(node.hashCode());
    }


    /**
     * Initialize the A-Star variables
     */
    private void initialize() {
        // Initialize the gScore and previous
        // VARIABLES
        gScore = new float[nodes.length];
        fScore = new float[nodes.length];

        // Map for neighbours
        cameFrom = new HashMap<>();
        closed = new HashSet<>();

        // Priority Queue that is sorted by its smallest f-Score using custom comparator
        open = new PriorityQueue<>((o1, o2) -> {
            float d1 = fScore[o1];
            float d2 = fScore[o2];
            return Float.compare(d1, d2);
        });

        // Go through all nodes and set gScore to infinity and previous to null
        for (int i = 0; i < nodes.length; i++) {
            gScore[i] = Float.POSITIVE_INFINITY;
            fScore[i] = Float.POSITIVE_INFINITY;
        }

        // cost from start to start is zero.
        gScore[start] = 0;
        // estimate the f score (min distance)
        fScore[start] = exactDistanceBetween(start, goal);

        // Only add "start" at the beginning
        open.add(start);
    }

    /**
     * @return if algorithm is finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @return if route is empty
     */
    public boolean isEmpty() {
        return route.isEmpty();
    }
}
