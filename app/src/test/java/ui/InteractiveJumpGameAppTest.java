
package ui;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InteractiveJumpGameAppTest {

    private String runScript(String script) {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(sink, true, StandardCharsets.UTF_8);

        new InteractiveJumpGameApp(
            new BufferedReader(new StringReader(script)),
            new ConsoleReporter(out, false, 80),
            new RandomBoardFactory(new Random(2026))
        ).run();

        return sink.toString(StandardCharsets.UTF_8);
    }

    private void assertContains(String output, String... messages) {
        for (String message : messages) {
            assertTrue(output.contains(message),
                "Missing message: " + message);
        }
    }

    @Test
    void presetMoveAndUndoWorks() {
        String output = runScript("1\n1\n1\n1\n2\n0\n0\n");

        assertContains(output,
            "LEGAL MOVES",
            "VICTORY! One token remains.",
            "Last move undone.",
            "Moves played: 0"
        );
    }

    @Test
    void randomBoardRegenerationWorks() {
        String output = runScript("2\n3\n4\n2\nR\n1\n0\n0\n0\n");

        assertContains(output,
            "MODE RANDOM",
            "New random board generated.",
            "LEGAL MOVES"
        );
    }

    @Test
    void invalidMoveIsRejected() {
        String output = runScript("1\n1\n1\n9\n0\n0\n0\n");

        assertContains(output, "Choose a number between 0 and 1.");
        assertFalse(output.contains("VICTORY!"));
    }

    @Test
    void movesAreNumberedWithoutPagination() {
        String output = runScript("1\n1\n1\n1\n0\n0\n");

        assertContains(output,
            "[1]  (1,1) -> (1,3) RIGHT",
            "Select a move (0-1):",
            "VICTORY! One token remains."
        );

        assertFalse(output.contains("N/P:"));
        assertFalse(output.contains("Page: "));
    }
}
