import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {

    private int port;
    private int connections;
    private ArrayList<ClientConnection> clients = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<String> usersPassword = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<String> getUsersPassword() {
        return usersPassword;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public synchronized void addUsers(String name) {
        this.users.add(name);
        XmlReadWrite.writeXml(users, "users.xml");
    }

    public synchronized void addUsersPassword(String uPassword) {
        this.usersPassword.add(uPassword);
        XmlReadWrite.writeXml(usersPassword, "usersPassword.xml");
    }

    public synchronized void addMessage(String message) {
        this.messages.add(message);
        XmlReadWrite.writeXml(messages, "messages.xml");
    }

    public Server(int port, int connections) {

        this.port = port;
        this.connections = connections;

        File fileUsers = new File("users.xml");
        File fileUsersPassword = new File("usersPassword.xml");
        File fileMessages = new File("messages.xml");

        if (fileUsers.exists())
            this.users = XmlReadWrite.readXml("users.xml");
        if (fileUsersPassword.exists())
            this.usersPassword = XmlReadWrite.readXml("usersPassword.xml");
        if (fileMessages.exists())
            this.messages = XmlReadWrite.readXml("messages.xml");

        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        ExecutorService service = Executors.newFixedThreadPool(connections);
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Start!");
            // добавляем периодическую отправку пинга
            service.submit(new Ping(this));

            while (true) {
                clientSocket = serverSocket.accept();
                ClientConnection client = new ClientConnection(clientSocket, this);
                clients.add(client);
                service.submit(client);
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            try {
                service.shutdown();
                clientSocket.close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientConnection o : clients) {
            o.sendMsg(msg);
        }
        if (!msg.equals("Ping"))
            addMessage(msg);
    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientConnection client) {
        clients.remove(client);
    }
}