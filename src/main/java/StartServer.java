
public class StartServer {

    public static void main(String[] args) {
        int port =3443;//порт сервера
        int connections =10;//число клиентов(один поток используется для пинга)
        Server server = new Server(port,connections);
    }
}
