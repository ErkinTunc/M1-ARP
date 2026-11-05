package ui;

import jumpgame.Action;
import jumpgame.Director;
import jumpgame.State;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * UI-to-domain adapter. Maintains one independent game state and its move history.
 * All move generation and application remain the responsibility of jumpgame.
 */
public final class GameSession {
    private final Director director;

    public GameSession(int[][] board) {
        Objects.requireNonNull(board, "board");
        if (board.length == 0 || board[0] == null || board[0].length == 0) {
            throw new IllegalArgumentException("Board must not be empty");
        }
        int columns = board[0].length;
        for (int[] row : board) {
            if (row == null || row.length != columns) {
                throw new IllegalArgumentException("Board must be rectangular");
            }
            for (int cell : row) {
                if (cell < 0) throw new IllegalArgumentException("Colors cannot be negative");
            }
        }
        // State currently keeps the original array reference, so never pass UI-owned arrays.
        this.director = new Director(new State(copy(board)));
    }

    public int[][] snapshot() {
        State state = director.getCurrentState();
        int[][] result = new int[state.getRowCount()][state.getColumnCount()];
        for (int row = 0; row < result.length; row++) {
            for (int column = 0; column < result[row].length; column++) {
                result[row][column] = state.getCellColor(row, column);
            }
        }
        return result;
    }

    public List<Action> availableActions() {
        return List.copyOf(director.getCurrentState().getPossibleActions());
    }

    /** Execute only an action freshly generated for the current state. Index is zero-based. */
    public Action performMove(int index) {
        List<Action> validMoves = availableActions();
        if (index < 0 || index >= validMoves.size()) {
            throw new IllegalArgumentException("Move index out of range");
        }
        Action action = validMoves.get(index);
        director.performAction(action);
        return action;
    }

    public boolean undo() {
        if (director.getMemory().getActions().isEmpty()) return false;
        director.backtrack();
        return true;
    }

    public int movesPlayed() {
        return director.getMemory().getActions().size();
    }

    public boolean isSolved() {
        return director.getCurrentState().isFinal();
    }

    public int tokenCount() {
        int tokens = 0;
        for (int[] row : snapshot()) {
            for (int cell : row) if (cell != 0) tokens++;
        }
        return tokens;
    }

    private static int[][] copy(int[][] board) {
        return Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
    }
}
