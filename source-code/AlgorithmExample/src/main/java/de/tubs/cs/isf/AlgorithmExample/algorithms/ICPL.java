package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class ICPL extends ASPLCATSampling {

	public ICPL(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("ICPL");
	}

	@Override
	public String getName() {
		return "ICPL";
	}

}
