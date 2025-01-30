package org.example.server;

import org.example.client.Maze;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;  // Порт для подключения
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static Maze maze;  // Один общий лабиринт для всех
    private static int playerIdCounter = 1;
    private static boolean isGameReady = false;
    private static String firstPlayerUsername = null;
    private static String secondPlayerUsername = null;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);

            new Thread(Server::updateSlowdownStates).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();  // Ожидание подключения клиента
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String username = in.readLine();

                if (username == null || username.isEmpty()) {
                    System.out.println("Никнейм не был передан. Отключаем клиента.");
                    clientSocket.close();
                    continue;
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, playerIdCounter, username);
                clients.add(clientHandler); // Добавляем клиента в список
                playerIdCounter++;
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Периодическое обновление состояния замедления всех игроков
    private static void updateSlowdownStates() {
        while (true) {
            try {
                Thread.sleep(1000);  // Пауза в 1 секунду (можно изменить)
                synchronized (clients) {
                    for (ClientHandler client : clients) {
                        if (client.getPlayer().isSlowed()) {  // Проверяем, если замедление активно
                            client.getPlayer().updateSlowdown();  // Обновляем состояние замедления
                            client.sendPlayerState();  // Отправляем обновленную информацию клиенту
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Класс для обработки запросов от клиента
    static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ServerPlayer player;
        private final int playerId;
        private final String username;
        private boolean isRunning = true;
        private String difficulty;

        public ClientHandler(Socket socket, int playerId, String username) {
            this.clientSocket = socket;
            this.playerId = playerId;
            this.username = username;
            this.player = new ServerPlayer(clientSocket, playerId, username); // Присваиваем ID игрока
        }

        @Override
        public void run() {
            try {
                // Создаем потоки для чтения и записи
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Отправляем ID игрока клиенту
                out.println("ID " + playerId);
                out.println("USER " + username);

                // Инициализация нового игрока
                player = new ServerPlayer(clientSocket, playerId, username);  // Создаем игрока
                synchronized (clients) {
                    // Отправляем состояние лабиринта новому клиенту


                    //АААААААААААА
                    //АААААААААААА
                    //sendMazeState(); ВНИМАНИЕ ТУТ ЗАКОММЕНТИЛА
                    //АААААААААААА
                    //АААААААААААА


                    // Отправляем состояние нового игрока только этому клиенту
                    sendPlayerState();
                }

                // Главный игровой цикл
                String message;
                while (isRunning && (message = in.readLine()) != null) {
                    System.out.println("reeeeaaaaaddddyyyy");
                    if (message.startsWith("USER READY")) {
                        String[] parts = message.split(" ");
                        String playerDifficulty = parts[3]; // Сложность из команды
                        this.difficulty = playerDifficulty;
                        System.out.println("reeeeaaaaaddddyyyy");// Сохраняем сложность
                        handlePlayerReady(username, difficulty);
                    } else {
                        // Обрабатываем другие команды (например, движение)
                        handlePlayerInput(message);
                    }

                    synchronized (clients) {
                        // Отправляем обновленное состояние только этому клиенту
                        sendPlayerState();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Очистка ресурсов и закрытие соединений
                cleanUp();
            }
        }

        private synchronized void handlePlayerReady(String username, String difficulty) {
            System.out.println("handlePlayerReady");
            System.out.println(firstPlayerUsername);
            System.out.println(secondPlayerUsername);

            if (firstPlayerUsername == null) {
                firstPlayerUsername = username;
                System.out.println("WAITING_FOR_SECOND_PLAYER");// Первый игрок готов
                sendMessage("WAITING_FOR_SECOND_PLAYER");  // Ожидаем второго игрока
            } else if (secondPlayerUsername == null) {
                secondPlayerUsername = username;
                System.out.println("GAME_READY"); // Второй игрок готов
                sendMessage("GAME_READY");  // Игра готова к началу
                // Оповещаем первого игрока о готовности второго
                getClientByUsername(firstPlayerUsername).sendMessage("GAME_READY");
                maze = createMazeBasedOnDifficulty(difficulty);
                maze.generate();  // Генерация лабиринта
                maze.getTrap().place();
                maze.getPoint().place();
                sendMazeState();
                // Запускаем игру, когда оба игрока готовы
                startGameForBothPlayers();
            }
        }

        private static Maze createMazeBasedOnDifficulty(String difficulty) {
            int width = 0;
            int height = 0;

            switch (difficulty.toLowerCase()) {
                case "easy":
                    width = 20;
                    height = 20;
                    break;
                case "medium":
                    width = 30;
                    height = 30;
                    break;
                case "hard":
                    width = 40;
                    height = 40;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
            }

            return new Maze(width, height);  // Генерируем лабиринт нужного размера
        }

        private synchronized void startGameForBothPlayers() {
            // Игра готова для начала
            sendMessage("START_GAME");
            getClientByUsername(firstPlayerUsername).sendMessage("START_GAME");
        }


        // Метод для остановки клиента (вызывается внутри handleVictory или при закрытии соединения)
        public void stopClient() {
            isRunning = false;
            interrupt();  // Прерываем поток
            cleanUp();    // Закрываем все ресурсы
        }

        // Метод для очистки ресурсов
        private void cleanUp() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMazeState() {
            // Отправляем текущее состояние лабиринта новому клиенту
            out.println("STATE " + maze.getWidth() + " " + maze.getHeight());
            // Отправляем каждый элемент лабиринта (например, 0 - пустая клетка, 1 - стена)
            for (int i = 0; i < maze.getHeight(); i++) {
                for (int j = 0; j < maze.getWidth(); j++) {
                    out.print(maze.getMaze()[i][j] + " ");
                }
                out.println();  // Новая строка после каждой строки лабиринта
            }

            for (int i = 0; i < maze.getHeight(); i++) {
                for (int j = 0; j < maze.getWidth(); j++) {
                    out.print(maze.getTrap().getTraps()[i][j] + " ");
                }
                out.println();  // Новая строка после каждой строки лабиринта
            }

            for (int i = 0; i < maze.getHeight(); i++) {
                for (int j = 0; j < maze.getWidth(); j++) {
                    out.print(maze.getPoint().getPoints()[i][j] + " ");
                }
                out.println();  // Новая строка после каждой строки лабиринта
            }
        }

        private void sendPlayerState() {
            // Отправляем информацию о текущем игроке только этому клиенту
            out.println("PLAYER " + player.getX() + " " + player.getY() + " " + player.getScore() + " " + player.isSlowed() + " " + player.getSlowdownTimer());
        }

        private void handlePlayerInput(String message) {
            // Разбор команды
            String[] parts = message.split(" ");
            if (parts[0].equals("MOVE")) {
                // Получаем сдвиги по осям
                int dx = Integer.parseInt(parts[1]);
                int dy = Integer.parseInt(parts[2]);
                player.move(dx, dy, maze);
                // После движения, отправляем обновленную информацию только этому клиенту
                sendMazeState();
                sendPlayerState();
            } else if (parts[0].equals("SET_TRAP")) {
                // Обработка команды установки ловушки
                int playerId = Integer.parseInt(parts[1]);  // Получаем ID игрока
                int x = Integer.parseInt(parts[2]);  // Координаты для установки ловушки
                int y = Integer.parseInt(parts[3]);

                // Проверим, можно ли поставить ловушку в текущей ячейке
                if (player.getScore() >= 3) {// Устанавливаем ловушку
                    player.placeTrap(x, y, maze);
                    player.setScore(player.getScore() - 3);
                    // Отправляем обновленное состояние лабиринта и игрока
                    sendMazeState();
                    sendPlayerState();
                }
            } else if (parts[0].equals("VICTORY")) {
                String winnerUsername = parts[1];
                // Обрабатываем победу игрока с заданным ID
                ClientHandler winningClient = getClientByUsername(winnerUsername);
                if (winningClient != null) {
                    handleVictory(winningClient);
                }
            }
        }

        private ClientHandler getClientByUsername(String username) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client.getPlayer().getUsername() == username) {
                        return client;
                    }
                }
            }
            return null;
        }

        public ServerPlayer getPlayer() {
            return player;
        }

        private static void handleVictory(ClientHandler winningClient) {
            // Уведомляем всех игроков о победе
            String victoryMessage = "PLAYER " + winningClient.getPlayer().getUsername() + " WINS!";
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.sendMessage(victoryMessage);
                }
            }
            // Останавливаем игру (например, можно завершить сессию)
            stopGame();
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        private static void stopGame() {
            System.out.println("Game over!");
            // Можно закрыть все соединения с клиентами
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.stopClient();
                }
            }
        }

    }
}
