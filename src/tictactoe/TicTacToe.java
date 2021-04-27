package tictactoe;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.plaf.FontUIResource;
import javax.swing.border.EtchedBorder;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TicTacToe extends JFrame {

    enum State {
        ATTEMPT("Game is not started"),
        PROCESS("Game not finished"),
        WIN(" wins"),
        DRAW("Draw"),
        EXIT("");
        State(String message) {
            this.message = message;
        }
        final String message;
    }
    
    enum Owner {
    
        E(" "), X("X"), O("O");
    
        Owner(String name) {
            this.sName = name;
        }
    
        final String sName;
    }    

    enum PlayerType {
        HUMAN("Humnan"), COMPUTER("Computer");
        PlayerType(String text) {
            this.text = text;
        }
        final String text;
    }

    static class MenuPanel extends JPanel {

        static class Button extends JButton {
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
    
    static class StatusBar extends JPanel {
    
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
    
    static class Cell extends JButton {
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
            setText(owner.sName);
        }
        void update(Owner owner, boolean b) {
            this.owner = owner;
            setText(owner.sName);
            setEnabled(b);
        }
    }
    
    static class Desk extends JPanel {
    
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
    
    private class Round {
    
        Round() {
            rand = new Random();
            map = desk.map;
            origin = desk.list;
            reset();
        }
    
        void reset() {
            avail.clear();
            for (Cell x : origin) {
                x.update(Owner.E);
                avail.add(x);
            }
            state = State.ATTEMPT;
            owner = Owner.X;
            step = 0;
        }
        void apply(Cell c) {
            if (null != c && avail.contains(c)) {
                c.update(owner);
                avail.remove(c);
            }
        }
        void aiMakeMove() {
            apply(avail.get(rand.nextInt(avail.size())));
        }
        boolean checkAi() {
            return pType[step % 2] == PlayerType.COMPUTER;
        }
        void start() {
            if (checkAi()) {
                playRound();
            }
        }
        void playRound() {
            while (true) {
                if (checkAi()) {
                    aiMakeMove();
                }
                checkBoard();
                if (State.PROCESS == state) {
                    ++step;
                    owner = (Owner.X == owner ? Owner.O : Owner.X);
                    if (!checkAi()) {
                        break;
                    }
                } else {
                    hWind.update();
                    break;
                }
            }
        }

        void checkLines() {
            for (int x = 0; x < map.length; x++) {
                int ch = 0;
                int cv = 0;
                for (int y = 0; y < map.length; y++) {
                    if (map[x][y].owner == owner) {
                        ++ch;
                    }
                    if (map[y][x].owner == owner) {
                        ++cv;
                    }
                }
                if (ch == map.length || cv == map.length) {
                    state = State.WIN;
                    break;
                }
            }
        }

        void checkDia() {
            int m = 0;
            int n = 0;
            int offset = map.length - 1;
            for (int x = 0; x < map.length; x++) {
                if (map[x][x].owner == owner) {
                    ++m;
                }
                if (map[offset - x][x].owner == owner) {
                    ++n;
                }
            }
            if (map.length == n || map.length == m) {
                state = State.WIN;
            }
        }

        void checkBoard() {
            checkLines();
            if (state == State.PROCESS) {
                checkDia();
                if (avail.size() <= 0) {
                    state = State.DRAW;
                }
            }
        }

        int step = 0;
    
        final Random rand;
        final ArrayList<Cell> avail = new ArrayList<>();
        final Cell[][] map;
        final Cell[] origin;
    }

    class WinHandle {

        WinHandle() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    state = State.EXIT;
                    super.windowClosing(e);
                }
            });
            for (Cell c : desk.list) {
                c.handle = x -> {
                    round.apply(x);
                    round.playRound();
                };
            }
            panel.player1.handle = x -> {
                togglePlayerAi(0);
                updatePlayer1();
            };
            panel.player2.handle = x -> {
                togglePlayerAi(1);
                updatePlayer2();
            };
            panel.reset.handle = x -> playerClickStartReset();
        }
        void update() {
            if (State.PROCESS == state) {
                panel.player1.setEnabled(false);
                panel.player2.setEnabled(false);
                panel.reset.setText("Reset");
                desk.setEnabled(true);
            } else if (State.DRAW == state) {
                desk.setEnabled(false);
                status.update(state.message);
            } else if (State.WIN == state) {
                desk.setEnabled(false);
                
            } else if (State.ATTEMPT == state) {
                panel.setEnabled(true);
                desk.update(Owner.E, false);
                panel.reset.setText("Start");
            }
            status.update(state == State.WIN ? owner.sName + state.message : state.message);
        }
        void updatePlayer1() {
            panel.player1.setText(pType[0].text);
        }
        void updatePlayer2() {
            panel.player2.setText(pType[1].text);
        }
    }

    void togglePlayerAi(int id) {
        pType[id] = pType[id] == PlayerType.HUMAN ? PlayerType.COMPUTER : PlayerType.HUMAN;
    }
    void playerClickStartReset() {
        if (State.ATTEMPT == state) {
            state = State.PROCESS;
            hWind.update();
            round.start();
        } else {
            state = State.ATTEMPT;
            round.reset();
            hWind.update();
        }
    }

    public TicTacToe() {
        super("Tic Tac Toe");
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
        hWind = new WinHandle();
        round = new Round();
    }

    State state = State.ATTEMPT;
    Owner owner = Owner.X;

    final PlayerType[] pType = {PlayerType.HUMAN, PlayerType.HUMAN};
    final Round round;
    final WinHandle hWind;
    final MenuPanel panel;
    final Desk desk;
    final StatusBar status;
}