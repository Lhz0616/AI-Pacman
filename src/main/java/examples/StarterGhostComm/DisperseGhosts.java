package examples.StarterGhostComm;

import java.util.EnumMap;

import com.fossgalaxy.object.annotations.ObjectDef;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

public class DisperseGhosts extends MASController{

    public DisperseGhosts(){
        this(50);
    }

    @ObjectDef("DG")
	public DisperseGhosts(int TICK_THRESHOLD) {
		super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
		controllers.put(GHOST.BLINKY, new DisperseGhost(GHOST.BLINKY, TICK_THRESHOLD));
        controllers.put(GHOST.INKY, new DisperseGhost(GHOST.INKY, TICK_THRESHOLD));
        controllers.put(GHOST.PINKY, new DisperseGhost(GHOST.PINKY, TICK_THRESHOLD));
        controllers.put(GHOST.SUE, new DisperseGhost(GHOST.SUE, TICK_THRESHOLD));
	}
    
    @Override
    public String getName() {
        return "DG";
    }
}
