package pacman.controllers;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public abstract class MyPacmanController extends Controller<MOVE> {

	protected static final int MIN_DISTANCE = 20;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		int current = game.getPacmanCurrentNodeIndex();

		// Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE),
		// then, run away
		MOVE ratg = this.runAwayTheGhosts(game, current);
		if (ratg != null) {
			return ratg;
		}

		// Strategy 2: find the nearest edible ghost and go after them
		MOVE rteg = this.runToEdibleGhost(game, current);
		if (rteg != null) {
			return rteg;
		}

		// Strategy 3: go after the pills and power pills
		return this.runToThePills(game, current);
	}
	
	protected abstract MOVE runAwayTheGhosts(Game game, int current);
	
	protected abstract MOVE runToEdibleGhost(Game game, int current);
	
	protected abstract MOVE runToThePills(Game game, int current);
	
	/**
	 * check with power pills are available and add then to array
	 * 
	 * @param powerPills array with power pill indexes
	 * @param game       the Game instance
	 * @param targets    array with the possible target indexes
	 */
	protected void getPowerPillsTargets(int[] powerPills, Game game, ArrayList<Integer> targets) {
		for (int i = 0; i < powerPills.length; i++) {
			if (game.isPowerPillStillAvailable(i)) {
				targets.add(powerPills[i]);
			}
		}
	}

	/**
	 * 
	 * @param pills   array with pills indexes
	 * @param game    the Game instance
	 * @param targets array with the possible target indexes
	 */
	protected void getPillsTargets(int[] pills, Game game, ArrayList<Integer> targets) {
		for (int i = 0; i < pills.length; i++) { // check which pills are available
			if (game.isPillStillAvailable(i)) {
				for (GHOST ghost : GHOST.values()) {
					if ((game.getGhostEdibleTime(ghost) > 0 || game.getGhostLairTime(ghost) > 0) || (game
							.getShortestPathDistance(pills[i], game.getGhostCurrentNodeIndex(ghost)) > MIN_DISTANCE)) {
						targets.add(pills[i]);
					}
				}
			}
		}
	}
}
