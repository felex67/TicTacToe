package tictactoe;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.Random;

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
        this.sname = name;
    }

    final String sname;
}

public class TicTacToe {
    enum PlayerType {
        HUMAN("Humnan"), COMPUTER("Computer");
        PlayerType(String text) {
            this.text = text;
        }
        final String text;
    }

    private class Round {

        Round() {
            rand = new Random();
            map = hWind.getMap();
            origin = hWind.getOrigin();
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
        private final Window window;

        WinHandle() {
            this.window = new Window();
            this.window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    state = State.EXIT;
                    super.windowClosing(e);
                }
            });
            for (Cell c : window.desk.list) {
                c.handle = x -> {
                    round.apply(x);
                    round.playRound();
                };
            }
            window.panel.player1.handle = x -> {
                togglePlayerAi(0);
                updatePlayer1();
            };
            window.panel.player2.handle = x -> {
                togglePlayerAi(1);
                updatePlayer2();
            };
            window.panel.reset.handle = x -> playerClickStartReset();
        }
        void update() {
            if (State.PROCESS == state) {
                window.panel.player1.setEnabled(false);
                window.panel.player2.setEnabled(false);
                window.panel.reset.setText("Reset");
                window.desk.setEnabled(true);
            } else if (State.DRAW == state) {
                window.desk.setEnabled(false);
                window.status.update(state.message);
            } else if (State.WIN == state) {
                window.desk.setEnabled(false);
                
            } else if (State.ATTEMPT == state) {
                window.panel.setEnabled(true);
                window.desk.update(Owner.E, false);
                window.panel.reset.setText("Start");
            }
            window.status.update(state == State.WIN ? owner.sname + state.message : state.message);
        }
        void updatePlayer1() {
            window.panel.player1.setText(pType[0].text);
        }
        void updatePlayer2() {
            window.panel.player2.setText(pType[1].text);
        }
        Cell[][] getMap() {
            return window.desk.map;
        }
        Cell[] getOrigin() {
            return window.desk.list;
        }
    }

    TicTacToe() {
        hWind = new WinHandle();
        round = new Round();
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

    State state = State.ATTEMPT;
    Owner owner = Owner.X;

    final PlayerType[] pType = {PlayerType.HUMAN, PlayerType.HUMAN};
    final Round round;
    final WinHandle hWind;
}
