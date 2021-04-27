package tictactoe;

import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.plaf.FontUIResource;

//import tictactoe.GameHandleOld.Owner;

import java.awt.GridLayout;
import java.awt.BorderLayout;

import java.awt.Font;

import javax.swing.border.EtchedBorder;



class MenuPanel extends JPanel {

    class Button extends JButton {
        Consumer<Button> handle;

        Button(String name, String text) {
            super(text);
            handle = null;

            setName(name);
            setVisible(true);
            setFocusPainted(false);
            addActionListener(x -> {
                if (handle != null) {
                    handle.accept(this);
                }
            });
        }
    }

    MenuPanel(int width, int height) {
        super();

        player1 = new Button("ButtonPlayer1", "Human");
        player2 = new Button("ButtonPlayer2", "Human");
        reset = new Button("ButtonStartReset", "Start");

        setLayout(new GridLayout(1, 3));
        
        add(player1);
        add(reset);
        add(player2);
        //
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setBounds(0, 0, width, height);
    }

    @Override
    public void setEnabled(boolean enabled) {
        player1.setEnabled(enabled);
        player2.setEnabled(enabled);

        reset.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    Button player1;
    Button player2;
    Button reset;
}

class StatusBar extends JPanel {
    
    StatusBar(int width, int height) {
        super();
        message = new JLabel();
        message.setText("Status");
        message.setName("LableStatus");

        setLayout(new BorderLayout());
        add(message, BorderLayout.SOUTH);
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        message.setLocation(10, 0);
        message.setVisible(true);
        message.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        
        setSize(width, height);
        setBounds(0, 0, width, height);
    }
    void update(String message) {
        this.message.setText(message);
    }
    JLabel message;
}

class Cell extends JButton {
    final int x;
    final int y;
    Owner owner = Owner.E;
    Consumer<Cell> handle;

    Cell(int x, int y, String name) {
        super();
        this.x = x;
        this.y = y;
        setName(name);
        setFocusPainted(false);
        setFont(new FontUIResource("Arial", FontUIResource.BOLD, 75));
        setVisible(true);
        addActionListener(a -> {
            if (handle != null) {
                handle.accept(this);
            }
        });
    }
    void update(Owner owner) {
        this.owner = owner;
        setText(owner.sname);
    }
    void update(Owner owner, boolean b) {
        this.owner = owner;
        setText(owner.sname);
        setEnabled(b);
    }
}

class Desk extends JPanel {

    Desk(int tx, int ty) {
        super();
        list = new Cell[tx * ty];
        map = new Cell[tx][ty];

        int n = tx * ty;
        setLayout(new GridLayout(tx, ty));
        for (int i = 0; i < n; i++) {
            int x = i / tx;
            int y = i % tx;
            String name = String.format("Button%c%d", 'A' + y, tx - x);
            Cell c = new Cell(x, y, name);
            list[i] = c;
            map[x][y] = c;
            add(c);
        }
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
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

    @Override
    public void setFont(Font font) {
        if (list != null) {
            for (JButton b : list) {
                b.setFont(font);
            }
        }
        super.setFont(font);
    }

    public void update(Owner owner, boolean b) {
        for (Cell a : list) {
            a.update(owner, b);
        }
    }

    final Cell[][] map;
    final Cell[] list;
}

public class Window extends JFrame {
    final MenuPanel panel;
    final Desk desk;
    final StatusBar status;

    Window() {
        super("Window");
        int w = 150 * 3;
        int mh = 30;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MenuPanel(w, mh);
        desk = new Desk(3, 3);
        status = new StatusBar(w, mh);

        setLayout(new BorderLayout());

        add(panel, BorderLayout.NORTH);
        add(desk, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setResizable(false);
        setSize(450, 510);
        setVisible(true);
        desk.update(Owner.E, false);
    }
}