package org.example.gui;

import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.DataBaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SimpleWindow extends JFrame {

    private JTextField tfName;
    private JTextField tfEmail;
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnHelp;

    public SimpleWindow() {
        super("Форма ввода данных");

        // Настройка окна
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLayout(new GridLayout(4, 2, 10, 10)); // Сетка для упорядочивания компонентов

        // Используем компоненты для меток и текстовых полей
        LabelWindow labelWindow = new LabelWindow();
        TextFieldWindow textFieldWindow = new TextFieldWindow();
        ButtonWindow buttonWindow = new ButtonWindow();

        // Метки и текстовые поля
        JLabel lblName = labelWindow.createLabel("Имя:");
        tfName = textFieldWindow.createTextField();

        JLabel lblEmail = labelWindow.createLabel("Электронная почта:");
        tfEmail = textFieldWindow.createTextField();

        // Кнопки
        btnSave = buttonWindow.createSaveButton(e -> saveData());
        btnCancel = buttonWindow.createCancelButton(e -> cancelData());
        btnHelp = buttonWindow.createHelpButton(e -> showHelp());

        // Добавляем компоненты в окно
        this.add(lblName);
        this.add(tfName);
        this.add(lblEmail);
        this.add(tfEmail);
        this.add(btnSave);
        this.add(btnCancel);
        this.add(btnHelp);

        // Делаем окно видимым
        this.setVisible(true);
    }

    private void saveData() {
        String name = tfName.getText();
        String email = tfEmail.getText();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } else {
            try (Connection connection = DataBaseConnection.connect()) {
                UserRepository userRepository = new UserRepository(connection);
                UserService userService = new UserService(userRepository);
                userService.saveUserData(name, email);
                JOptionPane.showMessageDialog(this, "Данные сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении данных!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelData() {
        tfName.setText("");
        tfEmail.setText("");
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this, "Введите ваше имя и электронную почту, затем нажмите 'Сохранить'.",
                "Справка", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Запуск приложения в потоке событий
        SwingUtilities.invokeLater(SimpleWindow::new);
    }
}
