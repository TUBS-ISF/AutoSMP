package de.tubs.cs.isf.AutoSMP.algorithms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.tubs.cs.isf.AutoSMP.eval.memory.GarbageCollectorLogAnalyzer;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.process.SamplingMemoryResults;

public abstract class AJavaMemoryTWiseSamplingAlgorithm extends ASamplingAlgorithm {

	protected final Path gcCollectorPath;
	protected final String maximumMemoryAllocation;
	protected final String minimumMemoryAllocation;

	public AJavaMemoryTWiseSamplingAlgorithm(Path algoPath, Path fmFile, Path outputFile, int t, int maxSize, int randomSeed, Path gcCollectorPath,
			String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(algoPath, fmFile, outputFile, t, maxSize, randomSeed);
		this.gcCollectorPath = gcCollectorPath;
		this.minimumMemoryAllocation = minimumMemoryAllocation;
		this.maximumMemoryAllocation = maximumMemoryAllocation;
	}

	/**
	 * At this point the user must configure the command to invoke their sampling
	 * algorithm.<br>
	 * <br>
	 * 
	 * <b>Note: The user commands are added to a pre defined set of commands. The
	 * pre defined set is:</b><br>
	 * <br>
	 * 
	 * <i>java (GC commands)</i><br>
	 * <br>
	 * 
	 * The final command will have the following syntax:<br>
	 * <br>
	 * 
	 * <i>java (GC commands) (user commands)</i><br>
	 * <br>
	 * 
	 * Example:<br>
	 * <br>
	 * 
	 * <i>java -da -Xloggc:logICPL.txt -XX:+PrintGCDetails -XX:+PrintGCDateStamps
	 * -Xmx12g -Xms2g -cp ".;..;../lib/*" no.sintef.ict.splcatool.SPLCATool -t
	 * t_wise -fm model.dimacs -s 2 -o sampleICPL.csv -a</i><br>
	 * <br>
	 * 
	 * where:<br>
	 * <br>
	 * 
	 * <b><i>java (GC commands)</i></b> == <i>java -da -Xloggc:logICPL.txt
	 * -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx12g -Xms2g</i><br>
	 * <b><i>(user commands)</i></b> == <i>-cp ".;..;../lib/*"
	 * no.sintef.ict.splcatool.SPLCATool -t t_wise -fm model.dimacs -s 2 -o
	 * sampleICPL.csv -a</i>
	 */
	@Override
	protected void addCommandElements() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null)) {
			return false;
		}
		final ASamplingAlgorithm other = (ASamplingAlgorithm) obj;
		return Objects.equals(this.getFullName(), other.getFullName());
	}

	/**
	 * Path to the garbage collector file.
	 */
	public final Path getPathOfGarbageCollectorFile() {
		return gcCollectorPath;
	}

	@Override
	public final SamplingMemoryResults parseMemory() throws IOException {
		GarbageCollectorLogAnalyzer analyzer = new GarbageCollectorLogAnalyzer(getPathOfGarbageCollectorFile());
		return analyzer.processGCResults();
	}

	@Override
	public final void postProcess() throws Exception {
		super.postProcess();
		try {
			Files.deleteIfExists(getPathOfGarbageCollectorFile());
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
	}

	@Override
	public final void preProcess() throws Exception {
		System.out.println("Start preprocessing algorithm");
		commandElements.clear();
		addCommandElement("java");
		addCommandElement("-da");
		addCommandElement("-" + this.maximumMemoryAllocation);
		addCommandElement("-" + this.minimumMemoryAllocation);

//		String version = Runtime.class.getPackage().getSpecificationVersion();
//		if (version.startsWith("11")) {
//			addCommandElement("-Xlog:gc:" + getPathOfGarbageCollectorFile());
//		} else {
//			addCommandElement("-Xloggc:" + getPathOfGarbageCollectorFile());
//			addCommandElement("-XX:+PrintGCDateStamps");
//		}
		addCommandElements();
	}
}
