package jumpgame;

import java.util.ArrayList;
import java.util.List;

public class MemoryOfActions {
    private final List<Action> actions;

    public MemoryOfActions() {
        this.actions = new ArrayList<>();
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public List<Action> getActions() {
        return actions;
    }
    
}
