package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("127.0.0.1", 5009)) {
//             Закомментировано: Взаимодействие с консолью
             System.out.println("Клиент подключен");
//             Scanner scanner = new Scanner(System.in);

            OutputStream outputStream = clientSocket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true); // true - автоматический flush

            // Запрос GET:
            // Закомментировано: Ввод URL с консоли
            // System.out.print("Введите URL (например, /index.html или /home): ");
            // String url = scanner.nextLine();
            String url = "/home";

            printWriter.println("GET " + url + " HTTP/1.1");
            printWriter.println("Host: localhost:5009");
            printWriter.println("Connection: close");
            printWriter.println();

//             Чтение ответа:
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                 Закомментировано: Вывод ответа в консоль
//                 System.out.println(line);
//            }
//             scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
