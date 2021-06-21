package de.tubs.cs.isf.samplingAlgorithms.algorithms;

import java.util.Random;

import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.PairWiseConfigurationGenerator;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
import de.tubs.cs.isf.samplingAlgorithms.util.logger.Logger;

public class IncLing extends ASamplingAlgorithm{
	
	private final String ALGO = "IncLing"; 
	/**
	 * 
	 * @param args
	 * [0] Path to feature model
	 * [1] Path to store sample
	 * [2] Int Coverage
	 * [3] Int max sample size
	 * [4] Random seed
	 */
	
	public static void main(String args[]) {
		Logger.getInstance().logInfo("Starting IncLing Sampling", false);
		
		IncLing chvatalSampling = new IncLing();
		chvatalSampling.init(args[0],args[1],args[2],args[3],args[4]);
		chvatalSampling.loadFeatureFM();
		chvatalSampling.runSampling();
		chvatalSampling.writeSample();
		
		Logger.getInstance().logInfo("finished IncLing Sampling", false);
	}
	
	public IncLing(){
		super();
	}
	
	@Override
	public void runSampling() {
		Logger.getInstance().logInfo("Running Sampling", false);
		PairWiseConfigurationGenerator pcg = new PairWiseConfigurationGenerator(randomCNF, maxSampleSize);
		this.sample = LongRunningWrapper.runMethod(pcg);
	}

	@Override
	public void init(String in, String out, String coverage, String maxSize, String rSeed) {
		this.INPUTDIR = in; 
		this.OUTPUTDIR = out; 
		this.coverage = Integer.parseInt(coverage);
		this.maxSampleSize = Integer.parseInt(maxSize);
		this.randomSeed = new Random(Integer.parseInt(rSeed));
	}

}
