package examples.StarterGhostComm;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.comms.Messenger;

public class DisperseGhost extends IndividualGhostController{
    private static final int CROWDED_DISTANCE = 30;
    private static final int PACMAN_DISTANCE = 15;
    private static final int PILL_PROXIMITY = 15;

    private int TICK_THRESHOLD;
    private int lastPacmanIndex = -1;
    private int tickSeen = -1;
    public static int lastRecordGhostEaten = 0;

    public DisperseGhost(GHOST ghost) {
        super(ghost);
    }

    public DisperseGhost(Constants.GHOST ghost, int TICK_THRESHOLD) {
        super(ghost);
        this.TICK_THRESHOLD = TICK_THRESHOLD;
    } 

    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Housekeeping - throw out old info
        int currentTick = game.getCurrentLevelTime();
        if (currentTick <= 2 || currentTick - tickSeen >= TICK_THRESHOLD) {
            lastPacmanIndex = -1;
            tickSeen = -1;
        }

        // Can we see PacMan? If so tell people and update our info
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1) {
            lastPacmanIndex = pacmanIndex;
            tickSeen = game.getCurrentLevelTime();
            if (messenger != null) {
                messenger.addMessage(new BasicMessage(ghost, null, BasicMessage.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
            }
        }

        // Has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghost)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    if (message.getTick() > tickSeen && message.getTick() < currentTick) { // Only if it is newer information
                        lastPacmanIndex = message.getData();
                        tickSeen = message.getTick();
                    }
                }
            }
        }
        if (pacmanIndex == -1) {
            pacmanIndex = lastPacmanIndex;
        }

        if(game.wasGhostEaten(ghost)){
            lastRecordGhostEaten ++;
            System.out.println("Ghost Eaten: " + lastRecordGhostEaten);
        }

        Boolean requiresAction = game.doesGhostRequireAction(ghost);
        if (requiresAction != null && requiresAction)        //if ghost requires an action
        {
            // if ghost is crowded and not close to pacman, disperse
            if(isCrowded(game) && !closeToMsPacman(game, currentIndex))
                return getRetreatActions(game);

            // if edible or Ms Pacman is close to powerpill, move away from Ms Pacman
            else if(game.getGhostEdibleTime(ghost) > 0 || closeToPower(game))
                return game.getApproximateNextMoveAwayFromTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost),DM.PATH);

            // else go towards Ms pacman
            else
                return game.getApproximateNextMoveTowardsTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost),DM.PATH);
        }
        return null;
    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            if (pacmanNodeIndex == -1) {
                pacmanNodeIndex = lastPacmanIndex;
            }
            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }
    
    private boolean closeToMsPacman(Game game, int location){
        if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), location) < PACMAN_DISTANCE)
            return true;
        
        return false;
    }

    private boolean isCrowded(Game game){
        float distance = 0;
        GHOST[] ghosts = GHOST.values(); 

        for(int i = 0; i<ghosts.length - 1; i++){
            for(int j = i+1; j<ghosts.length; j++)
                distance += game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), game.getGhostCurrentNodeIndex(ghosts[j]));
        }

        return (distance/6) < CROWDED_DISTANCE ? true : false;
    }

    private MOVE getRetreatActions(Game game){
        int ghostIndex = game.getGhostCurrentNodeIndex(ghost);

        if(game.getGhostEdibleTime(ghost) == 0 && game.getShortestPathDistance(ghostIndex, game.getPacmanCurrentNodeIndex()) < PACMAN_DISTANCE)
            return game.getApproximateNextMoveTowardsTarget(ghostIndex, game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
        

        else{

            return game.getApproximateNextMoveTowardsTarget(ghostIndex, game.getPowerPillIndices()[ghost.ordinal()], game.getGhostLastMoveMade(ghost), DM.PATH);
        }
        
    }

}
