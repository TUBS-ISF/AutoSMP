package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class ICPL extends ASPLCATSampling {

	public ICPL(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
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
