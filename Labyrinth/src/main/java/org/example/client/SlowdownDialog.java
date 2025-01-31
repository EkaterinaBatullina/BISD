package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlowdownDialog extends JDialog {
    private JLabel timerLabel;
    private int[] slowdownTimer;
    private Timer timer;

    public SlowdownDialog(Frame parent, int initialSlowdownTimer) {
        super(parent, "Ловушка!", false);
        this.slowdownTimer = new int[]{initialSlowdownTimer};
        timerLabel = new JLabel();
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        updateText();
        add(timerLabel);
        pack();
        setLocationRelativeTo(parent); // Центрируем относительно родительского окна

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slowdownTimer[0]--;
                updateText();
                if (slowdownTimer[0] <= 0) {
                    ((Timer) e.getSource()).stop();
                    dispose(); // Закрываем диалог
                }
            }
        });

        timer.start();
    }

    private void updateText(){
        timerLabel.setText("Ловушка, осталось: " + slowdownTimer[0] + " с.");
    }
}