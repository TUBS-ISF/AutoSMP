package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.io.IOException;
import java.nio.file.Path;

import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.tubs.cs.isf.AutoSMP.algorithms.AJavaMemoryTWiseSamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

public class saobAdapter extends AJavaMemoryTWiseSamplingAlgorithm{

	public saobAdapter(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
//		addCommandElement("echo 'hello saob interface'");
//		addCommandElement("-cp");
//		addCommandElement("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/");
		addCommandElement("-jar");
		addCommandElement("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/saob.jar");
//		Logger.getInstance().logInfo("SAOB Adapter Started", false);
	}
	
	@Override
	public String getName() {
		return "SAOB";
	}

	@Override
	public String getParameterSettings() {
		return "t"+t;
	}

	@Override
	public SolutionList parseResults() throws IOException {
		return null;
	}

}
