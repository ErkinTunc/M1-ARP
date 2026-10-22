package jumpgame;

/**
 * This class represents a state of the grid in the game. 
 * It contains a 2D array representing the grid and provides a method to check if the state is final,
 * meaning that there is only one non-zero element in the grid.
 * Each cell in the grid can be either 0 (empty) or a positive integer (representing a color).
 */
public class State {

    private int [][] grid;


    public State(int [][] grid) {
        this.grid = grid;
    }


    /**
     * Checks if the state is final, meaning that there is only one non-zero element in the grid.
     * @return true if the state is final, false otherwise
     */
    public boolean isFinal() {

        int count = 0;

        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                if(grid[i][j] != 0){
                    count++;
                }
            }
        }
        return count == 1;
    }

    public void colorCase(int color, int posX, int posY) {
        grid[posX][posY] = color;
    }
}