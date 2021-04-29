package tictactoe;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class iTicTacToe extends JFrame {

    class Summary {
        int step;
        int state;
        int curPlayer;
        int curSide;
        final int[] players = {0, 0};
    }
    class GameHandle {

        private class Move {

            Move(int x, int y) {
                this.x = x;
                this.y = y;
            }
        
            final int x;
            final int y;
        }
        
        GameHandle(int tx, int ty) {
            int n = tx * ty;
            origin = new Move[n];
            map = new int[tx][ty];
            avail = Arrays.asList(origin);
            rand = new Random();

            for (int i = 0; i < n; i++) {
                int x = i / tx;
                int y = i % tx;
                map[x][y] = EMPTY;
                origin[i] = new Move(x, y);
            }

            reset();
        }

        void start() {
            prepareStart();
            window.updateState();
            if (state.curPlayer == ROBOT) {
                applyRound();
            }
        }

        void reset() {
            state.curPlayer = state.players[0];
            state.curSide = SIDEX;
            state.state = ATTEMPT;
            state.step = 0;
        }

        void applyMove(int x, int y) {
            if (EMPTY == map[x][y]) {
                map[x][y] = state.curSide;
                window.updateMove(x, y);
            }
        }

        private void prepareStart() {
            state.curPlayer = state.players[PLAYER1];
            state.curSide = SIDEX;
            state.step = 0;
            state.state = PROCESS;
        }

        private void applyRound() {
            while (state.state == PROCESS) {
                if (state.curPlayer == ROBOT) {
                    aiMakeMove();
                }
                checkBoard();
                if (state.state == PROCESS) {
                    nextRound();
                    window.updateRound();
                } else {
                    window.updateState();
                }
            }
        }

        private void checkLines() {
            for (int x = 0; x < map.length; x++) {
                int ch = 0;
                int cv = 0;
                for (int y = 0; y < map.length; y++) {
                    if (map[x][y] == state.curSide) {
                        ++ch;
                    }
                    if (map[y][x] == state.curSide) {
                        ++cv;
                    }
                }
                if (ch == map.length || cv == map.length) {
                    state.state = WIN;
                    break;
                }
            }
        }

        private void checkDia() {
            int m = 0;
            int n = 0;
            int offset = map.length - 1;
            for (int x = 0; x < map.length; x++) {
                if (map[x][x] == state.curSide) {
                    ++m;
                }
                if (map[offset - x][x] == state.curSide) {
                    ++n;
                }
            }
            if (map.length == n || map.length == m) {
                state.state = WIN;
            }
        }

        private void checkBoard() {
            checkLines();
            if (state.state == PROCESS) {
                checkDia();
                if (avail.size() <= 0) {
                    state.state = DRAW;
                }
            }
        }

        private void nextRound() {
            ++state.step;
            state.curPlayer = state.players[state.step % 2];
            state.curSide = state.curSide == SIDEX ? SIDEO : SIDEX;
        }

        private void aiMakeMove() {
            Move move = avail.get(rand.nextInt(avail.size()));
            applyMove(move.x, move.y);
        }

        private final List<Move> avail;
        private final Move[] origin;
        private final Random rand;
        private final int[][] map;
    }

    class WindowHandler {
        /* Initialization */
        void init() {
            initMenu();
            initPanel();
        }

        private void initMenu() {
            menu.game.hh.addActionListener(x -> setPlayerTypes(HUMAN, HUMAN));
            menu.game.hr.addActionListener(x -> setPlayerTypes(HUMAN, ROBOT));
            menu.game.rh.addActionListener(x -> setPlayerTypes(ROBOT, HUMAN));
            menu.game.rr.addActionListener(x -> setPlayerTypes(ROBOT, ROBOT));
            menu.game.exit.addActionListener(x -> System.exit(0));
        }

        private void initPanel() {
            panel.reset.addActionListener(x -> toggleStartSelect());
            panel.players[PLAYER1].addActionListener(x -> togglePlayer(PLAYER1));
            panel.players[PLAYER1].addActionListener(x -> togglePlayer(PLAYER2));

            panel.reset.setText("Start");
            updatePanelPlayer(PLAYER1);
            updatePanelPlayer(PLAYER2);
        }

        /** Setters */
        private void togglePlayer(int id) {
            state.players[id] = (state.players[id] + 1) % PLAYER_NAME.length;
            updatePanelPlayer(id);
        }

        private void toggleStartSelect() {
            if (state.state == ATTEMPT) {
                // handle start event
                game.start();
            } else {
                // handle reset event
                game.reset();
            }
        }

        void setPlayerTypes(int p1, int p2) {
            state.players[PLAYER1] = p1;
            state.players[PLAYER2] = p2;
        }

        /* Handlers */
        void menuSelectGameStart(int p1, int p2) {
            setPlayerTypes(p1, p2);
            game.start();

        }

        /* Updaters */
        void updateState() {
            switch (state.state) {
                case ATTEMPT:
                    updateAttempt();
                    break;
                case PROCESS:
                    updateProcess();
                    break;
                case WIN:
                    updateWin();
                    break;
                case DRAW:
                    updateDraw();
                    break;
                default:
                    break;
            }
        }

        private void updateAttempt() {
            panel.players[PLAYER1].setEnabled(true);
            panel.players[PLAYER1].setEnabled(true);
            panel.reset.setText("Start");
            for (JButton b : desk.list) {
                b.setText(SIDE_NAME[EMPTY]);
                b.setEnabled(false);
            }
            status.message.setText(ST_TEXT[ATTEMPT]);
        }

        private void updateProcess() {
            panel.players[PLAYER1].setEnabled(false);
            panel.players[PLAYER2].setEnabled(false);
            panel.reset.setName("Reset");
            status.message.setText(
                String.format(ST_TEXT[PROCESS],
                    PLAYER_NAME[state.curPlayer],
                    SIDE_NAME[state.curSide]
                )
            );
            for (JButton b : desk.list) {
                b.setEnabled(true);
            }
        }

        private void updateWin() {
            for (JButton b : desk.list) {
                b.setEnabled(false);
            }
            status.message.setText(
                String.format(
                    ST_TEXT[WIN],
                    PLAYER_NAME[state.curPlayer],
                    SIDE_NAME[state.curSide]
                )
            );
        }

        private void updateDraw() {
            for (JButton b : desk.list) {
                b.setEnabled(false);
            }
            status.message.setText(ST_TEXT[DRAW]);
        }

        void updateRound() {
            status.message.setText(
                String.format(
                    ST_TEXT[PROCESS],
                    state.players[state.curPlayer],
                    SIDE_NAME[state.curSide]
                )
            );
        }

        void updateMove(int x, int y) {
            int id = x * tx + y;
            desk.list[id].setText(SIDE_NAME[state.curSide]);
            desk.list[id].setEnabled(false);
        }

        private void updatePanelPlayer(int id) {
            panel.players[id].setText(PLAYER_NAME[state.players[id]]);
        }

    }
    
    public iTicTacToe() {
        state = new Summary();
        menu = new MenuBar();
        panel = new MenuPanel();
        desk = new Desk(tx, ty);
        status = new StatusBar();
        game = new GameHandle(tx, ty);
        window = new WindowHandler();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        setLayout(new BorderLayout());
        setJMenuBar(menu);
        add(panel, BorderLayout.NORTH);
        add(desk, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        setResizable(false);
        setSize(450, 510);

        game.reset();
        window.init();
    }
/** Instance data */
    private final Summary state;
    private final MenuBar menu;
    private final MenuPanel panel;
    private final Desk desk;
    private final StatusBar status;
    private final GameHandle game;
    private final WindowHandler window;

    private static final int tx = 3;
    private static final int ty = 3;

    private static final int ATTEMPT = 0;
    private static final int PROCESS = 1;
    private static final int WIN = 2;
    private static final int DRAW = 3;

    private static final int HUMAN = 0;
    private static final int ROBOT = 1;

    private static final int PLAYER1 = 0;
    private static final int PLAYER2 = 1;

    private static final int SIDEX = 0;
    private static final int SIDEO = 1;
    private static final int EMPTY = 2;

    private static final String[] ST_TEXT = {
        "Game is not started",
        "The turn of %s Player(%s)",
        "The %s Player(%s) wins",
        "Draw"
    };
    private static final String[] PLAYER_NAME = {
        "Human", "Robot"
    };
    private static final String[] SIDE_NAME = {
        "X", "O", " "
    };
}
