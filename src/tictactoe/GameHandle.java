package tictactoe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JButton;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("unused")
public class GameHandle implements Runnable {
/********************************************************* Classes *******************************************************************/
    private enum State {
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
            this.name = name;
        }

        final String name;
    }

/** Inherits information about the current round */
    private class Round {

        Round() {
            map = window.desk.map;
            origin = window.desk.list;
            reset();
        }
    
        void reset() {
            freeCells.clear();
            for (Cell c : origin) {
                freeCells.add(c);
                c.update(Owner.E, false);
            }
            state = State.ATTEMPT;
            window.status.update(state.message);
            owner = Owner.X;
            step = 0;
        }

        void playRound() {
            while (State.PROCESS == state) {
                Cell c = sides[step % 2].makeMove();
                if (c != null) {
                    c.update(owner, false);
                    freeCells.remove(c);
                    checkBoard();
                    if (state == State.PROCESS && freeCells.size() <= 0) {
                        state = State.DRAW;
                    }
                }
                if (State.PROCESS == state) {
                    ++step;
                    owner = step % 2 == 1 ? Owner.O : Owner.X;
                } else {
                    window.desk.setEnabled(false);
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
            }
        }
    
        State state = State.ATTEMPT;
        int step = 0;
        Owner owner = Owner.X;
    
        final ArrayList<Cell> freeCells = new ArrayList<>();
        final Cell[][] map;
        final Cell[] origin;
        final Player[] sides = {human, human};
    }

/** Abstract Class Player */
    private abstract class Player {

        final String name;
    
        Player(String name) {
            this.name = name;
        }
        
        abstract Cell makeMove();
    }

/** Class Human: handle desk events */
    private class Human extends Player {

        Human(GameHandle handle) {
            super("Human");
        }
        @Override
        Cell makeMove() {
            // TODO Auto-generated method stub
            Cell cell = null;
            while (round.state != State.EXIT) {
                synchronized(deskQueue) {
                    try {
                        deskQueue.wait();
                        cell = deskQueue.poll();
                        if (null != cell && round.freeCells.contains(cell)) {
                            break;
                        } else if (null != cell) {
                            deskQueue.clear();
                        }
                    } catch (Exception ignore) {
    
                    }
                }
            }
            return cell;
        }
    }

/** Class computer makes moves as an ai */
    private class Computer extends Player {
        final Random rand;
    
        Computer(GameHandle handle) {
            super("Computer");
            rand = new Random();
        }
    
        @Override
        Cell makeMove() {
            // TODO Auto-generated method stub
            return round.freeCells.get(rand.nextInt(round.freeCells.size()));
        }
    }

/** Main window handle */
    private class Adapter extends WindowAdapter {

        Adapter() {
            for (Cell c : window.desk.list) {
                c.handle = deskOnClick;
                c.setText(" ");
            }
            window.panel.player1.handle = menuClickPlayer1;
            window.panel.player2.handle = menuClickPlayer2;
            window.panel.reset.handle = menuClickStart;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            synchronized(round) {
                round.state = State.EXIT;
                try {
                    round.notifyAll();
                    Thread.sleep(50);
                } catch (Exception ignore) {

                }
            }
            super.windowClosing(e);
        }

        void clickOnDesk(Cell cell) {
            synchronized(deskQueue) {
                deskQueue.add(cell);
                deskQueue.notifyAll();
            }
        }

        void clickOnPlayer1(JButton b) {
            togglePlayerAi(b, 0);
        }

        void clickOnPlayer2(JButton b) {
            togglePlayerAi(b, 1);
        }

        void togglePlayerAi(JButton b, int id) {
            synchronized(round) {
                if (round.state == State.ATTEMPT) {
                    round.sides[id] = round.sides[id] == computer ? human : computer;
                    b.setText(round.sides[id].name);
                }
            }
        }
        void clickOnStartReset(JButton b) {
            synchronized(round) {
                if (round.state == State.ATTEMPT) {
                    gameStart();
                } else if (round.state != State.EXIT) {
                    gameReset();
                }
                round.notifyAll();
            }
        }
        void gameStart() {
            window.panel.setEnabled(false);
            window.desk.setEnabled(true);
            window.panel.reset.setEnabled(true);
            window.panel.reset.setText("Reset");
            round.state = State.PROCESS;
            window.status.update(round.state.message);
        }
        void gameReset() {
            window.desk.update(Owner.E, false);
            window.panel.reset.setText("Start");
            window.panel.setEnabled(true);
            round.reset();
        }

        /** Button handlers */
        final Consumer<Cell> deskOnClick = c -> clickOnDesk(c);
        final Consumer<MenuPanel.Button> menuClickPlayer1 = b -> clickOnPlayer1(b);
        final Consumer<MenuPanel.Button> menuClickPlayer2 = b -> clickOnPlayer2(b);
        final Consumer<MenuPanel.Button> menuClickStart = b -> clickOnStartReset(b);
    }

/** Game handle */
    GameHandle(TicTacToe window) {
        this.window = window;
        //game = null; //new Game(window.desk.map);
        deskQueue = new LinkedList<>();
        human = new Human(this);
        computer = new Computer(this);
        round = new Round();
        window.addWindowListener(new Adapter());
        window.desk.setEnabled(true);
    }

    public void run() {
        boolean doWork = true;
        while (doWork) {
            synchronized(round) {
                try {
                    round.wait();
                    if (round.state == State.EXIT) {
                        prepareExit();
                    } else if (round.state == State.PROCESS) {
                        round.playRound();
                        if (round.state == State.WIN) {
                            window.status.update(round.owner.name + round.state.message);
                        } else {
                            window.status.update(round.state.message);
                        }
                    }
                } catch (Exception e) {
                    //System.err.println("Exception: '" + e.toString() + "'");
                }
            }
        }
    }

    void prepareExit () {
        try {
            //System.out.println("Exiting");
            Thread.sleep(computer.rand.nextInt(1_000));
        } catch (Exception ignore) {

        }
    }
    /** main window */
    private final TicTacToe window;
    /** round information */
    private final Round round;
    /** instance of human desk events handle */
    private final Human human;
    /** AI */
    private final Computer computer;
    /** desk events queue */
    private final Queue<Cell> deskQueue;

    private final static int INVALID_EVENT = -1;
    private final static int MENU_PLAYER1 = 1;
    private final static int MENU_PLAYER2 = 2;
    private final static int MENU_STARTRESET = 3;
    private final static int WINDOW_EXIT = 4;
}
