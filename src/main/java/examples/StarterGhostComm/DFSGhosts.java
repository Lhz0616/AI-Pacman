package examples.StarterGhostComm;

import java.util.EnumMap;

import com.fossgalaxy.object.annotations.ObjectDef;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

public class DFSGhosts extends MASController{

    public DFSGhosts(){
        this(50);
    }

    @ObjectDef("DFS")
	public DFSGhosts(int TICK_THRESHOLD) {
		super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
		controllers.put(GHOST.BLINKY, new DFSGhost(GHOST.BLINKY, TICK_THRESHOLD));
        controllers.put(GHOST.INKY, new DFSGhost(GHOST.INKY, TICK_THRESHOLD));
        controllers.put(GHOST.PINKY, new DFSGhost(GHOST.PINKY, TICK_THRESHOLD));
        controllers.put(GHOST.SUE, new DFSGhost(GHOST.SUE, TICK_THRESHOLD));
	}
    
    @Override
    public String getName() {
        return "DFS";
    }
}
