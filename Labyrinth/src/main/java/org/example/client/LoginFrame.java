package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JTextArea infoArea;

    public LoginFrame() {
        frame = new JFrame("Login Screen");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Панель для содержимого
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10)); // Добавим отступы между элементами
        panel.setBackground(new Color(245, 245, 245)); // Легкий серый фон панели

        // Заголовок для экрана входа
//        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        titlePanel.setBackground(new Color(245, 245, 245)); // Сделайте фон таким же
//        JLabel titleLabel = new JLabel("Login");
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));  // Крупный шрифт для заголовка
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);
        panel.add(new JLabel("")); // Пустой элемент для отступа

        // Поле для ввода имени пользователя
        JLabel usernameLabel = new JLabel("Username:", JLabel.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(usernameField);

        // Поле для ввода пароля
        JLabel passwordLabel = new JLabel("Password:", JLabel.CENTER);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(passwordField);

        // Кнопка для входа
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));  // Кнопка с жирным шрифтом
        loginButton.setBackground(new Color(0, 123, 255)); // Синий цвет кнопки
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204))); // Темно-синий кант
        loginButton.setPreferredSize(new Dimension(180, 40));  // Увеличим кнопку
        loginButton.addActionListener(e -> handleLogin());

        // Кнопка для регистрации
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(40, 167, 69)); // Зеленый цвет кнопки
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34))); // Темно-зеленый кант
        registerButton.setPreferredSize(new Dimension(180, 40));  // Увеличим кнопку
        registerButton.addActionListener(e -> handleRegistration());

        panel.add(loginButton);
        panel.add(registerButton);

        // Добавляем панель на фрейм
        frame.add(panel, BorderLayout.CENTER);

        // Для отображения ошибок или информации
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(245, 245, 245)); // Легкий серый фон
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Поменяли шрифт
        infoArea.setForeground(Color.DARK_GRAY);
        frame.add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        // Отображаем окно
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Центрируем окно
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Логика проверки логина через БД
        if (DBHelper.checkUserCredentials(username, password)) {
            frame.dispose();
            // Переход к следующему окну
            new Client(username);
            //new PreGameFrame(username);
            //frame.dispose();  // Закрываем окно входа
        } else {
            infoArea.setText("Invalid username or password.");
        }
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Логика регистрации через БД
        if (DBHelper.registerUser(username, password)) {
            infoArea.setText("Registration successful! You can now log in.");
        } else {
            infoArea.setText("Registration failed. Username may already be taken.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
