import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try(Socket clientSocket = new Socket("127.0.0.1",50000)) {
            System.out.println("Клиент подключен");
            OutputStream outputStream = clientSocket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            String input;
            System.out.println("Введите ваш вопрос или 'Exit' для завершения:");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            while(true) {
                input = scanner.nextLine();
                String messageToSend = input + "\n";
                outputStream.write(messageToSend.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                System.out.println("Ответ: " + in.readLine());
                if (input.equals("Exit")) {
                    break;
                }
            }
            scanner.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}