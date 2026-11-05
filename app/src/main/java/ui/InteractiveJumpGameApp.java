package ui;

import jumpgame.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/** Console navigation only; the domain classes decide which moves are legal. */
public final class InteractiveJumpGameApp {
    private static final int MOVES_PER_PAGE = 8;

    private final BufferedReader input;
    private final ConsoleReporter output;
    private final RandomBoardFactory boards;

    private DemoBoards.Preset currentPreset = DemoBoards.Preset.STANDARD;
    private GameSession session = new GameSession(currentPreset.board());
    private String mode = "PRESET";
    private int activeColors = currentPreset.colorCount();
    private String notice;

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
                output.option("3", "Play current board");
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
                activeColors = currentPreset.colorCount();
                mode = "PRESET";
                loadBoard(currentPreset.board());
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

        activeColors = colorCount;
        mode = "RANDOM";
        loadBoard(boards.generate(rows, columns, colorCount));
        showBoard();
    }

    /** Play screen is a controller. The same GameSession persists across screen changes. */
    private void showBoard() throws IOException {
        while (true) {
            showScreen("CURRENT BOARD");
            output.board(session.snapshot());
            int[][] board = session.snapshot();
            int tokens = session.tokenCount();
            output.info("Tokens: " + tokens
                    + "   |   Empty: " + (board.length * board[0].length - tokens)
                    + "   |   Moves played: " + session.movesPlayed());
            if (session.isSolved()) {
                output.success("VICTORY! One token remains.");
            } else if (session.availableActions().isEmpty()) {
                output.error("No legal moves remain. You can undo or choose another board.");
            }
            if (notice != null) {
                output.success(notice);
                notice = null;
            }
            if ("RANDOM".equals(mode)) {
                output.info("Random boards are NOT guaranteed to have a solution.");
                output.option("R", "Regenerate board (resets move history)");
            } else {
                output.info("Loaded preset: " + currentPreset.label());
            }
            output.option("1", "Show legal moves");
            output.option("2", "Perform a legal move");
            output.option("3", "Undo last move");
            output.info("Backtracking solver not connected yet.");
            output.option("0", "Main menu");

            String choice = read("Select option: ");
            if (choice == null || choice.equals("0")) return;
            switch (choice.toUpperCase()) {
                case "1" -> showMoves(false);
                case "2" -> showMoves(true);
                case "3" -> {
                    if (session.undo()) notice = "Last move undone.";
                    else { output.error("Nothing to undo."); pause(); }
                }
                case "R" -> {
                    if ("RANDOM".equals(mode)) {
                        loadBoard(boards.generate(board.length, board[0].length, activeColors));
                        notice = "New random board generated. History reset.";
                    } else {
                        output.error("Regenerate is available only for random boards.");
                        pause();
                    }
                }
                default -> {
                    output.error("Choose 0, 1, 2, 3" + ("RANDOM".equals(mode) ? " or R" : "") + ".");
                    pause();
                }
            }
        }
    }

    /** Numbered moves are paginated so 10x10 boards never flood the terminal. */
    private void showMoves(boolean selectMove) throws IOException {
        List<Action> moves = session.availableActions();
        int page = 0;
        while (true) {
            showScreen(selectMove ? "PERFORM A MOVE" : "LEGAL MOVES");
            output.board(session.snapshot());
            if (moves.isEmpty()) {
                output.info("No legal moves in this position.");
                pause();
                return;
            }
            int pageCount = (moves.size() + MOVES_PER_PAGE - 1) / MOVES_PER_PAGE;
            int first = page * MOVES_PER_PAGE;
            int last = Math.min(first + MOVES_PER_PAGE, moves.size());
            output.info("Legal moves: " + moves.size() + "   |   Page: " + (page + 1) + "/" + pageCount);
            for (int i = first; i < last; i++) {
                output.option(Integer.toString(i - first + 1), describeMove(moves.get(i)));
            }
            if (page > 0) output.option("P", "Previous page");
            if (page + 1 < pageCount) output.option("N", "Next page");
            output.option("0", "Back to board");
            String response = read(selectMove ? "Choose a move from this page: " : "N/P: Navigate | ENTER: Back: ");
            if (response == null || response.equals("0") || response.isBlank()) return;
            if (response.equalsIgnoreCase("N") && page + 1 < pageCount) { page++; continue; }
            if (response.equalsIgnoreCase("P") && page > 0) { page--; continue; }
            if (selectMove) {
                try {
                    int localIndex = Integer.parseInt(response) - 1;
                    int index = first + localIndex;
                    if (localIndex < 0 || index >= last) throw new NumberFormatException();
                    Action selected = session.performMove(index);
                    notice = "Move performed: " + describeMove(selected);
                    return;
                } catch (NumberFormatException e) {
                    // Invalid input must not mutate the game state.
                }
            }
            output.error(selectMove ? "Choose a listed move, N, P or 0." : "Choose N, P or 0.");
            pause();
        }
    }

    private static String describeMove(Action action) {
        int[] source = action.getPos();
        int deltaRow = switch (action.getDirection()) {
            case Up -> -1;
            case Down -> 1;
            default -> 0;
        };
        int deltaColumn = switch (action.getDirection()) {
            case Left -> -1;
            case Right -> 1;
            default -> 0;
        };
        int distance = action.getJumpedCount() + 1;
        int destRow = source[0] + deltaRow * distance;
        int destColumn = source[1] + deltaColumn * distance;
        return "(" + (source[0] + 1) + "," + (source[1] + 1) + ") -> ("
                + (destRow + 1) + "," + (destColumn + 1) + ") "
                + action.getDirection().name().toUpperCase(Locale.ROOT)
                + " | jump " + action.getJumpedCount()
                + " color " + action.getEliminatedColor();
    }

    private void loadBoard(int[][] board) {
        session = new GameSession(board);
        notice = null;
    }

    private void showOptions() throws IOException {
        while (true) {
            showScreen("OPTIONS");
            output.option("1", "ANSI colors: " + (output.colorsEnabled() ? "ON" : "OFF"));
            output.option("0", "Back to main menu");
            String choice = read("Select option: ");
            if (choice == null || choice.equals("0")) return;
            if (choice.equals("1")) output.setColorsEnabled(!output.colorsEnabled());
            else { output.error("Select 0 or 1."); pause(); }
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
                // Invalid user input should retry without crashing.
            }
            output.error("Enter one integer between " + min + " and " + max + ".");
        }
    }

    private String requireInput(String label) throws IOException {
        String value = read(label);
        if (value == null) throw new IOException("Input stream closed");
        return value;
    }

    private String read(String label) throws IOException {
        output.prompt(label);
        String value = input.readLine();
        return value == null ? null : value.trim();
    }

    private void pause() throws IOException { read("Press ENTER to return... "); }

    private void showScreen(String title) {
        int[][] board = session.snapshot();
        showSetup(board.length + " x " + board[0].length, Integer.toString(activeColors), mode);
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
}
