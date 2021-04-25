public class Ping implements Runnable {

    private final Server server;

    public Ping(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            // отправляем Ping всем клиентам каждую 0.1 секунду
            server.sendMessageToAllClients("Ping");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
