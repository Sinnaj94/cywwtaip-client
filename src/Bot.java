import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

import java.util.Arrays;

public class Bot implements Runnable {
    private NetworkClient client;

    public int getBotID() {
        return botID;
    }

    private int botID;
    public volatile boolean running;
    private int playerID;
    private AStar aStar;
    private GraphNode[] nodes;
    private KMeans KMeans;
    private GraphNode goal;
    private final float TOLERANCE = .05f;

    public Bot(NetworkClient client, int botID) {
        this.botID = botID;
        this.client = client;
        this.playerID = client.getMyPlayerNumber();
        running = true;
        // Implementing dijkstra
        //Dijkstra dijkstra = new Dijkstra(map, posToHash(), botID);
        float[] pos = client.getBotPosition(playerID, botID);
        nodes = client.getGraph();
        // Go to energy fields first and recharge
        aStar = new AStar(nodes, client.getBotPosition(playerID, botID), new float[]{0,1,0}, botID);
        //AStar.getShortestPath(g, g[0], g[100]);
        // Thread for updating the most interesting region
    }

    private boolean collectsEnergy() {
        float[] pos = client.getBotPosition(botID, playerID);
        return pos[0] > .94f || pos[1] > .94f || pos[2] > .94f;
    }

    private void think() {
        if(aStar.isFinished()) {
            if(goal == null && !aStar.isEmpty()) {
                goal = aStar.getNext();
            } else {
                if(isInPoint(goal) && !aStar.isEmpty()) {
                    goal = aStar.getNext();
                    //System.out.println("Remaining: " + aStar.routeLength() + " speed: " + client.getBotSpeed(botID));
                }
            }
            float dir = navigateTo(goal);
            if(!Float.isNaN(dir)) {
                client.changeMoveDirection(botID, dir);
            }
        }
    }

    private boolean isInPoint(GraphNode goal) {
        float[] pos = client.getBotPosition(playerID, botID);
        return FastMath.sqrt(FastMath.pow(pos[0] - goal.x, 2) + FastMath.pow(pos[1] - goal.y, 2) + FastMath.pow(pos[2] - goal.z, 2)) < TOLERANCE;
    }

    private Vector3D convert(float[] vec) {
        return new Vector3D(vec[0], vec[1], vec[2]);
    }

    private Vector3D getPosition() {
        return convert(client.getBotPosition(playerID, botID));
    }

    private Vector3D getDirection() {
        return convert(client.getBotDirection(botID));
    }

    private float navigateTo(GraphNode g) {
        // TODO
        Vector3D goal = new Vector3D(g.x, g.y, g.z);
        // Vektor a: Richtungsvektor
        Vector3D curPos = getPosition();
        Vector3D curDir = getDirection();
        Vector3D a = curDir;
        // Zielvektor
        // Tafelbild Lenz
        Vector3D b = goal.subtract(curPos.scalarMultiply(Vector3D.dotProduct(curPos, goal)));
        //Vector3D b = z.subtract(goal_dot);
        //Vector3D b = getPosition().subtract(goal);
        return angleBetween(a, b);
        //return getAngleOnSphere(curPos, curDir, goal);
    }

    private float angleBetween(Vector3D a, Vector3D b) {
        double length = (a.getNorm() * b.getNorm());
        return (float)FastMath.acos(Vector3D.dotProduct(a, b) / (length));
    }

    private Vector3D getGoal() {
        if(goal == null) {
            return null;
        }
        return new Vector3D(goal.x, goal.y, goal.z);
    }


    private int posToHash() {
        //Vector3D pos = getPosition();
        float[] p = client.getBotPosition(playerID, botID);
        return (int)(((float)((int)(((float)((int)(p[0] * 1260.0F)) + p[1]) * 1260.0F)) + p[2]) * 1260.0F);
    }

    private float distanceToGoal() {
        return (float)getPosition().distance(getGoal());
    }

    public void setGoal(float[] goal) {
        aStar = new AStar(nodes, client.getBotPosition(playerID, botID), goal, botID);
    }

    public void setGoal(int goal) {
        aStar = new AStar(nodes, client.getBotPosition(playerID, botID), goal, botID);
    }

    @Override
    public void run() {
        try {
            while(true) {
                synchronized (Main.sync) {
                    // move to the right direction
                    if(client.isGameRunning()) {
                        think();
                    } else {
                        Thread.sleep(50);
                    }
                    // Stop, if not running anymore

                    if(!client.isAlive()) {
                        Thread.sleep(1000);
                    }
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
