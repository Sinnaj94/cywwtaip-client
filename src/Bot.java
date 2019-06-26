import lenz.htw.cywwtaip.net.NetworkClient;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import java.util.Map;

public class Bot implements Runnable {
    private NetworkClient client;
    private int botID;
    public volatile boolean running;
    private int playerID;
    private Dijkstra dijkstra;
    private float angle;
    private final Vector3D goal = new Vector3D(1, 0 , 0);

    public Bot(NetworkClient client, int botID) {
        this.botID = botID;
        this.client = client;
        this.playerID = client.getMyPlayerNumber();
        running = true;
        // Implementing dijkstra
        //Dijkstra dijkstra = new Dijkstra(map, posToHash(), botID);
        // TODO: fix
        AStarGraph astar = new AStarGraph(client.getGraph(), 0, 40900);

    }

    private boolean collectsEnergy() {
        float[] pos = client.getBotPosition(botID, playerID);
        return pos[0] > .94f || pos[1] > .94f || pos[2] > .94f;
    }

    private void think() {
        //System.out.println("thinking...");
        // TODO: Dijkstra anbindung
        // chasing point
        float dir = navigateTo(goal);
        if(!Float.isNaN(dir)) {
            client.changeMoveDirection(botID, navigateTo(goal));
        }
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

    private float navigateTo(Vector3D goal) {
        // TODO
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


    private int posToHash() {
        //Vector3D pos = getPosition();
        float[] p = client.getBotPosition(playerID, botID);
        return (int)(((float)((int)(((float)((int)(p[0] * 1260.0F)) + p[1]) * 1260.0F)) + p[2]) * 1260.0F);
    }

    @Override
    public void run() {
        try {
            while(true) {
                synchronized (Main.sync) {
                    think();
                    Thread.sleep(10);
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
