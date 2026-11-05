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
        int[] pos = action.getPos();
        int color = action.getCellColor();


        currentState.colorCase(0, pos[0], pos[1]); 

        
       int distance = action.getJumpedCount();

       int stepX;
       int stepY;

        switch (action.getDirection()) {
            case Up:
                stepX = -1;
                break;
            case Down:
                stepX = 1;
                break;
            case Left:
                stepY = -1;
                break;
            case Right:
                stepY = 1;
                break;
            default:
                break;
        }

        for (int i = 1; i <= action.getJumpedCount(); i++) {
            int newX = pos[0] + stepX * i;
            int newY = pos[1] + stepY * i;
            currentState.colorCase(0, newX, newY); // Set the jumped cells to empty (0)
        }

        int targetX = pos[0] + stepX * (action.getJumpedCount() + 1);
        int targetY = pos[1] + stepY * (action.getJumpedCount() + 1);
        currentState.colorCase(action.getCellColor(), targetX, targetY);
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
                    wrongPosX = pos[0] + lastAction.getJumpedCount()+1; 
                    wrongPosY = pos[1];
                    break;
                case Down:
                    wrongPosX = pos[0] - lastAction.getJumpedCount()+1; 
                    wrongPosY = pos[1];
                    break;
                case Left:
                    wrongPosX = pos[0];
                    wrongPosY = pos[1] + lastAction.getJumpedCount()+1; 
                    break;
                case Right:
                    wrongPosX = pos[0];
                    wrongPosY = pos[1] - lastAction.getJumpedCount()+1; 
                    break;
                default:
                    throw new IllegalArgumentException("Unknown direction: " + lastAction.getDirection());
            }

            currentState.colorCase(0, wrongPosX, wrongPosY); // Set the cell to empty (0) after reverting

        }


    }



    public void run() {
        // Main game loop logic can be implemented here
        this.currentState.displayGrid();
    }

    public State getCurrentState() {
        return currentState;
    }

    public MemoryOfActions getMemory() {
        return memory;
    }
}
