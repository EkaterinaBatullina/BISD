package org.example.client;

import lombok.Getter;
import lombok.Setter;
import org.example.server.ServerPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
@Setter
@Getter
public class MazePanel extends JPanel implements KeyListener {

    private final int cellSize = 30;
    private final int mazeWidth = 20;
    private final int mazeHeight = 20;
    private SlowdownDialog slowdownDialog = null;

    private Maze maze;
    private ClientPlayer clientPlayer;
    private ServerPlayer serverPlayer;

    private JButton upButton, downButton, leftButton, rightButton;
    private Timer timer;
    private ImageIcon pointIcon;
    private Component thisComponent;
    private JButton placeTrapButton;
    private int currentSlowdownTime = 0;
    private String victoryMessage = null;

    // Конструктор теперь принимает клиентскую часть
    public MazePanel(ClientPlayer clientPlayer, ServerPlayer serverPlayer, Maze maze, Component thisComponent) {
        if (maze == null) {
            throw new IllegalArgumentException("Maze cannot be null");
        }
        this.clientPlayer = clientPlayer;
        this.serverPlayer = serverPlayer;
        this.maze = maze;
        this.thisComponent = thisComponent;

       // createButtons();
        initTimer();
        //initTimerr();

        pointIcon = new ImageIcon("point_icon.png");
        setLayout(null);
       // positionButtons();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

//        slowdownTimerLabel = new SlowdownTimerLabel(0); // создаем label
//        slowdownTimerLabel.setVisible(false);
//        add(slowdownTimerLabel);
        int windowWidth = mazeWidth * cellSize;  // +200 для кнопок и пространства
        int windowHeight = mazeHeight * cellSize;  // +100 для кнопок и информации

        setPreferredSize(new Dimension(windowWidth, windowHeight));
    }

//
//    private void createButtons() {
//        placeTrapButton = new JButton("Установить ловушку");
//        placeTrapButton.addActionListener(e -> {
//            if (serverPlayer.getScore() >= 3) {
//                serverPlayer.placeTrap(serverPlayer.getX(), serverPlayer.getY(), maze);
//                serverPlayer.setScore(serverPlayer.getScore() - 3);
//            } else {
//                JOptionPane.showMessageDialog(this, "Недостаточно очков для установки ловушки!");
//            }
//        });
//        placeTrapButton.setBounds(mazeWidth * cellSize + 30, 300, 150, 50);
//    }

    private void initTimer() {
        timer = new Timer(50, e -> {
            maze.getPoint().updateGlowState();
            repaint();
        });
        timer.start();
    }

//    private void initTimerr() {
//        timer = new Timer(1000, e -> {
//            // Уменьшаем таймер замедления, если игрок замедлен
//            if (serverPlayer.isSlowed()) {
//                serverPlayer.decreaseSlowdownTimer();
//            }
//            // Обновляем состояние "светящегося" элемента (если нужно)
//            maze.getPoint().updateGlowState();
//            // Перерисовываем панель
//            repaint();
//        });
//        timer.start();
//    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Проверьте, что clientPlayer не null перед вызовом его методов
        if (clientPlayer != null) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    clientPlayer.move(0, -1);
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    clientPlayer.move(0, 1);
                    break;
                case KeyEvent.VK_LEFT:
                    clientPlayer.move(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    clientPlayer.move(1, 0);
                    break;
                case KeyEvent.VK_SPACE:
                    if (serverPlayer.getScore() >= 3) {
                        serverPlayer.placeTrap(serverPlayer.getX(), serverPlayer.getY(), maze);
                        serverPlayer.setScore(serverPlayer.getScore() - 3);
                    } else {
                        //showTimedMessage(thisComponent, "Недостаточно очков для установки ловушки!");
                        JOptionPane.showMessageDialog(this, "Недостаточно очков для установки ловушки!");
                    }
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("clientPlayer is null!");
        }
    }

