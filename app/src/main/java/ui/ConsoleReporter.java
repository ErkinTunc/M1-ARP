package ui;

import java.io.PrintStream;
import java.util.Objects;

/** Handles console text, colors, and board display. */
public final class ConsoleReporter {
    private static final String ORANGE = "\u001B[38;2;255;102;0m";
    private static final String WHITE = "\u001B[1;37m";
    private static final String GREY = "\u001B[90m";
    private static final String RED = "\u001B[1;31m";
    private static final String GREEN = "\u001B[1;32m";
    private static final String RESET = "\u001B[0m";
    private static final String[] CELL_COLORS = {
            "\u001B[41;97m", "\u001B[42;30m", "\u001B[43;30m",
            "\u001B[44;97m", "\u001B[45;97m", "\u001B[46;30m"
    };
    private static final int MAX_CONTENT_WIDTH = 76;

    private final PrintStream out;
    private final boolean ansiSupported;
    private final int width;
    private final String indent;
    private boolean colorsEnabled;

    public ConsoleReporter(PrintStream out, boolean ansiSupported, int terminalColumns) {
        this.out = Objects.requireNonNull(out, "out");
        this.ansiSupported = ansiSupported;
        this.colorsEnabled = ansiSupported;

        int columns = Math.max(30, terminalColumns);
        this.width = Math.min(MAX_CONTENT_WIDTH, columns - 4);
        this.indent = " ".repeat((columns - width) / 2);
    }

    public static ConsoleReporter forCurrentTerminal() {
        return new ConsoleReporter(System.out, supportsAnsi(), terminalColumns());
    }

    public void clearScreen() {
        out.print(ansiSupported ? "\u001B[2J\u001B[H" : "\n".repeat(32));
        out.flush();
    }

    public void banner() {
        line("=".repeat(width), ORANGE);
        center("JUMP GAME | PROBLEM SOLVING", ORANGE);
        line("=".repeat(width), ORANGE);
    }

    public void status(String boardSize, String colors, String mode) {
        out.println();
        line("ACTIVE RULE  : Jump over another color into an empty cell", ORANGE);
        line("               Same-color jumped tokens are removed together", null);
        line("BOARD " + boardSize + "   |   COLORS " + colors + "   |   MODE " + mode, ORANGE);
    }

    public void heading(String heading) {
        out.println();
        center(heading, ORANGE);
        out.println();
    }

    public void option(String index, String description) {
        line("[" + index + "]  " + description, WHITE);
    }

    public void info(String message) {
        for (String row : message.split("\\R", -1)) {
            line(row, null);
        }
    }

    public void success(String message) {
        line("[OK] " + message, GREEN);
    }

    public void error(String message) {
        line("[ERROR] " + message, RED);
    }

    public void prompt(String label) {
        out.print(indent + style("> ", ORANGE) + label);
        out.flush();
    }

    public void board(int[][] board) {
        out.println();
        for (int[] row : board) {
            out.print(indent);
            for (int value : row) {
                String cell = value == 0 ? "  .  " : String.format(" %2d  ", value);
                String color = value == 0 ? GREY : CELL_COLORS[(value - 1) % CELL_COLORS.length];
                out.print(style(cell, color) + " ");
            }
            out.println();
        }
        out.println();
    }

    public boolean colorsEnabled() {
        return colorsEnabled;
    }

    public void setColorsEnabled(boolean enabled) {
        colorsEnabled = enabled && ansiSupported;
    }

    private void center(String text, String color) {
        int spaces = Math.max(0, (width - text.length()) / 2);
        line(" ".repeat(spaces) + text, color);
    }

    private void line(String text, String color) {
        out.println(indent + style(text, color));
    }

    private String style(String text, String color) {
        if (!colorsEnabled || color == null) {
            return text;
        }
        return color + text + RESET;
    }

    private static int terminalColumns() {
        try {
            String value = System.getenv("COLUMNS");
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException ignored) {
            // Use the default width if COLUMNS is invalid.
        }
        return 100;
    }

    private static boolean supportsAnsi() {
        String mode = System.getenv("JUMP_COLOR");
        if ("always".equalsIgnoreCase(mode)) {
            return true;
        }
        if ("never".equalsIgnoreCase(mode) || System.getenv().containsKey("NO_COLOR")) {
            return false;
        }

        String term = System.getenv("TERM");
        return System.console() != null
                || System.getenv().containsKey("WT_SESSION")
                || System.getenv().containsKey("ANSICON")
                || (term != null && !term.isBlank() && !"dumb".equalsIgnoreCase(term));
    }
}
