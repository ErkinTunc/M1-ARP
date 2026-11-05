package ui;

import jumpgame.Action;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {
    @Test
    void movesDoNotChangeOriginalBoardAndUndoRestoresIt() {
        for (DemoBoards.Preset preset : DemoBoards.Preset.values()) {
            int[][] original = preset.board();
            GameSession initial = new GameSession(original);
            List<Action> moves = initial.availableActions();
            for (int index = 0; index < moves.size(); index++) {
                GameSession session = new GameSession(original);
                session.performMove(index);
                assertEquals(1, session.movesPlayed());
                assertTrue(Arrays.deepEquals(original, preset.board()), "preset must not be modified");
                assertTrue(Arrays.deepEquals(original, initial.snapshot()), "other sessions must not change");
                assertTrue(session.undo(), "undo must succeed");
                assertTrue(Arrays.deepEquals(original, session.snapshot()), "undo must restore the board");
                assertEquals(0, session.movesPlayed());
            }
        }
    }

    @Test
    void starterCanBePlayedAndUndone() {
        GameSession session = new GameSession(DemoBoards.Preset.STARTER.board());
        assertEquals(1, session.availableActions().size());
        session.performMove(0);
        assertTrue(session.isSolved());
        assertEquals(1, session.tokenCount());
        assertTrue(session.undo());
        assertFalse(session.isSolved());
        assertEquals(2, session.tokenCount());
    }

    @Test
    void multiTokenJumpCanBeUndone() {
        int[][] board = {{1, 2, 2, 0}};
        GameSession session = new GameSession(board);
        assertEquals(1, session.availableActions().size());
        assertEquals(2, session.availableActions().get(0).getJumpedCount());
        session.performMove(0);
        assertTrue(Arrays.deepEquals(new int[][]{{0, 0, 0, 1}}, session.snapshot()));
        assertTrue(session.undo());
        assertTrue(Arrays.deepEquals(board, session.snapshot()));
    }

    @Test
    void cannotMoveIfBlockedOrChooseInvalidIndex() {
        GameSession blocked = new GameSession(DemoBoards.Preset.BLOCKED.board());
        assertTrue(blocked.availableActions().isEmpty());
        assertFalse(blocked.undo());
        assertThrows(IllegalArgumentException.class, () -> blocked.performMove(0));
        GameSession starter = new GameSession(DemoBoards.Preset.STARTER.board());
        assertThrows(IllegalArgumentException.class, () -> starter.performMove(5));
        assertEquals(0, starter.movesPlayed());
    }
}
