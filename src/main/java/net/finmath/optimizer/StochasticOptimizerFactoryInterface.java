/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 29.05.2015
 */

package net.finmath.optimizer;

import net.finmath.optimizer.StochasticOptimizerInterface.ObjectiveFunction;
import net.finmath.stochastic.RandomVariable;

/**
 * @author Christian Fries
 *
 * @version 1.0
 */
public interface StochasticOptimizerFactoryInterface {

	default StochasticOptimizerInterface getOptimizer(ObjectiveFunction objectiveFunction, RandomVariable[] initialParameters, RandomVariable[] targetValues) {
		return getOptimizer(objectiveFunction, initialParameters, null, null, null, targetValues);
	}

	default StochasticOptimizerInterface getOptimizer(ObjectiveFunction objectiveFunction, RandomVariable[] initialParameters, RandomVariable[] lowerBound, RandomVariable[] upperBound, RandomVariable[] targetValues) {
		return getOptimizer(objectiveFunction, initialParameters, lowerBound, upperBound, null, targetValues);
	}

	StochasticOptimizerInterface getOptimizer(ObjectiveFunction objectiveFunction, RandomVariable[] initialParameters, RandomVariable[] lowerBound, RandomVariable[] upperBound, RandomVariable[] parameterStep, RandomVariable[] targetValues);

}
