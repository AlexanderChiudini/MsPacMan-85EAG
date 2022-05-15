package pacman.controllers;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 *
 * The objective is to eat all the pills without being reached by the ghosts.
 * This controller prioritize eat the pills to maximize level clear
 *
 */
public final class MaximizeLevelPacmanController extends MyPacmanController {

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
//					int[] targetsArray = this.getTargetsWithEdibleGhost(game, ghost);
//					MOVE moveTowards = game.getNextMoveTowardsTarget(current,
//							game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH), DM.PATH);
//					
//					MOVE moveAway = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
//							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
//					
//					return (moveTowards.ordinal() > moveAway.ordinal()) ? moveAway : moveTowards;
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				}
			}
		}
		return null;
	}

	/**
	 * Goes to the closest, edible ghost or pill
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
			int[] targetsArray = this.getTargetsWithEdibleGhost(game, minGhost);
			
			return game.getNextMoveTowardsTarget(current,
					game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH), DM.PATH);
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

	/**
	 * create as array of indexes whit the pills and the closest edible ghost
	 * @param game	the Game instance
	 * @param minGhost	the nearest Ghost
	 * @return targets array with the possible target indexes
	 */
	private int[] getTargetsWithEdibleGhost(Game game, GHOST minGhost) {
		ArrayList<Integer> targets = new ArrayList<Integer>();
		if (minGhost != null) {
			targets.add(game.getGhostCurrentNodeIndex(minGhost));
		}
		int[] pills = game.getPillIndices();
		int[] powerPills = game.getPowerPillIndices();
		this.getPowerPillsTargets(powerPills, game, targets);
		this.getPillsTargets(pills, game, targets);
		int[] targetsArray = new int[targets.size()]; // convert from ArrayList to array

		for (int i = 0; i < targetsArray.length; i++) {
			targetsArray[i] = targets.get(i);
		}
		
		return targetsArray;
	}
	
}
