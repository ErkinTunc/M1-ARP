package ui;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
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
                new RandomBoardFactory(new Random(2026))).run();
        return sink.toString(StandardCharsets.UTF_8);
    }

    @Test
    void presetMoveAndUndoFlowWorks() {
        // Main > preset > STARTER > perform > move 1 > undo > menu > exit.
        String transcript = runScript("1\n1\n2\n1\n3\n0\n0\n");
        assertTrue(transcript.contains("PERFORM A MOVE"));
        assertTrue(transcript.contains("VICTORY! One token remains."));
        assertTrue(transcript.contains("Last move undone."));
        assertTrue(transcript.contains("Moves played: 0"));
    }

    @Test
    void randomRegenerationAndActionsFlowWorks() {
        String transcript = runScript("2\n3\n4\n2\nR\n1\n\n0\n0\n");
        assertTrue(transcript.contains("MODE RANDOM"));
        assertTrue(transcript.contains("New random board generated."));
        assertTrue(transcript.contains("LEGAL MOVES"));
    }

    @Test
    void invalidMoveInputDoesNotApplyMove() {
        String transcript = runScript("1\n1\n2\n9\n\n0\n0\n0\n");
        assertTrue(transcript.contains("Choose a listed move"));
        assertFalse(transcript.contains("VICTORY!"));
    }
}
