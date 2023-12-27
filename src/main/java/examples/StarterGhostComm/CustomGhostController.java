package examples.StarterGhostComm;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CustomGhostController extends IndividualGhostController {
    private static final int CROWDED_DISTANCE = 30;
    private static final int PACMAN_DISTANCE = 15;
    private static final int PILL_PROXIMITY = 15;

    private int TICK_THRESHOLD;
    private int lastPacmanIndex = -1;
    private int tickSeen = -1;
    public static int lastRecordGhostEaten = 0;
    private final GHOST ghost;

    public CustomGhostController(GHOST ghost) {
        super(ghost);
        this.ghost = ghost;
    }

    public CustomGhostController(Constants.GHOST ghost, int TICK_THRESHOLD) {
        super(ghost);
        this.TICK_THRESHOLD = TICK_THRESHOLD;
        this.ghost = ghost;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Determine which search algorithm to use based on some condition or parameter
        switch (selectedAlgorithm) {
            case UCS:
                return performUCS(game);
            case DFS:
                return performDFS(game);
            case BFS:
                return performBFS(game);
            case A_STAR:
                return performAStar(game);
            default:
                // If no valid algorithm selected, return a default move
                return MOVE.NEUTRAL;
        }
    }

    private MOVE performUCS(Game game) {
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
    
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost));
        HashSet<Integer> explored = new HashSet<>();
    
        frontier.add(new Node(currentIndex, null, 0.0));
    
        while (!frontier.isEmpty()) {
            Node currentNode = frontier.poll();
    
            if (currentNode.index == pacmanIndex) {
                return reconstructPath(currentNode, currentIndex, game);
            }
    
            explored.add(currentNode.index);
    
            for (int neighbor : game.getNeighbouringNodes(currentNode.index)) {
                double neighborCost = currentNode.cost + 1.0; // Assuming uniform cost
    
                if (!explored.contains(neighbor)) {
                    frontier.add(new Node(neighbor, currentNode, neighborCost));
                }
            }
        }
    
        return MOVE.NEUTRAL; // No path found or other conditions, return a default move
    }
    
    // Helper method to reconstruct the path and return the next move
    private MOVE reconstructPath(Node goalNode, int currentIndex, Game game) {
        Node currentNode = goalNode;
        while (currentNode.parent != null && currentNode.parent.index != currentIndex) {
            currentNode = currentNode.parent;
        }
    
        // If the current node's parent is the ghost's current position
        if (currentNode.parent != null && currentNode.parent.index == currentIndex) {
            return game.getNextMoveTowardsTarget(currentIndex, currentNode.index, DM.PATH);
        }
    
        // If no valid path found or other conditions, return a default move
        return MOVE.NEUTRAL;
    }
    
    // Node class for UCS exploration
    private static class Node {
        int index;
        Node parent;
        double cost;
    
        public Node(int index, Node parent, double cost) {
            this.index = index;
            this.parent = parent;
            this.cost = cost;
        }
    }
    
    private MOVE performDFS(Game game) {
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
    
        HashSet<Integer> visited = new HashSet<>(); // To track visited nodes
    
        // Start DFS exploration
        MOVE move = depthFirstSearch(game, currentIndex, pacmanIndex, visited);
    
        // If no path found or other conditions, return a default move
        return (move != null) ? move : MOVE.NEUTRAL;
    }
    
    // Recursive DFS exploration method
    private MOVE depthFirstSearch(Game game, int current, int target, HashSet<Integer> visited) {
        // Goal test - If the current node is the Pac-Man's location
        if (current == target) {
            // Return the next move towards Pac-Man
            // Example: return game.getNextMoveTowardsTarget(currentIndex, target, DM.PATH);
            // Placeholder return
            return MOVE.UP; // Replace this with actual move
        }
    
        visited.add(current);
    
        // Explore neighboring nodes in a DFS manner
        for (int neighbor : game.getNeighbouringNodes(current)) {
            if (!visited.contains(neighbor)) {
                MOVE move = depthFirstSearch(game, neighbor, target, visited);
                if (move != null) {
                    return move; // Return the move towards Pac-Man found in the subtree
                }
            }
        }
    
        return null; // Return null if no valid move found towards Pac-Man
    }
    
    private MOVE performBFS(Game game) {
    int currentIndex = game.getGhostCurrentNodeIndex(ghost);
    int pacmanIndex = game.getPacmanCurrentNodeIndex();

    Queue<Node> queue = new LinkedList<>();
    HashSet<Integer> visited = new HashSet<>();

    queue.add(new Node(currentIndex, null));

    while (!queue.isEmpty()) {
        Node currentNode = queue.poll();

        if (currentNode.index == pacmanIndex) {
            return reconstructPath(currentNode, currentIndex, game);
        }

        visited.add(currentNode.index);

        for (int neighbor : game.getNeighbouringNodes(currentNode.index)) {
            if (!visited.contains(neighbor)) {
                queue.add(new Node(neighbor, currentNode));
                visited.add(neighbor); // Mark node as visited to avoid revisiting
            }
        }
    }

    return MOVE.NEUTRAL; // No path found or other conditions, return a default move
}

