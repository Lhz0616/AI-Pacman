import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.DisperseGhosts;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.POCommGhosts;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import pacman.Executor;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;

import pacman.game.Constants.*;
import pacman.game.internal.POType;

import java.io.IOException;
import java.util.EnumMap;

import examples.StarterPacMan.AStarSearch.AStarSearchPacMan;
import examples.StarterPacMan.GeneticAlgorithm.EvolvedPacMan;
import examples.StarterPacMan.MCTS.MCTS;


/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void main(String[] args) throws IOException{
    	
    	int sightRadius = 500; // 5000 is maximum

        Executor executor = new Executor.Builder()
                .setVisual(true)
                .setPacmanPO(false)
                .setTickLimit(40000)
                .setScaleFactor(2) // Increase game visual size
                .setPOType(POType.RADIUS) // pacman sense objects around it in a radius wide fashion instead of straight line sights
                .setSightLimit(sightRadius) // The sight radius limit, set to maximum 
                .build();

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);

        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
        
        int speed = 1; // smaller number will run faster
        
        MASController ghosts = new DisperseGhosts(50);
        // MASController ghosts = new POCommGhosts(50);

        // A star search algorithm 
        executor.runGame(new AStarSearchPacMan(ghosts), ghosts, speed);

        // MCTS Algorithm
        // executor.runGame(new MCTS(), ghosts, speed);

        // Genetic Algorithm
        // executor.runGame(new EvolvedPacMan("bestGene.txt"), ghosts, speed);
                
    }
}
