package tictactoe;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;
import javax.swing.JPanel;
import javax.swing.JLabel;

class StatusBar extends JPanel {

    StatusBar() {
        super();
        message = new JLabel();
        message.setName("LabelStatus");

        setLayout(new BorderLayout());
        add(message, BorderLayout.SOUTH);
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        message.setVisible(true);
        message.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    }

    void update(String message) {
        this.message.setText(message);
    }
    JLabel message;
}
