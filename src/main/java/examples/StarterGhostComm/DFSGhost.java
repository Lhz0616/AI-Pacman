package examples.StarterGhostComm;

import java.util.HashSet;
import java.util.Stack;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DFSGhost extends IndividualGhostController {
    private final Constants.GHOST ghost;
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

        return MOVE.NEUTRAL; // No path found or other conditions, return a default move
    }
}
