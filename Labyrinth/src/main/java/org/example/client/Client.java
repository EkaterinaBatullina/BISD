package org.example.client;

import org.example.server.ServerPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PreGameFrame preGameFrame;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private MazePanel mazePanel;
    private ClientPlayer clientPlayer;
    private ServerPlayer serverPlayer;
    private int playerId;
    private String username;

    public Client(String username) {
        this.username = username;

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            preGameFrame = new PreGameFrame(username, out);

            // Инициализация окна
            this.frame = new JFrame("Maze Game");
            // Начинаем слушать данные с сервера в отдельном потоке
            new Thread(this::listenForGameState).start();
            setupControls();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForGameState() {
        try {
            String message;
            Maze maze = null;  // Лабиринт
            serverPlayer = new ServerPlayer(socket, playerId, username);  // Инициализация объекта serverPlayer
            clientPlayer = null;  // Инициализируем clientPlayer позже, после получения данных

            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                if (message.startsWith("STATE")) {
                    // Парсим размеры лабиринта
                    String[] parts = message.split(" ");
                    int mazeWidth = Integer.parseInt(parts[1]);
                    int mazeHeight = Integer.parseInt(parts[2]);

                    // Чтение и формирование массива лабиринта
                    int[][] newMaze = new int[mazeHeight][mazeWidth];
                    int[][] newTrap = new int[mazeHeight][mazeWidth];
                    int[][] newPoint = new int[mazeHeight][mazeWidth];
                    for (int i = 0; i < mazeHeight; i++) {
                        String[] row = in.readLine().split(" ");
                        for (int j = 0; j < mazeWidth; j++) {
                            newMaze[i][j] = Integer.parseInt(row[j]);
                        }
                    }
                    for (int i = 0; i < mazeHeight; i++) {
                        String[] row = in.readLine().split(" ");
                        for (int j = 0; j < mazeWidth; j++) {
                            newTrap[i][j] = Integer.parseInt(row[j]);
                        }
                    }
                    for (int i = 0; i < mazeHeight; i++) {
                        String[] row = in.readLine().split(" ");
                        for (int j = 0; j < mazeWidth; j++) {
                            newPoint[i][j] = Integer.parseInt(row[j]);
                        }
                    }
                    // Создание лабиринта
                    maze = new Maze(mazeWidth, mazeHeight);
                    maze.setMaze(newMaze);  // Устанавливаем новый массив лабиринта
                    maze.getTrap().setTraps(newTrap);
                    maze.getPoint().setPoints(newPoint);
                    // Если панель еще не создана, создаем её и добавляем в окно
                    if (mazePanel == null) {
                        mazePanel = new MazePanel(clientPlayer, serverPlayer, maze);
                        frame.getContentPane().removeAll();  // Удаляем старую панель, если она была
                        frame.add(mazePanel);  // Добавляем новую панель с лабиринтом
                        frame.pack();
                        frame.revalidate();
                        frame.repaint();
                    } else {
                        // Обновляем состояние лабиринта, если панель уже существует
                        mazePanel.updateMazeState(maze);
                    }

                    // Теперь создаем clientPlayer, если он еще не был инициализирован
                    if (clientPlayer == null) {
                        clientPlayer = new ClientPlayer(mazePanel, serverPlayer, socket);
                        mazePanel.setClientPlayer(clientPlayer);
                    }

                } else if (message.startsWith("PLAYER")) {
                    // Обрабатываем информацию о позиции игрока
                    String[] parts = message.split(" ");
                    int playerX = Integer.parseInt(parts[1]);
                    int playerY = Integer.parseInt(parts[2]);
                    int score = Integer.parseInt(parts[3]);
                    boolean slowed = Boolean.parseBoolean(parts[4]);
                    int slowdownTimer = Integer.parseInt(parts[5]);

                    // Обновляем состояние клиента и сервера
                    if (clientPlayer != null) {
                        clientPlayer.setX(playerX);
                        clientPlayer.setY(playerY);
                        clientPlayer.setScore(score);
                        clientPlayer.setSlowed(slowed);
                        clientPlayer.setSlowdownTimer(slowdownTimer);
                    }

                    // Обновляем состояние игрока на панели
                    if (mazePanel != null) {
                        mazePanel.updatePlayerState(playerX, playerY, score, slowed, slowdownTimer);
                    }
                } else if (message.startsWith("SCORE")) {

                    // Если панель уже существует, обновляем очки
                    if (mazePanel != null) {
                        mazePanel.updateScore();  // Обновляем отображение очков
                    }
                } else if (message.startsWith("TRAP")) {
                    // Если панель уже существует, обновляем очки
                    if (clientPlayer != null) {
                        clientPlayer.setSlowed(true);  // Устанавливаем флаг замедления
                    }
                    if (mazePanel != null) {
                        mazePanel.getServerPlayer().setSlowed(true);
                        System.out.println(10101010 + " " +  mazePanel.getServerPlayer().getSlowdownTimer());
                        mazePanel.getServerPlayer().setSlowdownTimer(5);
                        mazePanel.updateScore();
                        //mazePanel.getServerPlayer().setSlowed(false);
                        System.out.println(99999 + " " +  mazePanel.getServerPlayer().isSlowed());
                        clientPlayer.setSlowed(false);
                        //mazePanel.getServerPlayer().setSlowed(false);
                        // Обновляем отображение очков
                    }
                }  else if (message.startsWith("SLOWED")) {
                    // Обрабатываем состояние замедления от сервера
                    handleServerMessage(message);
                } else  if (message.startsWith("ID")) {
                    // Получаем ID игрока от сервера
                    String[] parts = message.split(" ");
                    playerId = Integer.parseInt(parts[1]);
                } else if (message.startsWith("VICTORY")) {
                    // Обработка победы
                    String[] parts = message.split(" ");
                    String winnerUsername = parts[1];

                    // Выводим сообщение о победе на панели
                    if (mazePanel != null) {
                        mazePanel.displayVictoryMessage("Player " + winnerUsername + " wins!");
                    }
                } else if (message.startsWith("GAME_READY")) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                    System.out.println("rrrrrrrrr");
                    // Уведомляем PreGameFrame, что игра готова
                    SwingUtilities.invokeLater(() -> {
                        preGameFrame.handleGameReady(); // Предполагаем, что у тебя есть ссылка на объект PreGameFrame
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        System.out.println(message);
        if (message.startsWith("SLOWED")) {
            // Разбиваем сообщение на части
            String[] parts = message.split(" ");

            // Если есть таймер (например, "SLOWED TRUE 5"), обновляем состояние замедления
            if (parts.length == 3) {
                boolean slowed = Boolean.parseBoolean(parts[1]);
                int timer = Integer.parseInt(parts[2]);

                // Обновляем состояние замедления
                updateSlowdownStateFromServer(slowed, timer);
            }
            // Если нет таймера (например, "SLOWED FALSE"), только меняем состояние
            else if (parts.length == 2 && parts[1].equals("FALSE")) {
                // Обновляем состояние замедления без таймера
                updateSlowdownStateFromServer(false, 0);
            } else if (message.startsWith("WAITING_FOR_SECOND_PLAYER")) {
                // Игрок ожидает второго игрока
                System.out.println("Waiting for second player...");
            }  else if (message.startsWith("WAITING_FOR_SECOND_PLAYER")) {
                // Игрок ожидает второго игрока
                System.out.println("Waiting for second player...");
                // Можно добавить уведомление в интерфейсе
            } else if (message.startsWith("GAME_READY")) {
                // Игра готова к запуску
                System.out.println("Second player is ready. Game will start soon.");
                // Можно обновить интерфейс, уведомив игрока
            } else if (message.startsWith("START_GAME")) {
                // Игра начинается
                System.out.println("Game started!");
                // Логика старта игры
                //startGame();

                SwingUtilities.invokeLater(() -> {
                    preGameFrame.handleGameReady(); // Предполагаем, что у тебя есть ссылка на объект PreGameFrame
                });

            }
        }
        // О
        // Обработка других сообщений от сервера
    }

    private void updateSlowdownStateFromServer(boolean slowed, int timer) {
        if (clientPlayer != null) {
            clientPlayer.setSlowed(slowed);
            clientPlayer.setSlowdownTimer(timer);
        }
        // Перерисовываем экран, чтобы отобразить обновленный таймер замедления
        if (mazePanel != null) {
            mazePanel.repaint();
        }
    }

    private void setupControls() {
        // Привязка клавиш
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                // Определяем направления в зависимости от нажатой клавиши
                int dx = 0;
                int dy = 0;

                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        dy = -1; // Двигаем игрока вверх
                        break;
                    case KeyEvent.VK_DOWN:
                        dy = 1; // Двигаем игрока вниз
                        break;
                    case KeyEvent.VK_LEFT:
                        dx = -1; // Двигаем игрока влево
                        break;
                    case KeyEvent.VK_RIGHT:
                        dx = 1; // Двигаем игрока вправо
                        break;
                    case KeyEvent.VK_SPACE:
                        handleSetTrap();  // Обработаем установку ловушки
                        break;
                }

                // Если был нажата клавиша, перемещаем игрока
                if (dx != 0 || dy != 0) {
                    sendMoveCommandToServer(dx, dy);
                    //clientPlayer.move(dx, dy);
                }
            }
        });

        // Убедимся, что панель получает фокус
        if (mazePanel != null) {
            mazePanel.setFocusable(true);  // Даем фокус панельке, чтобы она принимала события
            mazePanel.requestFocusInWindow();  // Запрашиваем фокус на панели
        }

        // Также можно добавить фокус на окно, если нужно
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    private void handleSetTrap() {
        if (serverPlayer.getScore() >= 3) {
            sendSetTrapCommandToServer();  // Отправим команду на сервер для установки ловушки
        } else {
            JOptionPane.showMessageDialog(frame, "Недостаточно очков для установки ловушки!");
        }
    }

    private void sendSetTrapCommandToServer() {
        try {
            out.println("SET_TRAP " + playerId + " " + serverPlayer.getX() + " " + serverPlayer.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMoveCommandToServer(int dx, int dy) {
        try {
            out.println("MOVE " + dx + " " + dy);  // Отправляем команду на сервер
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        new Client(username);
//    }
}
