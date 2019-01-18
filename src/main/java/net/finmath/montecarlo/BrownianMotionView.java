/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 16 Jan 2015
 */

package net.finmath.montecarlo;

import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * A Brownian motion which is defined by some factors of a given Brownian motion,
 * i.e., for a given multi-factorial Brownian motion W, this Brownian motion is
 * given by ( W(i[0]), W(i[1]) W(i[2]), ..., W(i[n-1]) )
 * where i is a given array of integers.
 *
 * You may use this class to change the number of factors and/or the order of
 * a given Brownian motion.
 *
 * You may use this, to create a link between two models, where each model requires
 * an individual object of type {@link BrownianMotion}.
 *
 * @author Christian Fries
 * @version 1.0
 */
public class BrownianMotionView implements BrownianMotion {

	private BrownianMotion		brownianMotion;
	private Integer[]					factors;

	/**
	 * Create a sub-view on a Brownian motion. The result is an object
	 * implementing {@link BrownianMotion}, i.e. a Brownian motion,
	 * which maps factor indices to possilby other factors of the given Brownian motion.
	 *
	 * You may use this class to change the number of factors and/or the order.
	 *
	 * @param brownianMotion A given Brownian motion.
	 * @param factors A map of indices i &rarr; j for i = 0,1,2,3,... given as an array of j's
	 */
	public BrownianMotionView(BrownianMotion brownianMotion, Integer[] factors) {
		super();
		this.brownianMotion = brownianMotion;
		this.factors = factors;
	}

	@Override
	public RandomVariable getBrownianIncrement(int timeIndex, int factor) {
		return brownianMotion.getBrownianIncrement(timeIndex, factors[factor]);
	}

	@Override
	public TimeDiscretizationInterface getTimeDiscretization() {
		return brownianMotion.getTimeDiscretization();
	}

	@Override
	public int getNumberOfFactors() {
		return factors.length;
	}

	@Override
	public int getNumberOfPaths() {
		return brownianMotion.getNumberOfPaths();
	}

	@Override
	public RandomVariable getRandomVariableForConstant(double value) {
		return brownianMotion.getRandomVariableForConstant(value);
	}

	@Override
	public BrownianMotion getCloneWithModifiedSeed(int seed) {
		return new BrownianMotionView(brownianMotion.getCloneWithModifiedSeed(seed), factors);
	}

	@Override
	public BrownianMotion getCloneWithModifiedTimeDiscretization(TimeDiscretizationInterface newTimeDiscretization) {
		return new BrownianMotionView(brownianMotion.getCloneWithModifiedTimeDiscretization(newTimeDiscretization), factors);
	}

	@Override
	public RandomVariable getIncrement(int timeIndex, int factor) {
		return getBrownianIncrement(timeIndex, factor);
	}
}
