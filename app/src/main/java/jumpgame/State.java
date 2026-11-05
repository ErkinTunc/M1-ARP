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

        int rows = grid.length;
        int cols = grid[0].length;

        for(int x = 0; x < rows; x++) {
            for(int y = 0; y < cols; y++) {
                int color = grid[x][y];

                if(color==0) {
                    continue; // Skip empty cells
                }
                for(Direction direction : Direction.values()) {
                    int stepX = 0;
                    int stepY = 0;
                    switch(direction) {
                        case Up:
                            stepX = -1;
                            break;
                        case Down:
                            stepX = 1; 
                            break;
                        case Left:
                            stepY = -1;
                            break;
                        case Right:
                            stepY = 1; 
                            break;
                    }
                    int currentX = x+stepX;
                    int currentY = y+stepY;
                    int jumpedCount = 0;
                    int eliminatedColor = -1; // Initialize to an invalid color

                    while(currentX>= 0 && currentX<rows && currentY>= 0 && currentY<cols) {
                        int currentColor = grid[currentX][currentY];

                        if(currentColor == 0){
                            if(jumpedCount > 0){
                                possibleActions.add(new Action(direction,color,x,y,eliminatedColor,jumpedCount));
                            }
                            break;
                        }

                        if(color == currentColor){
                            break; // Stop if we encounter a cell of the same color
                        }

                        if(jumpedCount == 0){
                            eliminatedColor = currentColor; // Set the eliminated color on the first jump
                        } else if(eliminatedColor != currentColor){
                            break; // Stop if we encounter a different color after the first jump
                        }

                        jumpedCount++;
                        currentX += stepX;
                        currentY += stepY;

                    }
                }
            }
        }
        return possibleActions ;

        

    }

    
    public int getRowCount() {
        return grid.length;
    }

    public int getColumnCount() {
        return grid[0].length;
    }
}