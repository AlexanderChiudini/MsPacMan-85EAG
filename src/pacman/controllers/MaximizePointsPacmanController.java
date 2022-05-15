package pacman.controllers;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * The objective is to eat all the power pills then eat all edible ghosts. This
 * controller prioritize eat a edible ghost to maximize points
 *
 */
public final class MaximizePointsPacmanController extends MyPacmanController {

	private static final int MIN_DISTANCE = 20;

	/**
	 * When PacMan is near a ghost, run away
	 * 
	 * @param game    the Game instance
	 * @param current current index in game
	 * @return the next movement to do
	 */
	protected MOVE runAwayTheGhosts(Game game, int current) {
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
				if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE) {
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				}
			}
		}
		return null;
	}

	/**
	 * Goes to nearest edible ghost in game
	 * 
	 * @param game    the Game instance
	 * @param current the current index in game
	 * @return the next movement to do
	 */
	protected MOVE runToEdibleGhost(Game game, int current) {
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;

		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostEdibleTime(ghost) > 0) {
				int distance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}
		}

		if (minGhost != null) { // we found an edible ghost
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(minGhost), DM.PATH);
		}

		return null;
	}

	/**
	 * Create an array of possible index target, prioritize the power pill in self
	 * array
	 * 
	 * @param game    the Game instance
	 * @param current the current index in game
	 * @return the next movement to do
	 */
	protected MOVE runToThePills(Game game, int current) {
		int[] pills = game.getPillIndices();
		int[] powerPills = game.getPowerPillIndices();

		ArrayList<Integer> targets = new ArrayList<Integer>();

		if (game.getPacmanNumberOfLivesRemaining() != Constants.NUM_LIVES) {
			this.getPowerPillsTargets(powerPills, game, targets);
		}

		this.getPillsTargets(pills, game, targets);

		if (game.getPacmanNumberOfLivesRemaining() == Constants.NUM_LIVES) {
			this.getPowerPillsTargets(powerPills, game, targets);
		}

		int[] targetsArray = new int[targets.size()]; // convert from ArrayList to array

		for (int i = 0; i < targetsArray.length; i++) {
			targetsArray[i] = targets.get(i);
		}

		if (game.getPacmanNumberOfLivesRemaining() == Constants.NUM_LIVES) {
			ArrayUtils.reverse(targetsArray);
		}

		// return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(current,
				game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH), DM.PATH);
	}

}
