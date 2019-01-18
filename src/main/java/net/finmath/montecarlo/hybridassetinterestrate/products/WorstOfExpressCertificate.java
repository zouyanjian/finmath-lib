/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 04.04.2015
 */

package net.finmath.montecarlo.hybridassetinterestrate.products;

import net.finmath.exception.CalculationException;
import net.finmath.modelling.ModelInterface;
import net.finmath.modelling.ProductInterface;
import net.finmath.montecarlo.hybridassetinterestrate.HybridAssetLIBORModelMonteCarloSimulationInterface;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;

/**
 * @author Christian Fries
 * @version 1.0
 */
public class WorstOfExpressCertificate implements ProductInterface {

	final double maturity;
	final double[] strikeLevels;
	final double[] exerciseDates;
	final double[] triggerPerformanceLevel;
	final double[] redemption;
	final double redemptionFinal;

	public WorstOfExpressCertificate(double maturity, double[] baseLevels,
			double[] exerciseDates, double[] triggerLevels,
			double[] redemption, double redemptionFinal) {
		super();
		this.maturity = maturity;
		this.strikeLevels = baseLevels;
		this.exerciseDates = exerciseDates;
		this.triggerPerformanceLevel = triggerLevels;
		this.redemption = redemption;
		this.redemptionFinal = redemptionFinal;
	}

	/* (non-Javadoc)
	 * @see net.finmath.modelling.ProductInterface#getValue(double, net.finmath.modelling.ModelInterface)
	 */
	@Override
	public Object getValue(double evaluationTime, ModelInterface model) {
		return null;
	}

	public double getValue(double evaluationTime, HybridAssetLIBORModelMonteCarloSimulationInterface model) throws CalculationException {

		RandomVariable zero				= model.getRandomVariableForConstant(0.0);
		RandomVariable values				= model.getRandomVariableForConstant(0.0);
		RandomVariable exerciseIndicator	= model.getRandomVariableForConstant(1.0);

		for(int triggerIndex=0; triggerIndex<exerciseDates.length; triggerIndex++) {

			// get worst performance
			RandomVariable worstPerformance = getWorstPerformance(model, exerciseDates[triggerIndex], strikeLevels);

			// exercise if worstPerformance >= triggerPerformanceLevel[triggerIndex]
			RandomVariable trigger = worstPerformance.sub(triggerPerformanceLevel[triggerIndex]);

			RandomVariable payment = exerciseIndicator.mult(redemption[triggerIndex]);
			payment = payment.div(model.getNumeraire(exerciseDates[triggerIndex]));

			// if trigger >= 0 we have a payment and set the exerciseIndicator to 0.
			values = values.add(trigger.choose(payment, new Scalar(0.0)));
			exerciseIndicator = trigger.choose(zero, exerciseIndicator);
		}

		/*
		 * final redemption
		 */

		RandomVariable worstPerformance = getWorstPerformance(model, maturity, strikeLevels);
		RandomVariable payment = exerciseIndicator.mult(worstPerformance.mult(redemptionFinal));

		payment = payment.div(model.getNumeraire(maturity));
		values = values.add(payment);

		/*
		 * numeraire at evaluationTime
		 */
		values = values.mult(model.getNumeraire(evaluationTime));
		return values.getAverage();
	}

	/**
	 * @param model
	 * @param exerciseDate
	 * @param baseLevels
	 * @return
	 * @throws CalculationException
	 */
	private static RandomVariable getWorstPerformance(HybridAssetLIBORModelMonteCarloSimulationInterface model, double exerciseDate, double[] baseLevels) throws CalculationException {
		RandomVariable worstPerformance = null;
		for(int assetIndex=0; assetIndex<baseLevels.length; assetIndex++) {
			RandomVariable underlying = model.getAssetValue(exerciseDate, assetIndex);
			RandomVariable performance = underlying.div(baseLevels[assetIndex]);
			worstPerformance = worstPerformance != null ? worstPerformance.cap(performance) : performance;
		}

		return worstPerformance;
	}
}
