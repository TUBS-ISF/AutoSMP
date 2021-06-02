package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class Chvatal extends ASPLCATSampling {

	public Chvatal(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("Chvatal");
	}

	@Override
	public String getName() {
		return "Chvatal";
	}

}
