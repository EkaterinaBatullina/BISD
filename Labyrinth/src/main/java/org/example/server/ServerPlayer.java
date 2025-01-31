package org.example.server;

import lombok.Getter;
import lombok.Setter;
import org.example.client.Maze;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Getter
@Setter
public class ServerPlayer {
    private int x, y;
    private int score = 0;
    private int id;
    private String username;
    //private boolean isWaiting = true;
    private boolean isSlowed = false;
    private int slowdownTimer; // Примерное значение для таймера замедления (например, 10 секунд)

    private Socket socket;

    public ServerPlayer(Socket socket, int id, String username) {
        this.x = 1;
        this.y = 1;
        this.id = id;
        this.username = username;
        this.socket = socket;

    }

    public void move(int dx, int dy, Maze maze)  {
//        System.out.println(isWaiting + " mooove");
        if (isSlowed) return;  // Если игрок замедлен, движение запрещено.
        System.out.println(dx + " " + dy);
        int newX = x + dx;
        int newY = y + dy;


        if (maze.isValidMove(newX, newY)) {
            x = newX;
            y = newY;

            // Логика взаимодействия с ловушками
            if (maze.hasTrapAt(x, y) && slowdownTimer == 0 && maze.trapIsCorrect(x, y, id)) {
                slowdownTimer = 5;
                System.out.println(1111111111 + " " + getSlowdownTimer());
                isSlowed = true;
                maze.removeTrapAt(x, y);
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("TRAP");
                    System.out.println(121212 + " " +  isSlowed);// Отправляем команду на сервер
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Логика получения очков
            if (maze.hasPointAt(x, y)) {
                score++;
                System.out.println(maze.getPoint().getPoints()[y][x]);
                maze.removePointAt(x, y);
                System.out.println(maze.getPoint().getPoints()[y][x]);
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("SCORE " + score);  // Отправляем команду на сервер
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // out.println("SCORE " + score);// Убираем точку с лабиринта
            }
            System.out.println(11111111 + " " + maze.getMaze()[y][x]);
            // Проверка, не попал ли игрок в точку выхода
            if (maze.getMaze()[y][x] == 2) { // 2 - это метка для точки выхода
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("VICTORY " + username);  // Сообщаем игроку, что он выиграл
                    System.out.println("Игрок " + id + " победил!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            }
    }

    // Обновление состояния замедления
    public void updateSlowdown() {
        if (slowdownTimer > 0) {
            slowdownTimer--;
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("SLOWED TRUE " + slowdownTimer);  // Отправляем информацию о замедлении
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (slowdownTimer == 0) {
                isSlowed = false;
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("SLOWED FALSE");  // Сообщаем клиенту, что замедление завершено
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Методы для получения состояния игрока
    public int getSlowdownTimer() {
        return slowdownTimer;
    }

    public boolean isSlowed() {
        return isSlowed;
    }

    public void placeTrap(int trapX, int trapY, Maze maze) {
        if (score >= 3) { // Если у игрока есть хотя бы 3 очка
            maze.setTrapAt(trapX, trapY, id); // Устанавливаем ловушку// Уменьшаем очки
        }
    }
}
