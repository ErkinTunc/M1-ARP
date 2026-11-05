package ui;

import jumpgame.Action;
import jumpgame.Director;
import jumpgame.State;

import java.util.List;
import java.util.Objects;

/** Connects the console UI to the game logic. */
public final class GameSession {
    private final Director director;

    public GameSession(int[][] board) {
        validateBoard(board);
        // Copy the input so a move cannot modify the caller's board.
        this.director = new Director(new State(copyBoard(board)));
    }

    public int[][] snapshot() {
        State state = director.getCurrentState();
        int[][] board = new int[state.getRowCount()][state.getColumnCount()];

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                board[row][column] = state.getCellColor(row, column);
            }
        }
        return board;
    }

    public List<Action> availableActions() {
        return List.copyOf(director.getCurrentState().getPossibleActions());
    }

    public Action performMove(int index) {
        List<Action> actions = availableActions();
        if (index < 0 || index >= actions.size()) {
            throw new IllegalArgumentException("Move index out of range");
        }

        Action action = actions.get(index);
        director.performAction(action);
        return action;
    }

    public boolean undo() {
        if (director.getMemory().getActions().isEmpty()) {
            return false;
        }
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
        int count = 0;
        for (int[] row : snapshot()) {
            for (int cell : row) {
                if (cell != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void validateBoard(int[][] board) {
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
                if (cell < 0) {
                    throw new IllegalArgumentException("Colors cannot be negative");
                }
            }
        }
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][];
        for (int row = 0; row < board.length; row++) {
            copy[row] = board[row].clone();
        }
        return copy;
    }
}
