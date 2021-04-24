package tictactoe;

import javax.swing.*;
import java.awt.*;

public class TicTacToeOld extends JFrame {
    enum Player {
        E(" "), X("X"), O("O"), D(" ");

        Player(String pName) {
            this.pName = pName;
        }

        final String pName;
    }

    enum State {
        ATTEMPT("Game is not started"),
        PROGRESS("Game in progress"),
        WIN("%s wins"),
        DRAW("Draw");

        State(String message) {
            this.message = message;
        }

        final String message;
    }

    class Desk extends JPanel {
        class Cell extends JButton {
            Player owner;

            Cell(String name) {
                super();
                super.addActionListener(x -> {
                    if (owner == Player.E) {
                        if (State.ATTEMPT == state) {
                            gameStart();
                        }
                        if (State.PROGRESS == state) {
                            --empty;
                            owner = player;
                            setText(owner.pName);
                            nextTurn();
                        }
                    }
                });
                super.setFocusPainted(false);
                super.setName(name);
                super.setVisible(true);
                update();
            }

            void update() {
                owner = player;
                super.setText(owner.pName);
            }
        }
        Desk(int width, int height, int tiling) {
            super();
            super.setSize(width, height);
            super.setLayout(new GridLayout(tiling, tiling));
            super.setVisible(true);

            this.tiling = tiling;
            cells = new Cell[tiling][tiling];
            for (char x = 0; x < tiling; x++) {
                for (int y = 0; y < tiling; y++) {
                    cells[x][y] = new Cell(String.format("Button%c%d", 'A' + y, 3 - x));
                    super.add(cells[x][y]);
                }
            }
            empty = tiling * tiling;
        }

        private Player checkLines() {
            Player p = Player.E;
            for (int x = 0; x < tiling; x++) {
                int cv = 0;
                int ch = 0;
                for (int y = 0; y < tiling; y++) {
                    if (cells[x][y].owner == player) {
                        ++cv;
                    }
                    if (cells[y][x].owner == player) {
                        ++ch;
                    }
                }
                if (cv == tiling || ch == tiling) {
                    p = player;
                    break;
                }
            }
            return p;
        }

        private Player checkDiagonal() {
            Player p = Player.E;
            int prime = 0;
            int sub = 0;
            int limit = tiling - 1;
            for (int n = 0; n <= limit; n++) {
                if (cells[n][n].owner == player) {
                    ++prime;
                }
                if (cells[limit - n][n].owner == player) {
                    ++sub;
                }
            }
            if (tiling == prime || tiling == sub) {
                p = player;
            }
            return p;
        }

        Player check() {
            if (player == checkLines() || player == checkDiagonal()) {
                return player;
            } else if (empty <= 0) {
                return Player.D;
            }
            return Player.E;
        }

        void reset() {
            empty = tiling * tiling;
            for (int x = 0; x < tiling; x++) {
                for (int y = 0; y < tiling; y++) {
                    cells[x][y].update();
                }
            }
        }

        @Override
        public void setFont(Font font) {
            for (int x = 0; x < tiling; x++) {
                for (int y = 0; y < tiling; y++) {
                    cells[x][y].setFont(font);
                }
            }
        }

        final private Cell[][] cells;
        final private int tiling;
        private int empty;
    }

    class StatusBar extends JPanel {
        final JLabel label;
        final JButton button;

        StatusBar() {
            super();

            label = new JLabel(state.message);
            label.setName("LabelStatus");
            label.setVisible(true);
            label.setSize(300, 80);

            button = new JButton("Reset");
            button.setName("ButtonReset");
            button.setVisible(true);
            button.addActionListener(x -> restart());
            button.setSize(120, 80);

            super.setLayout(new BorderLayout());
            super.add(label, BorderLayout.WEST);
            super.add(button, BorderLayout.EAST);
            super.setVisible(true);
            super.setSize(400, 80);
        }
    }
    class MenuBar extends JPanel {
        JButton button1;
        JButton button2;
        JButton button3;

        MenuBar() {
            super();
            Container pane = super.getRootPane();
            pane.setLayout(null);
            //int w = pane.getWidth();
            button1 = new JButton("Button1");
            button2 = new JButton("Button2");
            button3 = new JButton("Button3");
            pane.add(button1);
            pane.add(button2);
            pane.add(button3);
        }
    }
    public TicTacToeOld() {
        super("Tic Tac Toe");
        state = State.ATTEMPT;
        player = Player.E;

        status = new StatusBar();
        desk = new Desk(300, 300, 3);
        deskFont = new Font("Arial", Font.BOLD, 60);
        desk.setFont(deskFont);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(450, 570);
        setResizable(false);
        status.setSize(450, 120);

        setLayout(new BorderLayout());
        add(desk, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setVisible(true);
    }
    void nextTurn() {
        Player res = desk.check();
        if (state == State.PROGRESS && player == res) {
            state = State.WIN;
            status.label.setText(String.format(state.message, player.name()));
        } else if (state == State.PROGRESS && Player.D != res) {
            player = player == Player.X ? Player.O : Player.X;
        } else if (Player.D == res) {
            state = State.DRAW;
            status.label.setText(state.message);
        }
    }
    void gameStart() {
        state = State.PROGRESS;
        player = Player.X;
        status.label.setText(state.message);
    }
    void restart() {
        state = State.ATTEMPT;
        player = Player.E;
        status.label.setText(state.message);
        desk.reset();
    }

    final Desk desk;
    final StatusBar status;
    final Font deskFont;

    Player player;
    State state;
}
