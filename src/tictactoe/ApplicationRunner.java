package tictactoe;


public class ApplicationRunner {
    public static void main(String[] args) {
        //new TicTacToe();
        Window w = new Window(150);
        int i = 0;
        while (w.isDisplayable()) {
            System.out.print((i++) + "\r");
        }
    }
}
