package jumpgame;
import java.util.List;

public class Director {
    private final State currentState;
    private final MemoryOfActions memory;

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

       int stepX =0;
       int stepY =0;

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

        for (int i = 1; i <= distance; i++) {
            int newX = pos[0] + stepX * i;
            int newY = pos[1] + stepY * i;
            currentState.colorCase(0, newX, newY); // Set the jumped cells to empty (0)
        }

        int targetX = pos[0] + stepX * (distance + 1);
        int targetY = pos[1] + stepY * (distance + 1);
        currentState.colorCase(action.getCellColor(), targetX, targetY);
    }
        

        


    

    /**
     * Reverts the current state to the previous state based on the last action performed.
     */
    public void backtrack() {
 
        if (memory.getActions().isEmpty()) {
            return;
        }

            Action lastAction = memory.getActions().remove(memory.getActions().size() - 1);

            int[] pos = lastAction.getPos();            

            int color = lastAction.getCellColor(); 

            currentState.colorCase(color, pos[0], pos[1]); // Revert the color of the cell to its previous state


            int stepX =0 ;
            int stepY = 0;

            switch (lastAction.getDirection()) {


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
                    throw new IllegalArgumentException("Unknown direction: " + lastAction.getDirection());
            }


        

        for(int i = 0; i < lastAction.getJumpedCount(); i++) {
            
            int newX = pos[0] + stepX * (i + 1);
            int newY = pos[1] + stepY * (i + 1);
            currentState.colorCase(lastAction.getEliminatedColor(), newX, newY); // Restore the eliminated cells to their previous color
        }
        int finalX = pos[0] + stepX * (lastAction.getJumpedCount() + 1);
        int finalY = pos[1] + stepY * (lastAction.getJumpedCount() + 1);
        currentState.colorCase(0, finalX, finalY);

    }




    /**
     * Runs the game by attempting to solve it. If a solution is found, it displays the final state and the sequence of actions taken. If no solution exists, it indicates that as well.
     * The method uses a recursive backtracking approach to explore all possible actions from the current state.
     * If a final state is reached, the method returns true. If no solution is found, it backtracks and tries other actions.
     */
    public void run() {
        
        if(solve()){
            
            System.out.println("Solution found!");
            this.currentState.displayGrid();
            memory.getActions().forEach(action -> {
                System.out.println("Pion " + action.getCellColor() + " en " + action.getPos()[0] + "," + action.getPos()[1] + " avec la direction " + action.getDirection() + " avec " + action.getJumpedCount() + " pion(s) sauté(s)");
            });
        } else {
            System.out.println("No solution exists.");
            this.currentState.displayGrid();
        }


    }

    /**
     * Attempts to solve the game by recursively exploring all possible actions from the current state.
     * If a final state is reached, the method returns true. If no solution is found, it backtracks and tries other actions.
     * @return true if a solution is found, false otherwise
     */
    public boolean solve(){

            if(currentState.isFinal()) {
                return true;
            }

            List<Action> possibleActions = currentState.getPossibleActions();

            for(Action action : possibleActions) {
                System.out.println("Tente pion "+ action.getCellColor() + " en " + action.getPos()[0] + "," + action.getPos()[1] + "avec la direction"+ action.getDirection() + " avec " + action.getJumpedCount() + " pion(s) sauté(s)");
                performAction(action);
                this.currentState.displayGrid();

                if(solve()) {
                    return true;
                }
                System.out.println("Backtrack sur pion "+ action.getCellColor() + " en " + action.getPos()[0] + "," + action.getPos()[1] + "avec la direction"+ action.getDirection() + " avec " + action.getJumpedCount() + " pion(s) sauté(s)");
                backtrack();
                this.currentState.displayGrid();
            }
            return false;
    }

    public State getCurrentState() {
        return currentState;
    }

    public MemoryOfActions getMemory() {
        return memory;
    }
}
