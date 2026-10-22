public class Action {
    private final Direction direction;
    private final int cellNumber;

    public Action(Direction direction, int cellNumber) {
        this.direction = direction;
        this.cellNumber = cellNumber;
    }



    public Direction getDirection() {
        return direction;
    }

    public int getCellNumber() {
        return cellNumber;
    }
}
