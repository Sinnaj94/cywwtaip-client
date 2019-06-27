import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import java.util.Map;

public class Bot implements Runnable {
    private NetworkClient client;
    private int botID;
    public volatile boolean running;
    private int playerID;
    private Dijkstra dijkstra;
    private AStarGraph aStar;
    private float angle;
    private GraphNode goal;
    final float TOLERANCE = .02f;

    public Bot(NetworkClient client, int botID) {
        this.botID = botID;
        this.client = client;
        this.playerID = client.getMyPlayerNumber();
        running = true;
        // Implementing dijkstra
        //Dijkstra dijkstra = new Dijkstra(map, posToHash(), botID);
        float[] pos = client.getBotPosition(playerID, botID);
        aStar = new AStarGraph(client.getGraph(), pos, new float[]{0,1,0}, botID);
    }

    private boolean collectsEnergy() {
        float[] pos = client.getBotPosition(botID, playerID);
        return pos[0] > .94f || pos[1] > .94f || pos[2] > .94f;
    }

    private void think() {
        if(aStar.isFinished()) {
            if(goal == null) {
                goal = aStar.getNext();
            } else {
                if(isInPoint(goal)) {
                    goal = aStar.getNext();
                    System.out.println("Remaining: " + aStar.routeLength() + " speed: " + client.getBotSpeed(botID));
                }
            }
            float dir = navigateTo(goal);
            if(!Float.isNaN(dir)) {
                client.changeMoveDirection(botID, navigateTo(goal));
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

    @Override
    public void run() {
        try {
            while(true) {
                synchronized (Main.sync) {
                    // move to the right direction
                    think();
                    Thread.sleep(5);
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
