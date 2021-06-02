package de.tubs.cs.isf.AutoSMP.modules;

import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.Sample;
import de.tubs.cs.isf.AutoSMP.util.PrefixChecker;

public class StabilityCalculatorModule {

	private final SamplingConfig config;
	/**
	 * Saves samples for the current system. It contains all samples from one model
	 * and for all system iteration. Each system iteration represents one list
	 */
	private Sample[][] curentSystemSamples = null;

	/**
	 * Saves samples for the last system. It contains all samples from one model and
	 * for all system iteration. Each system iteration represents one list
	 */
	private Sample[][] previousSystemSamples = null;
	private final AutoSMP sampler;

	public StabilityCalculatorModule(AutoSMP sampler) {
		this.sampler = sampler;
		this.config = sampler.getConfig();
	}

	/**
	 * Caches a sample at the cell for the given information. Note: Sample is only
	 * cached when stability calculation is activated.
	 * 
	 * @param systemIteration The current system iteration.
	 * @param algorithmIndex  The index of the current algorithm.
	 * @param sample          The sample to cache.
	 */
	public void cacheCurrentSample(int systemIteration, int algorithmIndex, Sample sample) {
		if (config.calculateStability.getValue().toLowerCase().equals("true")) {
			curentSystemSamples[systemIteration][algorithmIndex] = sample;
		}
	}

	/**
	 * Retrieves a cached sample of the current iteration at the cell for the given
	 * information. Note: Sample can only be retrieved when stability calculation is
	 * activated.
	 * 
	 * @param systemIteration The current system iteration.
	 * @param algorithmIndex  The index of the current algorithm.
	 * @param sample          The sample to cache.
	 */
	public Sample getCurrentCachedSample(int systemIteration, int algorithmIndex) {
		if (config.calculateStability.getValue().toLowerCase().equals("true")) {
			return curentSystemSamples[systemIteration][algorithmIndex];
		}
		return null;
	}

	/**
	 * Retrieves a cached sample of the previous iteration at the cell for the given
	 * information. Note: Sample can only be retrieved when stability calculation is
	 * activated.
	 * 
	 * @param systemIteration The current system iteration.
	 * @param algorithmIndex  The index of the current algorithm.
	 * @param sample          The sample to cache.
	 */
	public Sample getPreviousCachedSample(int systemIteration, int algorithmIndex) {
		if (config.calculateStability.getValue().toLowerCase().equals("true")) {
			return previousSystemSamples[systemIteration][algorithmIndex];
		}
		return null;
	}

	public void init() {
		if (!config.calculateStability.getValue().toLowerCase().equals("true")
				&& !config.calculateStability.getValue().toLowerCase().equals("false")) {
			// If not true or false decide automatically
			config.calculateStability
					.setValue("" + (PrefixChecker.getLongestCommonPrefix(config.systemNames).length() > 5
							&& config.systemNames.size() > 1));
		}
	}

	/**
	 * Prepares the stability data for the current system. Note: Sample is only
	 * prepared when stability calculation is activated.
	 * 
	 * @param systemIteration    Iteration of the system.
	 * @param numberOfAlgorithms The number of algorithm that are evaluated in this
	 *                           system iteration.
	 */
	public void prepareSystem(int systemIteration, int numberOfAlgorithms) {
		// Create temp lists when stability is going to be calculated
		if (config.calculateStability.getValue().toLowerCase().equals("true")) {
			if (systemIteration == 0) {
				// Each list represents one system iteration, and, thus contains all samples of
				// all algorithms
				curentSystemSamples = new Sample[config.systemIterations.getValue()][numberOfAlgorithms];
				// Each list represents one system iteration, and, thus contains all samples of
				// all algorithms
				previousSystemSamples = new Sample[config.systemIterations.getValue()][numberOfAlgorithms];
			} else {
				previousSystemSamples = curentSystemSamples;
				curentSystemSamples = new Sample[config.systemIterations.getValue()][numberOfAlgorithms];
			}
		}
	}
}
