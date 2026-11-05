package jumpgame;
import java.util.List;
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

    public int getCellColor(int x, int y){
        return grid[x][y];
    }

    public void displayGrid() {
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }


    public List<Action> getPossibleActions(){

        List<Action> possibleActions = new java.util.ArrayList<>();

        for(int i = 0; i < grid.length; i++) {

            for(int j = 0; j < grid[i].length; j++) {

                if(grid[i][j] != 0) { // If the cell is not empty

                    for(Direction direction : Direction.values()) {
                        
                        int[] actualPos = new int[]{i, j};
                        int jumpedCount = 0;
                        int nextX = actualPos[0];
                        int nextY = actualPos[1];

                        // Check if the next position in the given direction is valid for a jump(i.e., there is a non-zero cell to jump over and an empty cell to land on and the jump is within the grid boundaries)
                        if(direction == Direction.Up && nextX - 2 >= 0 && grid[nextX - 1][nextY] != 0 && grid[nextX - 2][nextY] == 0) {
                            nextX -= 2;

                        } else if(direction == Direction.Down && nextX + 2 < grid.length && grid[nextX + 1][nextY] != 0 && grid[nextX + 2][nextY] == 0) {
                            nextX += 2;
                        } else if(direction == Direction.Left && nextY - 2 >= 0 && grid[nextX][nextY - 1] != 0 && grid[nextX][nextY - 2] == 0   ) {
                            nextY -= 2;
                        } else if(direction == Direction.Right && nextY + 2 < grid[0].length && grid[nextX][nextY + 1] != 0 && grid[nextX][nextY + 2] == 0) {
                            nextY += 2;
                        }

                        if(nextX != actualPos[0] || nextY != actualPos[1]) {
                            jumpedCount++;

                            int eliminatedColor = grid[(actualPos[0] + nextX) / 2][(actualPos[1] + nextY) / 2];

                            possibleActions.add(new Action(direction, grid[i][j], i, j, eliminatedColor, jumpedCount));

                        }

                      
                    }
                }
            }
        }

        return possibleActions;
        

    }
    public int getRowCount() {
        return grid.length;
    }

    public int getColumnCount() {
        return grid[0].length;
    }
}