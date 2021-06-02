package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.nio.file.Path;

public class IncLing extends AFIDESampling {

	public IncLing(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
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
