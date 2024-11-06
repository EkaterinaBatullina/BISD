import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int SERVER_PORT = 50000;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);
            // wait client connection
            //System.out.println("accept");
            System.out.println("start server");

            Socket clientSocket = server.accept();

            System.out.println("connected " + clientSocket.getInetAddress() +
                    ":" + clientSocket.getPort());

            InputStream inputStream = clientSocket.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String message = bufferedReader.readLine();

            System.out.println(message);

            clientSocket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}