package reinforcement;

import mdp.*;

public class ValueIteration {

	private MarkovDecisionProblem mdp;
	private Action[] policy;
	private double[] values;
	private double[] probs;
	private double discount = 1.0;

	/**
	 * Creates a new instance of the value iteration algorithm for a specified mdp
	 * 
	 * @param mdp
	 */
	public ValueIteration(MarkovDecisionProblem mdp) {

		this.mdp = mdp;
		int len = mdp.getWidth() * mdp.getHeight();
		this.policy = new Action[len];
		this.values = new double[len];
		this.probs = this.mdp.getProbs();

		// Initialize all values to 1 (arbitrary value)
		for (int i = 0; i < len; i++) {
			this.values[i] = 1.0;
		}

		this.values = iterate(500, values);
		determinePolicy();
	}

	/**
	 * Recursively performs the value iteration algorithm
	 * 
	 * @param iter   : the amount of iterations the VIA has to run
	 * @param values : the current values of the MDP
	 * @return the (approximate) values for each position in the mdp
	 */
	private double[] iterate(int iter, double[] values) {
		if (iter == 0) {
			return values;
		} else {
			double[] newValues = new double[values.length];
			for (int pos = 0; pos < values.length; pos++) {
				double bestVal = -100.0;
				for (Action a : Action.values()) {
					double actionVal = computeValue(values, pos, a);
					if (actionVal > bestVal)
						bestVal = actionVal;
				}
				newValues[pos] = bestVal;
			}
			return iterate(iter - 1, newValues);
		}
	}

	/**
	 * Determine a policy for the mdp based on the values array
	 */
	private void determinePolicy() {
		for (int pos = 0; pos < this.policy.length; pos++) {
			Field f = this.mdp.getField(pos % this.mdp.getWidth(), pos / this.mdp.getWidth());

			// Obstacles and terminating states don't need a policy
			if (f == Field.OBSTACLE || f == Field.NEGREWARD || f == Field.REWARD)
				this.policy[pos] = null;

			// Compute the best action by comparing the expected value of each action in
			// state pos
			else {
				Action bestAction = null;
				double bestVal = -100.0;
				for (Action a : Action.values()) {
					double v = computeValue(this.values, pos, a);
					if (v > bestVal) {
						bestVal = v;
						bestAction = a;
					}
				}
				this.policy[pos] = bestAction;
			}
		}
	}

	/**
	 * Computes a candidate value of a position in the MDP based on action a
	 * 
	 * @param values
	 * @param pos
	 * @return
	 */
	private double computeValue(double[] values, int pos, Action a) {
		int xPos = pos % this.mdp.getWidth();
		int yPos = pos / this.mdp.getWidth();
		Field currentField = this.mdp.getField(xPos, yPos);

		// Obstacles have no value
		if (currentField == Field.OBSTACLE)
			return 0;

		// Positive and negative reward fields result in termination of the MDP and thus
		// are updated with their reward
		else if (currentField == Field.REWARD || currentField == Field.NEGREWARD)
			return this.mdp.getReward(xPos, yPos);

		// Compute the relevant Bellman equation for action a
		else {
			double actionVal = this.mdp.getReward(xPos, yPos);
			double sum = 0.0;

			// Do Action a
			sum += isPossible(a, xPos, yPos)
					? probs[0] * values[(yPos + actionDY(a)) * this.mdp.getWidth() + (xPos + actionDX(a))]
					: 0.0;
			if (!this.mdp.isDeterministic()) {

				// Perform a left sidestep
				sum += (isPossible(Action.previousAction(a), xPos, yPos))
						? 0.5 * probs[1]
								* values[(yPos + actionDY(Action.previousAction(a))) * this.mdp.getWidth()
										+ (xPos + actionDX(Action.previousAction(a)))]
						: 0.0;

				// Perform a right sidestep
				sum += (isPossible(Action.nextAction(a), xPos, yPos))
						? 0.5 * probs[1]
								* values[(yPos + actionDY(Action.nextAction(a))) * this.mdp.getWidth()
										+ (xPos + actionDX(Action.nextAction(a)))]
						: 0.0;

				// Perform a backstep
				sum += (isPossible(Action.backAction(a), xPos, yPos))
						? probs[2] * values[(yPos + actionDY(Action.backAction(a))) * this.mdp.getWidth()
								+ (xPos + actionDX(Action.backAction(a)))]
						: 0.0;

				// Do nothing
				sum += probs[3] * values[pos];
				sum = sum * discount;
				actionVal += sum;
			}
			return actionVal;
		}
	}

	/**
	 * Checks whether Action a is possible in position (xPos, yPos)
	 * 
	 * @param a
	 * @param xPos
	 * @param yPos
	 * @return possibility of action
	 */
	private boolean isPossible(Action a, int xPos, int yPos) {
		switch (a) {
		case UP:
			if ((yPos + 1) >= 0 && (yPos + 1) < this.mdp.getHeight())
				return this.mdp.getField(xPos, yPos + 1) != Field.OBSTACLE;
			else
				return false;
		case DOWN:
			if ((yPos - 1) >= 0 && (yPos - 1) < this.mdp.getHeight())
				return this.mdp.getField(xPos, yPos - 1) != Field.OBSTACLE;
			else
				return false;
		case LEFT:
			if ((xPos - 1) >= 0 && (xPos - 1) < this.mdp.getWidth())
				return this.mdp.getField(xPos - 1, yPos) != Field.OBSTACLE;
			else
				return false;
		case RIGHT:
			if ((xPos + 1) >= 0 && (xPos + 1) < this.mdp.getWidth())
				return this.mdp.getField(xPos + 1, yPos) != Field.OBSTACLE;
			else
				return false;
		}
		return false;
	}

	/**
	 * Compute the change in x coordinate if action a is performed
	 * 
	 * @param a
	 * @return delta x
	 */
	private int actionDX(Action a) {
		switch (a) {
		case UP:
		case DOWN:
			return 0;
		case LEFT:
			return -1;
		case RIGHT:
			return 1;
		}
		return 0;
	}

	/**
	 * Compute change in y coordinate if action a is performed
	 * 
	 * @param a
	 * @return delta y
	 */
	private int actionDY(Action a) {
		switch (a) {
		case UP:
			return 1;
		case DOWN:
			return -1;
		case LEFT:
		case RIGHT:
			return 0;
		}
		return 0;
	}

	/**
	 * Consult the policy to get the best action in state (xPos, yPos)
	 * 
	 * @param xPos
	 * @param yPos
	 * @return policy action
	 */
	public Action getPolicy(int xPos, int yPos) {
		return this.policy[xPos + yPos * this.mdp.getWidth()];
	}
}
