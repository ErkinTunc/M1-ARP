package jumpgame;

public class Director {
    private State currentState;
    private MemoryOfActions memory;

    public Director(State initialState) {
        this.currentState = initialState;
        this.memory = new MemoryOfActions();
    }

    /**
     * Performs an action on the current state and records it in memory.
     * @param action The action to be performed, which includes the direction, cell number, position, and color.
     */
    public void performAction(Action action) {

        memory.addAction(action);
    }


    
    /**
     * Reverts the current state to the previous state based on the last action performed.
     */
    public void backtrack() {
 
        if (!memory.getActions().isEmpty()) {

            Action lastAction = memory.getActions().remove(memory.getActions().size() - 1);

            int[] pos = lastAction.getPos();            

            int color = lastAction.getCellColor(); 

            currentState.colorCase(color, pos[0], pos[1]); // Revert the color of the cell to its previous state

            int wrongPosX ;
            int wrongPosY ;
            
            switch (lastAction.getDirection()) {


                case Up:
                    wrongPosX = pos[0] + lastAction.getCellColor(); 
                    wrongPosY = pos[1];
                    break;
                case Down:
                    wrongPosX = pos[0] - lastAction.getCellColor(); 
                    wrongPosY = pos[1];
                    break;
                case Left:
                    wrongPosX = pos[0];
                    wrongPosY = pos[1] + lastAction.getCellColor(); 
                    break;
                case Right:
                    wrongPosX = pos[0];
                    wrongPosY = pos[1] - lastAction.getCellColor(); 
                    break;
                default:
                    throw new IllegalArgumentException("Unknown direction: " + lastAction.getDirection());
            }

            currentState.colorCase(0, wrongPosX, wrongPosY); // Set the cell to empty (0) after reverting

        }


    }

    

    public State getCurrentState() {
        return currentState;
    }

    public MemoryOfActions getMemory() {
        return memory;
    }
}
