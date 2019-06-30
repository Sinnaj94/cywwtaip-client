import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

import java.util.concurrent.BrokenBarrierException;

/**
 * Bot class for navigating a bot
 */
public class Bot implements Runnable {
    private final float TOLERANCE = .02f;
    public volatile boolean running;
    private NetworkClient client;
    private int botID;
    private int playerID;
    private AStar aStar;
    private GraphNode[] nodes;
    private GraphNode goal;
    private double lastRefill;

    /**
     * Constructor for Bot
     * @param client NetworkClient from cywwtaip
     * @param botID Given Bot id
     */
    public Bot(NetworkClient client, int botID) {
        this.botID = botID;
        this.client = client;
        this.playerID = client.getMyPlayerNumber();
        running = true;

        // Get the graph
        nodes = client.getGraph();

        // Go to energy fields first and recharge (the interesting points updates the algorithm)
        aStar = new AStar(nodes, client.getBotPosition(playerID, botID), new float[]{0, 1, 0}, botID, client.getMyPlayerNumber());
    }

    /**
     * @return ID of the current Bot
     */
    public int getBotID() {
        return botID;
    }

    /**
     * Check if the bot collects energy
     * @return if the bot collects energy
     */
    private boolean collectsEnergy() {
        // if the game is running, bot doesn't need to be checked
        if (!client.isGameRunning()) {
            return true;
        }
        float[] pos = client.getBotPosition(botID, playerID);
        // It collects energy if a vectorpoint > .94
        return FastMath.abs(pos[0]) > .94f || FastMath.abs(pos[1]) > .94f || FastMath.abs(pos[2]) > .94f;
    }

    /**
     * Update the lastRefill for outer use
     */
    private void energyUpdate() {
        if (collectsEnergy()) {
            lastRefill = System.currentTimeMillis();
        }
    }

    /**
     * Return the last refill
     * @return Last refill in system time (milliseconds)
     */
    public double getLastRefill() {
        return lastRefill;
    }

    /**
     * The actual thinking algorithm
     */
    private void think() {
        // Always update the energy first
        energyUpdate();
        // Check if aStar can be used
        if (aStar.isFinished() && aStar != null) {
            // if the current goal is null, set it! (except aStar is empty)
            if(!aStar.isEmpty()) {
                // null ponter
                if (goal == null) {
                    goal = aStar.getNext();
                } else {
                    // If point is reached
                    if (isInPoint(goal)) {
                        goal = aStar.getNext();
                    }
                }
            }

            // Get the current direction
            float dir = angleTo(goal);

            // only turn if the direction is not nan
            if (!Float.isNaN(dir)) {
                client.changeMoveDirection(botID, dir);
            }
        }
    }

    /**
     * Check if the bot is in point (at least near to it using tolerance)
     * @param goal The goal
     * @return if the bot is in the given point
     */
    private boolean isInPoint(GraphNode goal) {
        float[] pos = client.getBotPosition(playerID, botID);
        return CustomMath.dist(pos, goal) < TOLERANCE;
    }

    /**
     * Get position as Vector3D
     * @return Position as Vector3D
     */
    private Vector3D getPosition() {
        return CustomMath.convert(client.getBotPosition(playerID, botID));
    }

    /**
     * Get direction as Vector3D
     * @return the current direction
     */
    private Vector3D getDirection() {
        return CustomMath.convert(client.getBotDirection(botID));
    }

    /**
     * Get the current position as float[]
     * @return
     */
    public float[] getBotPosition() {
        return client.getBotPosition(playerID, botID);
    }

    /**
     * Angle to a given GraphNode
     * @param g Graph Node goal
     * @return the angle in radians
     */
    private float angleTo(GraphNode g) {
        Vector3D goal = new Vector3D(g.x, g.y, g.z);
        // Vektor a: Richtungsvektor
        Vector3D curPos = getPosition();
        Vector3D a = getDirection();
        // Zielvektor
        // Tafelbild Lenz
        Vector3D b = goal.subtract(curPos.scalarMultiply(Vector3D.dotProduct(curPos, goal)));
        return angleBetween(a, b);
    }

    /**
     * Return the angle between two 3D vectors
     * @param a First vector
     * @param b Second vector
     * @return the angle in radians
     */
    private float angleBetween(Vector3D a, Vector3D b) {
        double length = (a.getNorm() * b.getNorm());
        return (float) FastMath.acos(Vector3D.dotProduct(a, b) / (length));
    }

    /**
     * Get the current goal as vector
     * @return current goal as vector
     */
    private Vector3D getGoal() {
        if (goal == null) {
            return null;
        }
        return new Vector3D(goal.x, goal.y, goal.z);
    }

    /**
     * Set the goal
     * @param goal The goal to be set using index
     */
    public void setGoal(int goal) {
        aStar = new AStar(client.getGraph(), client.getBotPosition(playerID, botID), goal, botID, client.getMyPlayerNumber());
    }

    /**
     * Set the goal
     * @param goal The goal to be set using position
     */
    public void setGoal(float[] goal) {
        aStar = new AStar(client.getGraph(), client.getBotPosition(playerID, botID), goal, botID, client.getMyPlayerNumber());
    }

    /**
     * The run method - threading of the bot
     */
    @Override
    public void run() {
        int tries = 0;
        try {
            while (true) {
                // Only think while the game is running
                if (client.isGameRunning()) {
                    // Perform the thinking
                    think();
                    try {
                        // Barrier has 3 Values for 3 bots, so they are treated equally
                        Main.barrier.await();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                } else {
                    // If the game didn't start yet wait a lil
                    Thread.sleep(50);
                }
                // Stop, if not running anymore

                if (!client.isAlive()) {
                    Thread.sleep(1000);
                    tries++;
                    if(tries >= 3) {
                        System.out.println("Exiting, the server doesn't repond anymore.");
                        System.exit(-1);
                    }
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
