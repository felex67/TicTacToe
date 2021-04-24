package tictactoe;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;



class Game implements Runnable {

    enum Side {

        X("X"), O("O"), E(" ");

        Side(String title) {
            this.title = title;
        }

        String title;
    }

    /** Game round */
    class Round {
        Round(Cell[][] map) {
            this.map = map;
            freeCells = new ArrayList<>();
            reset();
        }
        void makeMove(Cell cell) {
            freeCells.remove(cell);
            moves[turn][0] = cell.x;
            moves[turn][1] = cell.y;
            ++turn;
        }
        void save(Connection conn) {

        }
        void reset() {
            freeCells.clear();
            int move = 0;
            for (int x = 0; x < map.length; x++) {
                for (int y = 0; y < map[0].length; y++) {
                    freeCells.add(map[x][y]);
                    moves[move][0] = moves[move][1] = -1;
                }
            }
            turn = 0;
        }

        final int[][] moves = {
            {-1, -1}, {-1, -1}, {-1, -1},
            {-1, -1}, {-1, -1}, {-1, -1},
            {-1, -1}, {-1, -1}, {-1, -1}
        };

        final ArrayList<Cell> freeCells;
        final Cell[][] map;
        int id = 0;
        int win = -1;
        int turn = 0;
    }

    enum State {
        ATTEMPT("The game is not started"),
        PROCESS("Game not finished"),
        WIN("%s wins"),
        DRAW("Draw"),
        EXIT("Exit");

        State(String msg) {
            this.msg = msg;
        }

        final String msg;
    }

    enum Player {
        AI("Computer"), HUMAN("Player");
        Player(String name) {
            this.name = name;
        }

        final String name;
    }

    class Ai {

        Cell makeMove(ArrayList<Cell> buttons) {
            Cell cell = null;
            if (conn != null) {
                /*  */
            } else {
                cell = buttons.get(rand.nextInt(buttons.size()));
            }
            return cell;
        }

        final Random rand = new Random();
        final Connection conn = null;
    }

    Game(Window window) {
        this.window = window;
        map = window.desk.map;
        players[0] = players[1] = Player.HUMAN;
        ai = new Ai();
        round = new Round(map);
        conn = null;

        side = Side.X;
        state = State.ATTEMPT;
        window.desk.setEnabled(false);
    }

    void playerMove(Cell cell) {
        if (state == State.PROCESS) {
            round.makeMove(cell);
            cell.update(side, true);
        }
    }

    void togglePlayerAi(JButton button, Side side) {
        int p = side.ordinal();
        if (players[p] == Player.HUMAN) {
            players[p] = Player.AI;
            button.setText("Computer");
        } else {
            players[p] = Player.HUMAN;
            button.setText("Player");
        }
    }

    void attempt() {
        round.reset();
        window.desk.setEnabled(false);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (state != State.EXIT) {

        }
    }

    final Player[] players = {Player.HUMAN, Player.HUMAN};
    final Ai ai;
    final Cell map[][];
    final Window window;
    final Connection conn;
    final Round round;

    Side side;
    State state;
}
