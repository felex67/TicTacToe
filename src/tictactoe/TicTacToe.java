package tictactoe;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
        PROCESS("The turn of %s Player (%s)"),
        WIN("The %s Player (%s) wins"),
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
        HUMAN("Human"), COMPUTER("Robot");
        PlayerType(String text) {
            this.text = text;
        }
        final String text;
    }

    static class MenuBar extends JMenuBar {
        static class MenuGame extends JMenu {
            MenuGame(String name) {
                super(name);
                setName("Menu" + name);
                setVisible(true);

                JMenuItem[] list = {
                        hh = new JMenuItem("Human vs. Human"),
                        hr = new JMenuItem("Human vs. Robot"),
                        rh = new JMenuItem("Robot vs. Human"),
                        rr = new JMenuItem("Robot vs. Robot")
                };
                exit = new JMenuItem("Exit");

                hh.setName("MenuHumanHuman");
                hr.setName("MenuHumanRobot");
                rh.setName("MenuRobotHuman");
                rr.setName("MenuRobotRobot");
                exit.setName("MenuExit");

                for (JMenuItem i : list) {
                    i.setVisible(true);
                    add(i);
                }

                addSeparator();

                add(exit);
                exit.setVisible(true);
            }

            final JMenuItem hh;
            final JMenuItem hr;
            final JMenuItem rh;
            final JMenuItem rr;
            final JMenuItem exit;
        }
        MenuBar() {
            super();
            mGame = new MenuGame("Game");
            add(mGame);
            setVisible(true);
        }
        final MenuGame mGame;
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

        final Button player1;
        final Button player2;
        final Button reset;
    }

    static class StatusBar extends JPanel {

        StatusBar(int width, int height) {
            super();
            message = new JLabel();
            message.setText("Status");
            message.setName("LabelStatus");

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

    class Round {

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
            player = pType[0];
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
            try {
                Thread.sleep(200L);
            } catch (Exception e) {
                System.err.println("Exception: " + e.toString());
            }
        }

        void start() {
            reset();
            state = State.PROCESS;
            hWind.updateState();
            if (player == PlayerType.COMPUTER) {
                playRound();
            }
        }

        void playRound() {
            while (state == State.PROCESS) {
                if (player == PlayerType.COMPUTER) {
                    aiMakeMove();
                }
                checkBoard();
                if (State.PROCESS == state) {
                    nextStep();
                    if (player != PlayerType.COMPUTER) {
                        break;
                    }
                } else {
                    hWind.updateState();
                    break;
                }
            }
        }

        void nextStep() {
            ++step;
            owner = owner == Owner.X ? Owner.O : Owner.X;
            player = pType[step % 2];
            hWind.updateStatus();
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
                if (state == State.PROCESS && avail.size() <= 0) {
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
                    if (c.owner == Owner.E) {
                        round.apply(x);
                        round.playRound();
                    }
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
            round.reset();
            state = State.ATTEMPT;
            updateState();
        }

        void updateState() {
            if (State.PROCESS == state) {
                updateStart();
            } else if (State.DRAW == state) {
                updateDraw();
            } else if (State.WIN == state) {
                updateWin();
            } else if (State.ATTEMPT == state) {
                updateReset();
            }

        }

        private void updateReset() {
            panel.setEnabled(true);
            panel.reset.setText("Start");
            desk.update(Owner.E, false);
            status.update(state.message);
        }

        private void updateStart() {
            panel.player1.setEnabled(false);
            panel.player2.setEnabled(false);
            panel.reset.setText("Reset");
            desk.update(Owner.E, true);
            updateStatus();
        }

        private void updateWin() {
            desk.setEnabled(false);
            status.update(
                    String.format(
                            state.message,
                            player.text,
                            owner.sName
                    )
            );;
        }

        private void updateDraw() {
            desk.setEnabled(false);
            status.update(state.message);
        }

        void updateStatus() {
            status.update(
                    String.format(
                            State.PROCESS.message,
                            player.text,
                            owner.sName
                    )
            );
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
            gameStart();
        } else {
            gameReset();
        }
    }

    void gameStart() {
        round.start();
    }

    void gameReset() {
        round.reset();
        state = State.ATTEMPT;
        hWind.updateState();
    }

    void initMenu() {
        menu.mGame.hh.addActionListener(x -> setGameType(PlayerType.HUMAN, PlayerType.HUMAN));
        menu.mGame.hr.addActionListener(x -> setGameType(PlayerType.HUMAN, PlayerType.COMPUTER));
        menu.mGame.rh.addActionListener(x -> setGameType(PlayerType.COMPUTER, PlayerType.HUMAN));
        menu.mGame.rr.addActionListener(x -> setGameType(PlayerType.COMPUTER, PlayerType.COMPUTER));
        menu.mGame.exit.addActionListener(x -> System.exit(0));
    }

    void setGameType(PlayerType p1, PlayerType p2) {
        gameReset();
        pType[0] = p1;
        pType[1] = p2;
        panel.player1.setText(p1.text);
        panel.player2.setText(p2.text);
        gameStart();
    }

    void updatePanel() {
        if (state == State.ATTEMPT) {
            panel.player1.setText(pType[0].text);
            panel.player1.setEnabled(true);
            panel.player2.setText(pType[1].text);
            panel.player2.setEnabled(true);
        }
    }

    public TicTacToe() {
        super("Tic Tac Toe");
        int w = 150 * 3;
        int mh = 30;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        menu = new MenuBar();
        setJMenuBar(menu);
        menu.setVisible(true);
        panel = new MenuPanel(w, mh);
        desk = new Desk(3, 3);
        status = new StatusBar(w, mh);

        initMenu();

        setLayout(new BorderLayout());

        add(panel, BorderLayout.NORTH);
        add(desk, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        setResizable(false);
        setSize(450, 510);
        setVisible(true);

        desk.update(Owner.E, false);
        round = new Round();
        hWind = new WinHandle();
    }

    State state = State.ATTEMPT;
    Owner owner = Owner.X;
    PlayerType player = PlayerType.HUMAN;
    int step = 0;

    final PlayerType[] pType = {PlayerType.HUMAN, PlayerType.HUMAN};
    final Round round;
    final WinHandle hWind;

    final MenuBar menu;
    final MenuPanel panel;
    final Desk desk;
    final StatusBar status;
}