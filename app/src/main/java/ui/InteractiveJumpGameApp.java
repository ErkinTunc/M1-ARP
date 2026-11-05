package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

/** Handles only user interaction and navigation; domain/solver remains separate. */
public final class InteractiveJumpGameApp {
    private final BufferedReader input;
    private final ConsoleReporter output;
    private final RandomBoardFactory boards;

    private DemoBoards.Preset currentPreset = DemoBoards.Preset.STANDARD;
    private int[][] board = currentPreset.board();
    private String mode = "PRESET";
    private int activeColors = currentPreset.colorCount();

    public InteractiveJumpGameApp(BufferedReader input, ConsoleReporter output) {
        this(input, output, new RandomBoardFactory(new Random()));
    }

    public InteractiveJumpGameApp(BufferedReader input, ConsoleReporter output, RandomBoardFactory boards) {
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
        this.boards = Objects.requireNonNull(boards, "boards");
    }

    public static void runInConsole() {
        new InteractiveJumpGameApp(
                new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                ConsoleReporter.forCurrentTerminal()).run();
    }

    public void run() {
        try {
            while (true) {
                showScreen("MAIN MENU");
                output.option("1", "Choose preset board");
                output.option("2", "Generate random board");
                output.option("3", "View current board");
                output.option("4", "Rules");
                output.option("5", "Options");
                output.option("0", "Exit");
                String choice = read("Select option: ");
                if (choice == null || choice.equals("0")) return;
                switch (choice) {
                    case "1" -> choosePreset();
                    case "2" -> createRandomBoard();
                    case "3" -> showBoard();
                    case "4" -> showRules();
                    case "5" -> showOptions();
                    default -> showError("Select an option between 0 and 5.");
                }
            }
        } catch (IOException e) {
            output.error("Input error: " + e.getMessage());
        }
    }

    private void choosePreset() throws IOException {
        DemoBoards.Preset[] presets = DemoBoards.Preset.values();
        while (true) {
            showScreen("CHOOSE PRESET BOARD");
            for (int i = 0; i < presets.length; i++) {
                DemoBoards.Preset preset = presets[i];
                output.option(Integer.toString(i + 1), preset.label() + "  |  " + preset.description());
            }
            output.option("0", "Back to main menu");
            String choice = read("Choose a board: ");
            if (choice == null || choice.equals("0")) return;
            try {
                int index = Integer.parseInt(choice) - 1;
                if (index < 0 || index >= presets.length) throw new NumberFormatException();
                currentPreset = presets[index];
                board = currentPreset.board();
                activeColors = currentPreset.colorCount();
                mode = "PRESET";
                showBoard();
                return;
            } catch (NumberFormatException e) {
                output.error("Choose a listed board number.");
                pause();
            }
        }
    }

    private void createRandomBoard() throws IOException {
        showSetup("? x ?", "?", "RANDOM SETUP");
        output.heading("RANDOM BOARD CONFIGURATION");
        output.info("No cell-by-cell input. Only board dimensions and colors.");
        int rows = readNumber("Rows (2-10): ", 2, 10);

        showSetup(rows + " x ?", "?", "RANDOM SETUP");
        output.heading("RANDOM BOARD CONFIGURATION");
        int columns = readNumber("Columns (2-10): ", 2, 10);
        int maxColors = Math.min(6, rows * columns - 1);

        showSetup(rows + " x " + columns, "?", "RANDOM SETUP");
        output.heading("RANDOM BOARD CONFIGURATION");
        output.info("Available colors: 2-" + maxColors);
        int colorCount = readNumber("Number of colors (2-" + maxColors + "): ", 2, maxColors);

        // Publish new board only after all configuration values are valid.
        board = boards.generate(rows, columns, colorCount);
        activeColors = colorCount;
        mode = "RANDOM";
        showBoard();
    }

    private void showBoard() throws IOException {
        while (true) {
            showScreen("CURRENT BOARD");
            output.board(board);
            int tokenCount = countTokens(board);
            output.info("Tokens: " + tokenCount
                    + "   |   Empty: " + (board.length * board[0].length - tokenCount));
            if ("RANDOM".equals(mode)) {
                output.info("Random boards are NOT guaranteed to have a solution.");
                output.option("R", "Regenerate using the same dimensions and colors");
            } else {
                output.info("Loaded preset: " + currentPreset.label());
            }
            output.info("Solver not connected yet: board preview only.");
            String choice = read("ENTER: Main menu" + ("RANDOM".equals(mode) ? "  |  R: Regenerate" : "") + " : ");
            if (choice != null && choice.equalsIgnoreCase("R") && "RANDOM".equals(mode)) {
                board = boards.generate(board.length, board[0].length, activeColors);
                continue;
            }
            return;
        }
    }

    private void showOptions() throws IOException {
        while (true) {
            showScreen("OPTIONS");
            output.option("1", "ANSI colors: " + (output.colorsEnabled() ? "ON" : "OFF"));
            output.option("0", "Back to main menu");
            String choice = read("Select option: ");
            if (choice == null || choice.equals("0")) return;
            if (choice.equals("1")) {
                output.setColorsEnabled(!output.colorsEnabled());
            } else {
                output.error("Select 0 or 1.");
                pause();
            }
        }
    }

    private void showRules() throws IOException {
        showScreen("RULES");
        output.info("1. A colored token jumps horizontally or vertically into an empty cell.");
        output.info("2. The jumped token must have a different color from the moving token.");
        output.info("3. A continuous line of jumped tokens of the same color can be removed.");
        output.info("4. Win condition: exactly one token remains.");
        pause();
    }

    private int readNumber(String label, int min, int max) throws IOException {
        while (true) {
            String raw = requireInput(label);
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Invalid user input is handled via a clear message and retry.
            }
            output.error("Enter one integer between " + min + " and " + max + ".");
        }
    }

    private String requireInput(String prompt) throws IOException {
        String response = read(prompt);
        if (response == null) throw new IOException("Input stream closed");
        return response;
    }

    private String read(String prompt) throws IOException {
        output.prompt(prompt);
        String response = input.readLine();
        return response == null ? null : response.trim();
    }

    private void pause() throws IOException {
        read("Press ENTER to return... ");
    }

    private void showScreen(String title) {
        showSetup(board.length + " x " + board[0].length,
                Integer.toString(activeColors), mode);
        output.heading(title);
    }

    private void showSetup(String dimensions, String colors, String currentMode) {
        output.clearScreen();
        output.banner();
        output.status(dimensions, colors, currentMode);
    }

    private void showError(String message) throws IOException {
        showScreen("INVALID OPTION");
        output.error(message);
        pause();
    }

    private static int countTokens(int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int cell : row) if (cell != 0) count++;
        }
        return count;
    }
}
