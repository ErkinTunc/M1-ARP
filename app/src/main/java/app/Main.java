package app;
import jumpgame.Director;
import jumpgame.State;
import ui.InteractiveJumpGameApp;

/** Application entry point. No presentation or game rules here. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {

        int[][] grid ={
            {0, 1, 0, 0},
            {4, 0, 3, 0},
            {2, 0, 0, 0},
            {0, 0, 0, 0}
        };
        State gridTest = new State(grid);
        Director director = new Director(gridTest);
        director.run();
        InteractiveJumpGameApp.runInConsole();
    }
}
