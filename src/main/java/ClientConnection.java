import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection implements Runnable {

    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private Socket clientSocket;

    String[] arrayString;

    public ClientConnection(Socket socket, Server server) {
        try {
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            boolean login = false;
            while (true) {
                // Если от клиента пришло сообщение
                if (inMessage.hasNext()) {
                    // если залогинены
                    if (login) {
                        String clientMessage = inMessage.nextLine();
                        // выходим из чата
                        if (clientMessage.equalsIgnoreCase("logout")) {
                            break;
                        }
                        // отправляем данное сообщение всем клиентам
                        server.sendMessageToAllClients(clientMessage);
                    } else {
                        //проверки логина или регистрация
                        String clientMessage = inMessage.nextLine();
                        arrayString = clientMessage.split(":");

                        if (arrayString[2].equals("Login")) {
                            if (server.getUsersPassword().contains(arrayString[0] + ":" + arrayString[1])) {
                                sendMsg("login ok");
                                sendAllMessages();
                                server.sendMessageToAllClients(arrayString[0] + " вошел в чат");
                                login = true;
                            } else {
                                sendMsg("неправильный логин или пароль");
                            }
                        } else if (server.getUsers().contains(arrayString[0])) {
                            sendMsg("логин уже занят");
                        } else {
                            server.addUsers(arrayString[0]);
                            server.addUsersPassword(arrayString[0] + ":" + arrayString[1]);
                            sendMsg("register ok");
                            sendAllMessages();
                            server.sendMessageToAllClients(arrayString[0] + " вошел в чат");
                            login = true;
                        }
                    }
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            server.removeClient(this);
        }
    }

    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //отправляем архив сообщений клиенту после логина или регистрации
    private void sendAllMessages() {
        for (int i = 0; i < server.getMessages().size(); i++) {
            sendMsg(server.getMessages().get(i));
        }
    }
}