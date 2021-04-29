package tictactoe;

import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

class Desk extends JPanel {

    Desk(int tx, int ty) {
        super();
        list = new JButton[tx * ty];

        int n = tx * ty;
        setLayout(new GridLayout(tx, ty));
        for (int i = 0; i < n; i++) {
            int x = i / tx;
            int y = i % tx;
            String name = String.format("Button%c%d", 'A' + y, tx - x);
            list[i] = new JButton(" ");
            list[i].setName(name);
            list[i].setFocusPainted(false);
            add(list[i]);
        }
        setFont(new FontUIResource("Arial", FontUIResource.BOLD, 75));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (JButton b : list) {
            b.setEnabled(false);
        }
        super.setEnabled(enabled);
    }

    @Override
    public void setFont(Font font) {
        if (list != null) {
            for (JButton b : list) {
                b.setFont(font);
            }
        }
        super.setFont(font);
    }

    final JButton[] list;
}
