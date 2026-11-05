
package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @Test
    void undoRestoresBoardForAllMoves() {
        for (DemoBoards.Preset preset : DemoBoards.Preset.values()) {
            int[][] board = preset.board();
            GameSession otherSession = new GameSession(board);

            int moveCount = otherSession.availableActions().size();

            for (int i = 0; i < moveCount; i++) {
                GameSession session = new GameSession(board);

                session.performMove(i);
                assertEquals(1, session.movesPlayed());
                assertArrayEquals(board, preset.board());
                assertArrayEquals(board, otherSession.snapshot());

                assertTrue(session.undo());
                assertArrayEquals(board, session.snapshot());
                assertEquals(0, session.movesPlayed());
            }
        }
    }

    @Test
    void starterCanBePlayedAndUndone() {
        GameSession session = new GameSession(
                DemoBoards.Preset.STARTER.board());

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
        int[][] board = { { 1, 2, 2, 0 } };
        GameSession session = new GameSession(board);

        assertEquals(1, session.availableActions().size());
        assertEquals(2, session.availableActions().get(0).getJumpedCount());

        session.performMove(0);
        assertArrayEquals(new int[][] { { 0, 0, 0, 1 } }, session.snapshot());

        assertTrue(session.undo());
        assertArrayEquals(board, session.snapshot());
    }

    @Test
    void rejectsInvalidMoves() {
        GameSession blocked = new GameSession(
                DemoBoards.Preset.BLOCKED.board());

        assertTrue(blocked.availableActions().isEmpty());
        assertFalse(blocked.undo());
        assertThrows(IllegalArgumentException.class,
                () -> blocked.performMove(0));

        GameSession starter = new GameSession(
                DemoBoards.Preset.STARTER.board());

        assertThrows(IllegalArgumentException.class,
                () -> starter.performMove(5));

        assertEquals(0, starter.movesPlayed());
    }
}
