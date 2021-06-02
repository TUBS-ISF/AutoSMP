package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class Random extends AFIDESampling {

	public Random(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("Random");
		addCommandElement("-l");
		addCommandElement(Integer.toString(100));
	}

	@Override
	public String getName() {
		return "Random";
	}

}
