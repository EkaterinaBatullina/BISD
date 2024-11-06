import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ManyClientServer {
    public static final int SERVER_PORT = 50000;
    private static boolean serverRunning = true;

    public static void main (String[] args){
        try(ServerSocket server = new ServerSocket(SERVER_PORT)) {
            System.out.println("Запуск сервера");
            while(serverRunning) {
                System.out.println("Ожидаю подключения клиента");
                Socket clientSocket = server.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void stopServer() {
        serverRunning = false;
    }

}


