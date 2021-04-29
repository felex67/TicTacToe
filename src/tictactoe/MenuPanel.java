package tictactoe;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;

class MenuPanel extends JPanel {

    MenuPanel() {
        super();

        players = new JButton[2]; //("ButtonPlayer1");
        for (int i = 0; i < 2; i++) {
            JButton b = new JButton();
            b.setName("ButtonPlayer" + (i + 1));
            b.setVisible(true);
            b.setFocusPainted(false);
            players[i] = b;
        }
        
        reset = new JButton();
        reset.setName("ButtonStartReset");
        reset.setVisible(true);
        reset.setFocusPainted(false);

        setLayout(new GridLayout(1, 3));

        add(players[0]);
        add(reset);
        add(players[1]);
        //
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    }

    final JButton[] players;
    final JButton reset;
}
