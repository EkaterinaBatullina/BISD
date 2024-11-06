import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static Map<String, String> answers = new HashMap<>();

    static {
        answers.put("Как у тебя дела?", "У меня все отлично, спасибо!");
        answers.put("Какой сегодня день?", "Сегодня прекрасный день!");
        answers.put("Что ты знаешь о Java?", "Java - это язык программирования.");
        answers.put("Какой твой любимый цвет?", "Мне нравятся все цвета!");
        answers.put("Что ты думаешь о погоде?", "Погода часто влияет на настроение!");
    }
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                if (message.equals("Exit")) {
                    ManyClientServer.stopServer();
                    String exitResponse = "Сервер останавливается.\n";
                    outputStream.write(exitResponse.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    break;
                }
                String response = answers.getOrDefault(message, "Извините, я не могу на это ответить.") + "\n";
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

