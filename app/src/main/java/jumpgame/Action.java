package jumpgame;

/**
 * Action
 * Represents an action performed on the grid, including the direction of movement, the cell number, the initial position of the cell before the action, and the color.
 */
public class Action {
    private final Direction direction;
    private final int cellColor;
    private final int initialPosX;
    private final int initialPosY;
    private final int color;

    public Action(Direction direction, int cellColor, int initialPosX, int initialPosY, int color) throws IllegalArgumentException {
        if(direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        this.direction = direction;
        this.cellColor = cellColor;
        this.initialPosX = initialPosX;
        this.initialPosY = initialPosY;
        this.color = color;
    }   



    public boolean isValid() {
        return direction != null;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getCellColor() {
        return cellColor;
    }

    public int[] getPos() {
        return new int[]{initialPosX, initialPosY};
    }
}
