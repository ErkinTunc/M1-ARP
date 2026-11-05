package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Generates random boards without guaranteeing they can be solved. */
public final class RandomBoardFactory {
    private final Random random;

    public RandomBoardFactory(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public int[][] generate(int rows, int columns, int colorCount) {
        if (rows < 2 || rows > 10 || columns < 2 || columns > 10) {
            throw new IllegalArgumentException("Board dimensions must be between 2 and 10.");
        }

        int cellCount = rows * columns;
        if (colorCount < 2 || colorCount > 6 || colorCount >= cellCount) {
            throw new IllegalArgumentException("Colors must be between 2 and min(6, cells - 1).");
        }

        // Leave empty cells and include each selected color at least once.
        int emptyCount = Math.min(Math.max(1, cellCount / 3), cellCount - colorCount);
        int tokenCount = cellCount - emptyCount;
        List<Integer> cells = new ArrayList<>(cellCount);

        for (int color = 1; color <= colorCount; color++) {
            cells.add(color);
        }
        while (cells.size() < tokenCount) {
            cells.add(1 + random.nextInt(colorCount));
        }
        while (cells.size() < cellCount) {
            cells.add(0);
        }

        Collections.shuffle(cells, random);

        int[][] board = new int[rows][columns];
        for (int index = 0; index < cellCount; index++) {
            board[index / columns][index % columns] = cells.get(index);
        }
        return board;
    }
}
