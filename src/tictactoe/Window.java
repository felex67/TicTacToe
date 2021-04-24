package tictactoe;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EtchedBorder;


class MenuButton extends JButton {
    final int id;
    MenuButton(int id, String name, String text) {
        super(text);
        this.id = id;
        setName(name);
        setVisible(true);
        setFocusPainted(false);
    }
}

class MenuBoard extends JPanel {
    MenuButton player1;
    MenuButton player2;
    MenuButton reset;

    MenuBoard(int width, int height) {
        super();

        player1 = new MenuButton(0, "ButtonPlayer1", "Human");
        player2 = new MenuButton(1, "ButtonPlayer2", "Human");
        reset = new MenuButton(2, "ButtonStartReset", "Start");

        setLayout(new GridLayout(1, 3));
        
        add(player1);
        add(reset);
        add(player2);
        //
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setBounds(0, 0, width, height);
    }
}

class StatusBar extends JPanel {
    
    StatusBar(int width, int height) {
        super();
        message = new JLabel();
        message.setName("LableStatus");

        setLayout(new BorderLayout());
        add(message, BorderLayout.SOUTH);
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        message.setLocation(10, 0);
        message.setVisible(true);
        message.setHorizontalTextPosition(SwingConstants.LEFT);
        
        setSize(width, height);
        setBounds(0, 0, width, height);
    }

    JLabel message;
}

public class Window extends JFrame {
    MenuBoard panel;
    Desk desk;
    StatusBar status;
    Game game;
    Thread pThread;

    Window(int cellSize) {
        super("Window");
        int w = cellSize * 3;
        int mh = 30;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MenuBoard(w, mh);
        desk = new Desk(cellSize, 3);
        status = new StatusBar(w, mh);

        setLayout(new BorderLayout());

        add(panel, BorderLayout.NORTH);
        add(desk, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setResizable(false);
        setSize(450, 510);
        setVisible(true);
        desk.reset();
        game = new Game(this);
        pThread = new Thread(game);
    }
}