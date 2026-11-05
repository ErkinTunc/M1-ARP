package jumpgame;

import java.util.List;

/**
 * This class represents a state of the grid in the game.
 * It contains a 2D array representing the grid and provides a method to check
 * if the state is final,
 * meaning that there is only one non-zero element in the grid.
 * Each cell in the grid can be either 0 (empty) or a positive integer
 * (representing a color).
 */
public class State {

    // ---------------- Attributes ----------------
    private final int[][] grid;

    // -------------- Constructor ----------------
    public State(int[][] grid) {
        this.grid = grid;
    }

    // --------------- Methods ------------------

    /**
     * Checks if the state is final, meaning that there is only one non-zero element
     * in the grid.
     * 
     * @return true if the state is final, false otherwise
     */
    public boolean isFinal() {

        int count = 0;

        for (int[] grid1 : grid) {
            for (int j = 0; j < grid1.length; j++) {
                if (grid1[j] != 0) {
                    count++;
                }
            }
        }
        return count == 1;
    }

    /**
     * Colors a specific cell in the grid with the given color.
     * 
     * @param color the color to set the cell to
     * @param posX  the x-coordinate of the cell
     * @param posY  the y-coordinate of the cell
     */
    public void colorCase(int color, int posX, int posY) {
        grid[posX][posY] = color;
    }

    /**
     * Returns the color of the cell at the specified coordinates.
     * 
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @return the color of the cell at (x, y)
     */
    public int getCellColor(int x, int y) {
        return grid[x][y];
    }

    /**
     * Displays the current state of the grid to the console.
     * Each row of the grid is printed on a new line, with cell values separated by
     * spaces.
     */
    public void displayGrid() {
        for (int[] grid1 : grid) {
            for (int j = 0; j < grid1.length; j++) {
                System.out.print(grid1[j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Returns a list of all possible actions that can be performed from the current
     * state.
     * An action is possible if a cell can jump over one or more adjacent cells of a
     * different color and land on an empty cell.
     * The method iterates through each cell in the grid, checking in all four
     * directions (up, down, left, right) for possible jumps.
     * 
     * @return a list of possible actions from the current state
     */
    public List<Action> getPossibleActions() {

        List<Action> possibleActions = new java.util.ArrayList<>();

        int rows = grid.length;
        int cols = grid[0].length;

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int color = grid[x][y];

                if (color == 0) {
                    continue; // Skip empty cells
                }
                for (Direction direction : Direction.values()) {
                    int stepX = 0;
                    int stepY = 0;
                    switch (direction) {
                        case Up -> stepX = -1;
                        case Down -> stepX = 1;
                        case Left -> stepY = -1;
                        case Right -> stepY = 1;
                    }

                    int currentX = x + stepX;
                    int currentY = y + stepY;
                    int jumpedCount = 0;
                    int eliminatedColor = -1; // Initialize to an invalid color

                    while (currentX >= 0 && currentX < rows && currentY >= 0 && currentY < cols) {
                        int currentColor = grid[currentX][currentY];

                        if (currentColor == 0) {
                            if (jumpedCount > 0) {
                                possibleActions.add(new Action(direction, color, x, y, eliminatedColor, jumpedCount));
                            }
                            break;
                        }

                        if (color == currentColor) {
                            break; // Stop if we encounter a cell of the same color
                        }

                        if (jumpedCount == 0) {
                            eliminatedColor = currentColor; // Set the eliminated color on the first jump
                        } else if (eliminatedColor != currentColor) {
                            break; // Stop if we encounter a different color after the first jump
                        }

                        jumpedCount++;
                        currentX += stepX;
                        currentY += stepY;

                    }
                }
            }
        }
        return possibleActions;

    }

    // ---------------- Getters ----------------

    /**
     * Returns the number of rows in the grid.
     * 
     * @return the number of rows
     */
    public int getRowCount() {
        return grid.length;
    }

    /**
     * Returns the number of columns in the grid.
     * 
     * @return the number of columns
     */
    public int getColumnCount() {
        return grid[0].length;
    }
}