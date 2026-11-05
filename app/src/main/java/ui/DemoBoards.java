package ui;

import java.util.Arrays;

/** Carefully chosen fixtures; no UI or solving logic belongs here. */
public final class DemoBoards {
    private DemoBoards() { }

    public enum Preset {
        STARTER("STARTER", "3x3 - one-move solution", new int[][] {
                {1, 2, 0},
                {0, 0, 0},
                {0, 0, 0}
        }),
        STANDARD("STANDARD", "4x4 - original demonstration", new int[][] {
                {1, 2, 0, 0},
                {2, 3, 3, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        }),
        CHALLENGE("CHALLENGE", "5x5 - four colors", new int[][] {
                {1, 2, 0, 3, 0},
                {2, 3, 3, 0, 4},
                {0, 4, 2, 0, 1},
                {3, 0, 1, 4, 0},
                {0, 2, 0, 0, 0}
        }),
        BLOCKED("BLOCKED", "2x2 - no legal jump", new int[][] {
                {1, 1},
                {0, 0}
        });

        private final String label;
        private final String description;
        private final int[][] template;

        Preset(String label, String description, int[][] template) {
            this.label = label;
            this.description = description;
            this.template = deepCopy(template);
        }

        public String label() {
            return label;
        }

        public String description() {
            return description;
        }

        public int[][] board() {
            return deepCopy(template);
        }

        public int colorCount() {
            return (int) Arrays.stream(template).flatMapToInt(Arrays::stream)
                    .filter(value -> value != 0).distinct().count();
        }
    }

    public static int[][] example() {
        return Preset.STANDARD.board();
    }

    private static int[][] deepCopy(int[][] original) {
        return Arrays.stream(original).map(int[]::clone).toArray(int[][]::new);
    }
}
