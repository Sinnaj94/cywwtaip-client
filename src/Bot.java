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

    public Bot(NetworkClient client, int botID, Map<Integer, Node> map) {
        this.botID = botID;
        this.client = client;
        this.playerID = client.getMyPlayerNumber();
        //dijkstra = new Dijkstra(map, map.get(posToHash()));
        running = true;
    }

    private boolean collectsEnergy() {
        float[] pos = client.getBotPosition(botID, playerID);
        return pos[0] > .94f || pos[1] > .94f || pos[2] > .94f;
    }

    private void think() {
        //System.out.println("thinking...");
        // TODO
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
        return convert(client.getBotPosition(botID, playerID));
    }

    private Vector3D  getDirection() {
        float[] dir = client.getBotDirection(botID);
        return new Vector3D(dir[0], dir[1], dir[2]);
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
        return (float)angleBetween(a, b);
    }

    private double angleBetween(Vector3D a, Vector3D b) {
        return FastMath.acos(Vector3D.dotProduct(a, b) / (a.getNorm() * b.getNorm()));
    }

    private int posToHash() {
        Vector3D pos = getPosition();
        return (int)(((float)((int)(((float)((int)(pos.getX() * 1260.0F)) + pos.getY()) * 1260.0F)) + pos.getZ()) * 1260.0F);
    }

    @Override
    public void run() {
        try {
            while(true) {
                think();
                Thread.sleep(100);
                // Stop, if not running anymore

                if(!client.isAlive()) {
                    Thread.sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