//    private void showTimedMessage(Component parent, String message) {
//        System.out.println(message);
//        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Сообщение", true); // получаем родительское окно
//        JLabel messageLabel = new JLabel(message);
//        JLabel timerLabel = new JLabel("");
//        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
//        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.add(messageLabel);
//        panel.add(timerLabel);
//        dialog.add(panel);
//
//        dialog.pack();
//        dialog.setLocationRelativeTo(parent);
//        dialog.setVisible(true);
//
//        final int[] remainingSeconds = {5};
//        Timer timer = new Timer(1000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (remainingSeconds[0] > 0) {
//                    timerLabel.setText("Закроется через: " + remainingSeconds[0] + " секунд");
//                    remainingSeconds[0]--;
//                } else {
//                    ((Timer) e.getSource()).stop();
//                    dialog.dispose();
//                }
//            }
//        });
//        timer.start();
//    }


    @Override
    public void keyReleased(KeyEvent e) {}

    private void positionButtons() {
        placeTrapButton.setBounds(mazeWidth * cellSize + 30, 100, 150, 50);
        this.add(placeTrapButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze(g);
        // Рисуем игрока только если clientPlayer инициализирован
        drawPlayerView(g);  // Отрисовываем игрока
        for (int i = Math.max(0, serverPlayer.getY() - 1); i <= Math.min(mazeHeight - 1, serverPlayer.getY() + 1); i++) {
            for (int j = Math.max(0, serverPlayer.getX() - 1); j <= Math.min(mazeWidth - 1, serverPlayer.getX() + 1); j++) {
                if (maze.getPoint().getPoints()[i][j] == 1) {
                    int pointX = j * cellSize + cellSize / 2;
                    int pointY = i * cellSize + cellSize / 2;
                    int pointSize = 10;
                    drawStar(g, pointX, pointY, pointSize);
                }
            }
        }

        int starIconX = mazeWidth * cellSize - 30;
        int starIconY = 30;

        drawStar(g, starIconX, starIconY, 20);
        g.setColor(Color.BLACK);
        g.drawString("" + serverPlayer.getScore(), mazeWidth * cellSize - 40, 35);
        if (serverPlayer.isSlowed()) {
            System.out.println(222222 + " " + serverPlayer.getSlowdownTimer());
            g.setColor(Color.RED);
            g.drawString("Ловушка, осталось: " + serverPlayer.getSlowdownTimer() + " с.", mazeWidth * cellSize - 170, mazeWidth * cellSize - 10);
          }
//        if (serverPlayer.isSlowed()) {
//            System.out.println(222222 + " " + serverPlayer.getSlowdownTimer());
//            showSlowdownDialog(serverPlayer.getSlowdownTimer());
//
//        } else {
//            if (slowdownDialog != null && slowdownDialog.isVisible()){
//                slowdownDialog.dispose();
//                slowdownDialog = null;
//            }
//            currentSlowdownTime = 0;
//        }
    }


    private void showSlowdownDialog(int slowdownTimer) {
        if (slowdownDialog == null || !slowdownDialog.isVisible()) {
            slowdownDialog = new SlowdownDialog((Frame) SwingUtilities.getWindowAncestor(thisComponent), slowdownTimer);
            slowdownDialog.setVisible(true);

        } else {
            slowdownDialog.dispose();
            slowdownDialog = new SlowdownDialog((Frame) SwingUtilities.getWindowAncestor(thisComponent), slowdownTimer);
            slowdownDialog.setVisible(true);
        }

    }
    public int getCellSize() {
        if (maze == null) return 20;
        int rows = maze.getHeight();
        int cols = maze.getWidth();

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int cellWidth = panelWidth / cols;
        int cellHeight = panelHeight / rows;
        return Math.min(cellWidth, cellHeight);
    }

    private void drawMaze(Graphics g) {
        maze.draw(g, cellSize);
    }

    private void drawVictoryMessage(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(victoryMessage, 200, 200);  // Можно изменить координаты, чтобы текст был в нужном месте
    }

    public void displayVictoryMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        //this.victoryMessage = message;
       // repaint();  // Перерисовываем панель, чтобы отобразить сообщение
    }
    public void drawPlayerView(Graphics g) {
        if (clientPlayer != null) {
            clientPlayer.draw(g, cellSize, maze.getPoint());
        }
    }

//    public void drawPlayerView(Graphics g) {
//        if (clientPlayer != null) {
//            clientPlayer.draw(g, mazePanel.getCellSize(), mazePanel.getPoint());
//        }
//    }

    private void drawStar(Graphics g, int pointX, int pointY, int pointSize) {
        maze.getPoint().drawStarGlow(g, pointX, pointY, pointSize);
        maze.getPoint().drawStar(g, pointX, pointY, pointSize);
    }


    public void updateMazeState(Maze maze) {
        // Обновляем текущий лабиринт
//        this.maze.setMaze(newMaze);
//        this.maze.getTrap().setTraps(newTrap);
//        this.maze.getPoint().setPoints(newPoint);
        this.maze = maze;
        repaint();  // Перерисовываем панель после обновления лабиринта
    }

    // Метод для обновления состояния игрока
    public void updatePlayerState(int playerX, int playerY, int score, boolean slowed, int slowdownTimer) {
        this.serverPlayer.setX(playerX);
        this.serverPlayer.setY(playerY);
        this.serverPlayer.setScore(score);
        this.serverPlayer.setSlowed(slowed);
        this.serverPlayer.setSlowdownTimer(slowdownTimer);
        repaint();
    }

    public void updateScore() {
        repaint();  // Перерисовываем панель, чтобы показать новые очки
    }

}
