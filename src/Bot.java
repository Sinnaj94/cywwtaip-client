import lenz.htw.cywwtaip.net.NetworkClient;

public class Bot implements Runnable {
    private NetworkClient client;
    private int id;
    public volatile boolean running;
    private int player;
    private Dijkstra dijkstra;

    public Bot(NetworkClient client, int id) {
        this.id = id;
        this.client = client;
        this.player = client.getMyPlayerNumber();
        running = true;
    }

    private boolean collectsEnergy() {
        float[] pos = client.getBotPosition(id, player);
        return pos[0] > .94f || pos[1] > .94f || pos[2] > .94f;
    }

    private void think() {
        System.out.println("thinking...");
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
