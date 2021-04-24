package tictactoe;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.FontUIResource;

import java.awt.Font;
import java.awt.GridLayout;


class Cell extends JButton {

    final int x;
    final int y;
    Game.Side owner;

    Cell(int x, int y, String name) {
        super(Game.Side.E.title);
        this.x = x;
        this.y = y;
        owner = Game.Side.E;
        setName(name);
        setFocusPainted(false);
        setVisible(true);
    }

    void update(Game.Side owner, boolean lock) {
        this.owner = owner;
        setText(owner.title);
        setEnabled(lock);
    }
}

class Desk extends JPanel {

    Desk(int cellSize, int tiling) {
        super();
        setLayout(new GridLayout(tiling, tiling));
        map = new Cell[tiling][tiling];
        for (int i = 0; i < tiling; i++) {
            for (int j = 0; j < tiling; j++) {
                Cell b = new Cell(i, j, String.format("Button%c%d", 'A' + j, tiling - i));
                map[i][j] = b;
                add(b);
                b.setFont(new FontUIResource("Arial", Font.BOLD, cellSize / 2));
            }
        }
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setBounds(0, 0, cellSize * tiling, cellSize * tiling);
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                map[x][y].setEnabled(enabled);
            }
        }
        super.setEnabled(enabled);
    }

    void reset() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map.length; y++) {
                map[x][y].update(Game.Side.E, false);
            }
        }
    }

    final Cell[][] map;
}
