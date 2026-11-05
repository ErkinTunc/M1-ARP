package jumpgame;

import java.util.ArrayList;
import java.util.List;

/**
 * MemoryOfActions is a class that maintains a record of actions performed in
 * the game. It stores a list of Action objects, allowing for tracking and
 * retrieval of actions taken during gameplay.
 */
public class MemoryOfActions {

    // -------------- Attributes ----------------

    private final List<Action> actions;

    // -------------- Constructor ----------------

    /**
     * Constructs a new MemoryOfActions object.
     */
    public MemoryOfActions() {
        this.actions = new ArrayList<>();
    }

    /**
     * Adds an action to the memory.
     * 
     * @param action The action to be added.
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Returns the list of actions stored in the memory.
     * 
     * @return The list of actions.
     */
    public List<Action> getActions() {
        return actions;
    }

}
