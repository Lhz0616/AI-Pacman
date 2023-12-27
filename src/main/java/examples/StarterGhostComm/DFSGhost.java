package examples.StarterGhostComm;

import java.util.HashSet;
import java.util.Stack;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DFSGhost extends IndividualGhostController {
    private final Constants.GHOST ghost;
    private static final int PACMAN_DISTANCE = 15;
    private static final int PILL_PROXIMITY = 15;

    public DFSGhost(Constants.GHOST ghost) {
        super(ghost);
        this.ghost = ghost;
    }

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

        HashSet<Integer> visited = new HashSet<>(); // To track visited nodes
        Stack<Integer> stack = new Stack<>(); // Stack for DFS exploration
        stack.push(currentIndex);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();

            if (currentNode == pacmanIndex) {
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

        if (closeToPower(game)) {
            // Perform an action when close to a power pill
            // For example, move away from Ms. Pac-Man
            return game.getNextMoveAwayFromTarget(currentIndex, pacmanIndex, Constants.DM.PATH);
        }
        
        if (closeToMsPacman(game, currentIndex)) {
            // Perform an action when close to Ms. Pac-Man
            // For example, move towards Ms. Pac-Man
            return game.getNextMoveTowardsTarget(currentIndex, pacmanIndex, Constants.DM.PATH);
        }

        return MOVE.NEUTRAL; // No path found or other conditions, return a default move
    }

    // Helper methods for distance calculation, proximity checks, etc.
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();

        for (int powerPillIndex : powerPills) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(powerPillIndex);

            if (powerPillStillAvailable != null && powerPillStillAvailable) {
                int distance = game.getShortestPathDistance(powerPillIndex, pacmanNodeIndex);
                if (distance != -1 && distance < PILL_PROXIMITY) {
                    return true; // Found a power pill close to Ms. Pac-Man
                }
            }
        }

        return false; // No power pill close to Ms. Pac-Man
    }

    private boolean closeToMsPacman(Game game, int location) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        int distance = game.getShortestPathDistance(pacmanNodeIndex, location);

        return distance != -1 && distance < PACMAN_DISTANCE;
    }
}
