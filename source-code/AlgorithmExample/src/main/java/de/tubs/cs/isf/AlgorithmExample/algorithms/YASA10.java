package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class YASA10 extends AFIDESampling {

	public YASA10(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}	

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("YASA");
		addCommandElement("-m");
		addCommandElement("10");
	}

	@Override
	public String getName() {
		return "YASA";
	}

	@Override
	public String getParameterSettings() {
		return "t" + t + "_m" + 10;
	}

}
