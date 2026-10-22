package jumpgame;

/**
 * 
 */

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    @Test
    void storesAllActionInformationCorrectly() {
        Action action = new Action(Direction.Right, 1, 0, 0, 3, 2);

        assertEquals(Direction.Right, action.getDirection());
        assertEquals(1, action.getCellColor());
        assertArrayEquals(new int[] { 0, 0 }, action.getPos());
        assertEquals(3, action.getEliminatedColor());
        assertEquals(2, action.getJumpedCount());
    }

    @Test
    void rejectsNullDirection() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(null, 1, 0, 0, 2, 1));
    }

    @Test
    void rejectsInvalidMovingTokenColor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 0, 0, 0, 2, 1));

        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, -1, 0, 0, 2, 1));
    }

    @Test
    void rejectsInvalidEliminatedTokenColor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, 0, 0, 0, 1));

        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, 0, 0, -2, 1));
    }

    @Test
    void rejectsJumpOverSameColor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 2, 0, 0, 2, 1));
    }

    @Test
    void rejectsInvalidJumpedCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, 0, 0, 2, 0));

        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, 0, 0, 2, -1));
    }

    @Test
    void rejectsNegativeStartingCoordinates() {
        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, -1, 0, 2, 1));

        assertThrows(IllegalArgumentException.class,
                () -> new Action(Direction.Right, 1, 0, -1, 2, 1));
    }

    @Test
    void changingReturnedPositionDoesNotModifyAction() {
        Action action = new Action(Direction.Right, 1, 0, 0, 2, 1);

        int[] position = action.getPos();
        position[0] = 5;
        position[1] = 5;

        assertArrayEquals(new int[] { 0, 0 }, action.getPos());
    }
}