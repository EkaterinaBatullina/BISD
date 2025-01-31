package org.example.client;

import lombok.Getter;
import lombok.Setter;
import org.example.server.ServerPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Setter
@Getter
public class ClientPlayer {
    private ServerPlayer serverPlayer;
    private int x, y;
    private int score = 0;
    private boolean isSlowed = false;
    private int slowdownTimer = 0;
    private MazePanel mazePanel;
    private Image cloudTextur;
    private Socket socket;

    public ClientPlayer(MazePanel mazePanel, ServerPlayer serverPlayer, Socket socket) {
        if (mazePanel == null || serverPlayer == null) {
            throw new IllegalArgumentException("MazePanel and ServerPlayer cannot be null");
        }
        this.serverPlayer = serverPlayer;
        this.x = 1;  // Инициализация позиции игрока
        this.y = 1;
        this.mazePanel = mazePanel;
        this.socket = socket;
        initTextures();
    }

    private void initTextures() {
        cloudTextur = new ImageIcon(getClass().getResource("/images.jpeg")).getImage();
    }

    public void move(int dx, int dy) {
       // System.out.println(isWaiting + " mooove");
        if (isSlowed) {
            System.out.println("Player is slowed, cannot move.");
            return; // Если игрок замедлен, не даем двигаться
        }

        // Отправка команды на сервер
        //serverPlayer.move(dx, dy, mazePanel.getMaze());
        sendMoveCommandToServer(dx, dy);
        // Синхронизация с состоянием сервера
        x = serverPlayer.getX();
        y = serverPlayer.getY();
        score = serverPlayer.getScore();
        isSlowed = serverPlayer.isSlowed();
        slowdownTimer = serverPlayer.getSlowdownTimer();

        if (mazePanel.getMaze().getMaze()[y][x] == 2) {
            // Игрок достиг выхода, отправляем серверу команду о победе
            sendVictoryCommandToServer();
        }


        // Вычисление ограниченной области для перерисовки
        int cellSize = mazePanel.getCellSize();
        int startX = Math.max(0, x - 1) * cellSize;
        int startY = Math.max(0, y - 1) * cellSize;
        int endX = Math.min(mazePanel.getMazeWidth(), x + 1) * cellSize;
        int endY = Math.min(mazePanel.getMazeHeight(), y + 1) * cellSize;

        // Перерисовываем только измененную часть
        System.out.println(startX + " " + startY + " " + startX + " " + (endX - startX) + " " + (endY - startY));
        mazePanel.repaint(startX, startY, endX - startX, endY - startY);
    }

    private void sendVictoryCommandToServer() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("VICTORY " + serverPlayer.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMoveCommandToServer(int dx, int dy) {
        // Предполагаем, что у тебя есть доступ к сетевому соединению (например, сокет)
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(333444 + " " + slowdownTimer);
            out.println("MOVE " + dx + " " + dy);
            System.out.println(44444 + " " + slowdownTimer);// Отправляем команду на сервер
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Рисование области вокруг игрока
    public void draw(Graphics g, int cellSize, Point point) {
        int startX = x - 1;
        int startY = y - 1;

        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        int endX = Math.min(point.getMaze().getWidth() - 1, x + 1);
        int endY = Math.min(point.getMaze().getHeight() - 1, y + 1);
        // Отображение невидимых клеток
        for (int i = 0; i < point.getMaze().getHeight(); i++) {
            for (int j = 0; j < point.getMaze().getWidth(); j++) {
                if (i < startY || i > endY || j < startX || j > endX) {
                    g.setColor(new Color(169, 169, 169)); // Серый для невидимых клеток
                    g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }

        // Отображение видимых клеток
        for (int i = startY; i <= endY; i++) {
            for (int j = startX; j <= endX; j++) {
                if (point.getMaze().getMaze()[i][j] == 0 || point.getMaze().getMaze()[i][j] == 2) {
                    g.drawImage(cloudTextur, j * cellSize, i * cellSize, cellSize, cellSize, null);
                }
            }
        }
        // Рисуем игрока
        g.setColor(Color.BLACK); // Игрок черный
        g.fillOval(x * cellSize + cellSize / 4, y * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);

    }

    public void setScore(int score) {
        this.score = score;
    }

}
