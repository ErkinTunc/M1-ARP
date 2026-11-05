package app;

import ui.InteractiveJumpGameApp;

/** Application entry point. No presentation or game rules here. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        InteractiveJumpGameApp.runInConsole();
    }
}
