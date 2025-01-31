package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.System.out;

public class PreGameFrame {
    private JFrame frame;
    private String username;
    private JComboBox<String> difficultyComboBox;
    private JButton startButton;
    private JButton helpButton;
    private PrintWriter out;
    private boolean isReady = false;

    public PreGameFrame(String username, PrintWriter out) {
        this.username = username;
        this.out = out;
        frame = new JFrame("Pre-Game Screen");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Панель для центра
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10)); // Отступы между элементами
        panel.setBackground(new Color(245, 245, 245)); // Легкий серый фон панели

        // Заголовок с приветствием
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));  // Увеличен размер шрифта
        welcomeLabel.setForeground(Color.BLACK);
        panel.add(welcomeLabel);

        // Дропдаун для выбора сложности
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Увеличен шрифт
        difficultyComboBox.setBackground(Color.WHITE);
        difficultyComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        difficultyComboBox.setPreferredSize(new Dimension(150, 40));  // Сделаем его более высоким
        panel.add(difficultyComboBox);

        // Кнопка помощи
        helpButton = new JButton("Help");
        helpButton.setFont(new Font("Arial", Font.PLAIN, 14));
        helpButton.setBackground(new Color(230, 230, 230)); // Светлый серый фон
        helpButton.setForeground(Color.BLACK);
        helpButton.setFocusPainted(false);
        helpButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        helpButton.addActionListener(e -> showHelp());

        // Эффект при наведении на кнопку Help
        helpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                helpButton.setBackground(new Color(210, 210, 210)); // Тёмно-серый при наведении
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                helpButton.setBackground(new Color(230, 230, 230)); // Возвращаем обратно
            }
        });
        panel.add(helpButton);

        // Кнопка начала игры
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));  // Сделаем шрифт жирным и крупным
        startButton.setBackground(new Color(0, 123, 255)); // Синий цвет кнопки
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204))); // Темно-синий кант
        startButton.setPreferredSize(new Dimension(180, 40));  // Сделаем кнопку шире и выше
        startButton.addActionListener(e -> startGame());

        // Эффект при наведении на кнопку Start
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(0, 102, 204)); // Темно-синий при наведении
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(0, 123, 255)); // Возвращаем цвет
            }
        });
        panel.add(startButton);

        // Добавляем панель на фрейм
        frame.add(panel, BorderLayout.CENTER);

        // Дополнительная информация или пояснения снизу
        JTextArea infoArea = new JTextArea("Please choose your difficulty and click 'Start Game'.");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(245, 245, 245));
        infoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        infoArea.setForeground(Color.DARK_GRAY);
        frame.add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        // Отображаем окно
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Центрируем окно
    }

    // Показать справку
    private void showHelp() {
        JOptionPane.showMessageDialog(frame, "Game Rules:\n\n" +
                "1. Move through the maze using arrow keys.\n" +
                "2. Avoid traps and solve the maze to win.", "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    // Запуск игры
    private void startGame() {
        if (!isReady) {
            isReady = true;
            startButton.setText("Waiting for other player...");
            startButton.setEnabled(false);  // Блокируем кнопку до тех пор, пока второй игрок не нажмет
        }

        // Получаем выбранную сложность
        String difficulty = (String) difficultyComboBox.getSelectedItem();

        // Передаем данные в Client (создаем клиент и передаем username и сложность)
        //new Client(username); // Создаем клиента с необходимыми параметрами

        // Отправляем данные на сервер
        notifyServerGameReady(username, difficulty);
        // Ждем второго игрока
    }

    // Отправка данных на сервер (пока что заглушка)
    private void notifyServerGameReady(String username, String difficulty) {
        // Отправка сообщения на сервер с готовностью игрока и его именем
        out.println("READY " + username + " " + difficulty);  // например: "READY Player1 Easy"
        // Логика отправки данных на сервер
    }

    // Запуск экрана перед игрой для тестирования
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new PreGameFrame("Player1"));
//    }

    // Обработка состояния готовности игры
    public void handleGameReady() {
        System.out.println("Start Game");
        startButton.setText("Start Game");
        startButton.setEnabled(true);  // Разблокируем кнопку после получения подтверждения от сервера
    }

}
