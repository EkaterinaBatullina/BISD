package org.example;

import org.example.client.Client;
import org.example.client.LoginFrame;
import org.example.server.Server;

import javax.swing.*;

public class DualClientTest {

    public static void main(String[] args) {
//        // Запуск сервера в отдельном потоке
//        new Thread(() -> {
//            Server.main(new String[0]);  // Запускаем сервер
//        }).start();
//
//        // Даем серверу немного времени для запуска, чтобы он мог слушать подключения
//        try {
//            Thread.sleep(2000); // Ждем 2 секунды, чтобы сервер успел запуститься
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Запуск первого клиента (он подключается к серверу)
//        new Thread(() -> {
//            Client client1 = new Client();  // Первый игрок
//        }).start();
//
//        // Ждем немного времени, чтобы первый клиент успел подключиться
//        try {
//            Thread.sleep(1000);  // Немного времени для подключения клиента
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Запуск второго клиента (второй игрок)
//        new Thread(() -> {
//            Client client2 = new Client();  // Второй игрок
//        }).start();
//


        new Thread(() -> {
            Server.main(new String[0]);  // Запускаем сервер
        }).start();

        // Запуск первого клиента в отдельном потоке
        Thread client1 = new Thread(() -> {
            SwingUtilities.invokeLater(() -> new LoginFrame());
        });

        // Запуск второго клиента в отдельном потоке
        Thread client2 = new Thread(() -> {
            SwingUtilities.invokeLater(() -> new LoginFrame());
        });

        // Стартуем оба потока
        client1.start();
        client2.start();
    }
}
