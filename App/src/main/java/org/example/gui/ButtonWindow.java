package org.example.gui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ButtonWindow extends JFrame {

    public JButton createSaveButton(ActionListener actionListener) {
        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(actionListener);
        return saveButton;
    }

    public JButton createCancelButton(ActionListener actionListener) {
        JButton cancelButton = new JButton("Отменить");
        cancelButton.addActionListener(actionListener);
        return cancelButton;
    }

    public JButton createHelpButton(ActionListener actionListener) {
        JButton helpButton = new JButton("Справка");
        helpButton.addActionListener(actionListener);
        return helpButton;
    }
}
