package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.ovgu.featureide.fm.core.io.csv.ConfigurationListFormat;
import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.tubs.cs.isf.AutoSMP.algorithms.AJavaMemoryTWiseSamplingAlgorithm;

public abstract class AFIDESampling extends AJavaMemoryTWiseSamplingAlgorithm {

	public AFIDESampling(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}
	
	@Override
	protected void addCommandElements() {
		addCommandElement("-cp");
		addCommandElement("algorithms/tools/FIDE/*");
		addCommandElement("de.ovgu.featureide.fm.core.cli.FeatureIDECLI");
		addCommandElement("genconfig");
		addCommandElement("-t");
		addCommandElement(Integer.toString(t));
		addCommandElement("-o");
		addCommandElement(getPathOfOutputFile().toString());
		addCommandElement("-fm");
		addCommandElement(getPathOfModelFile().toString());
		addCommandElement("-s");
		addCommandElement("100");
	}
	
	@Override
	public String getParameterSettings() {
		return "t" + t;
	}

	@Override
	public SolutionList parseResults() {
		SolutionList configurationList = new SolutionList();
		SimpleFileHandler.load(outputFile, configurationList, new ConfigurationListFormat());
		return configurationList;
	}
}