// Helper method to reconstruct the path and return the next move
private MOVE reconstructPath(Node goalNode, int currentIndex, Game game) {
    Node currentNode = goalNode;
    while (currentNode.parent != null && currentNode.parent.index != currentIndex) {
        currentNode = currentNode.parent;
    }

    // If the current node's parent is the ghost's current position
    if (currentNode.parent != null && currentNode.parent.index == currentIndex) {
        return game.getNextMoveTowardsTarget(currentIndex, currentNode.index, DM.PATH);
    }

    // If no valid path found or other conditions, return a default move
    return MOVE.NEUTRAL;
}

// Node class for BFS exploration
private static class Node {
    int index;
    Node parent;

    public Node(int index, Node parent) {
        this.index = index;
        this.parent = parent;
    }
}

private MOVE performAStar(Game game) {
    int currentIndex = game.getGhostCurrentNodeIndex(ghost);
    int pacmanIndex = game.getPacmanCurrentNodeIndex();

    PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fCost));
    HashSet<Integer> visited = new HashSet<>();

    frontier.add(new Node(currentIndex, null, 0.0, game.getManhattanDistance(currentIndex, pacmanIndex)));

    while (!frontier.isEmpty()) {
        Node currentNode = frontier.poll();

        if (currentNode.index == pacmanIndex) {
            return reconstructPath(currentNode, currentIndex, game);
        }

        visited.add(currentNode.index);

        for (int neighbor : game.getNeighbouringNodes(currentNode.index)) {
            if (!visited.contains(neighbor)) {
                double neighborCost = currentNode.gCost + 1.0; // Assuming uniform cost

                // Calculate heuristic (Manhattan distance to Pac-Man)
                double heuristic = game.getManhattanDistance(neighbor, pacmanIndex);

                double fCost = neighborCost + heuristic;

                frontier.add(new Node(neighbor, currentNode, neighborCost, fCost));
            }
        }
    }

    return MOVE.NEUTRAL; // No path found or other conditions, return a default move
}

// Helper method to reconstruct the path and return the next move
private MOVE reconstructPath(Node goalNode, int currentIndex, Game game) {
    Node currentNode = goalNode;
    while (currentNode.parent != null && currentNode.parent.index != currentIndex) {
        currentNode = currentNode.parent;
    }

    // If the current node's parent is the ghost's current position
    if (currentNode.parent != null && currentNode.parent.index == currentIndex) {
        return game.getNextMoveTowardsTarget(currentIndex, currentNode.index, DM.PATH);
    }

    // If no valid path found or other conditions, return a default move
    return MOVE.NEUTRAL;
}

// Node class for A* exploration
private static class Node {
    int index;
    Node parent;
    double gCost; // Cost from start node to current node
    double fCost; // fCost = gCost + heuristic cost

    public Node(int index, Node parent, double gCost, double fCost) {
        this.index = index;
        this.parent = parent;
        this.gCost = gCost;
        this.fCost = fCost;
    }
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
