package examples.StarterGhostComm;

import java.util.HashSet;
import java.util.Stack;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DFSGhost extends IndividualGhostController {
    private final Constants.GHOST ghost;
    private static int PACMAN_DISTANCE = 15;
    private static int PILL_PROXIMITY = 15;

    public static int lastRecordGhostEaten = 0;

    public DFSGhost(Constants.GHOST ghost, int TICK_THRESHOLD) {
        super(ghost);
        this.ghost = ghost;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        return performDFS(game);
    }

    private MOVE performDFS(Game game) {
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        int pacmanIndex = game.getPacmanCurrentNodeIndex();

        // Dynamic adjustment of thresholds based on game state
        if (game.isGhostEdible(ghost)) {
            PACMAN_DISTANCE = 20; // Increase distance threshold when ghost is edible
            PILL_PROXIMITY = 20; // Increase proximity threshold when ghost is edible
        } else {
            PACMAN_DISTANCE = 15; // Reset distance threshold when ghost is not edible
            PILL_PROXIMITY = 15; // Reset proximity threshold when ghost is not edible
        }
        
        HashSet<Integer> visited = new HashSet<>(); // To track visited nodes
        Stack<Integer> stack = new Stack<>(); // Stack for DFS exploration
        stack.push(currentIndex);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();
            if (currentNode == pacmanIndex) {
                if(game.getGhostEdibleTime(ghost) > 0) {
//                    System.out.println("away");
                    return game.getApproximateNextMoveAwayFromTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
                }
                return game.getNextMoveTowardsTarget(currentIndex, currentNode, Constants.DM.PATH); // Return the move towards Pac-Man
            }

            visited.add(currentNode);

            // Explore neighboring nodes in a DFS manner
            for (int neighbor : game.getNeighbouringNodes(currentNode)) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }

        // No direct path to Pac-Man found, apply other strategies
        if (closeToPower(game, pacmanIndex)) {
            // Move away from Ms. Pac-Man if close to a power pill
            return game.getNextMoveAwayFromTarget(currentIndex, pacmanIndex, Constants.DM.PATH);
        }

        if (closeToMsPacman(game, currentIndex, pacmanIndex)) {
            // Move towards Ms. Pac-Man if within a certain distance
            return game.getNextMoveTowardsTarget(currentIndex, pacmanIndex, Constants.DM.PATH);
        }
        if(game.wasGhostEaten(ghost)){
            lastRecordGhostEaten ++;
            System.out.println("Ghost Eaten: " + lastRecordGhostEaten);
        }
        return MOVE.NEUTRAL; // No immediate path found, return a default move
    }

    // Helper methods for distance calculation, proximity checks, etc.
    private boolean closeToPower(Game game, int pacmanIndex) {
        int[] powerPills = game.getActivePowerPillsIndices(); // Get active power pills
    
        for (int powerPillIndex : powerPills) {
            int distance = game.getShortestPathDistance(powerPillIndex, pacmanIndex);
    
            if (distance != -1 && distance < PILL_PROXIMITY) {
                return true; // Found a power pill close to Ms. Pac-Man
            }
        }
    
        return false; // No power pill close to Ms. Pac-Man
    }
    

    private boolean closeToMsPacman(Game game, int location, int pacmanIndex) {
        int distance = game.getShortestPathDistance(pacmanIndex, location);
        return distance != -1 && distance < PACMAN_DISTANCE;
    }
    
}
