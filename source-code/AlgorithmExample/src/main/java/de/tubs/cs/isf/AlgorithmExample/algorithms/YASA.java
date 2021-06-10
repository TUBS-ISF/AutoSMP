package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class YASA extends AFIDESampling {

	public YASA(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}	

	@Override
	protected void addCommandElements() {
		super.addCommandElements();
		addCommandElement("-a");
		addCommandElement("YASA");
		addCommandElement("-m");
		addCommandElement("1");
	}

	@Override
	public String getName() {
		return "YASA";
	}

	@Override
	public String getParameterSettings() {
		return "t" + t + "_m" + 1;
	}

}