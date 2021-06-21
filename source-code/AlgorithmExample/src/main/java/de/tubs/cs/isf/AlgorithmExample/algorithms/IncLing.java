package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class IncLing extends AFIDESampling {

	public IncLing(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("Incling");
	}

	@Override
	public String getName() {
		return "Incling";
	}

}
