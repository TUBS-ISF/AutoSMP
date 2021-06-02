package de.tubs.cs.isf.AutoSMP.algorithms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.tubs.cs.isf.AutoSMP.logger.IOutputReader;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.process.SamplingMemoryResults;

public abstract class ASamplingAlgorithm implements IOutputReader {

	protected final ArrayList<String> commandElements = new ArrayList<>();

	protected final Path fmFile;
	protected int iterations = -1;
	protected final Path outputFile;
	protected final int t;

	public ASamplingAlgorithm(Path fmFile, Path outputFile, int t) {
		this.fmFile = fmFile;
		this.outputFile = outputFile;
		this.t = t;
	}

	/**
	 * Adds a new command element to the list of commands.
	 * 
	 * @param parameter Element to add.
	 */
	public void addCommandElement(String parameter) {
		commandElements.add(parameter);
	}

	/**
	 * At this point the user must configure the command to invoke their sampling
	 * algorithm. Command arguments can be added via
	 * {@link #addCommandElement(String)}.
	 */
	protected abstract void addCommandElements() throws Exception;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final ASamplingAlgorithm other = (ASamplingAlgorithm) obj;
		return Objects.equals(getFullName(), other.getFullName());
	}

	/**
	 * @return The command that is used to start your sampling algorithm. Is
	 *         composed of all {@link #commandElements} elements.
	 */
	public String getCommand() {
		StringBuilder commandBuilder = new StringBuilder();
		for (String commandElement : commandElements) {
			commandBuilder.append(commandElement);
			commandBuilder.append(' ');
		}
		return commandBuilder.toString();
	}

	/**
	 * @return A read-only list of all commands.
	 */
	public List<String> getCommandElements() {
		return Collections.unmodifiableList(commandElements);
	}

	/**
	 * @return A distinctive identifier to replicate your sampling process.
	 */
	public String getFullName() {
		return getName() + "_" + getParameterSettings();
	}

	/**
	 * @return The current iteration.
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @return The name of the algorithm.
	 */
	public abstract String getName();

	/**
	 * @return A string to identify the parameters of your algorithm. <br>
	 *         For example, 't2_m1' with <br>
	 *         <b>t2</b> = identifier for the aimed coverage <br>
	 *         <b>m1</b> = and argument specific for YASA that changes the sampling
	 *         process.
	 */
	public abstract String getParameterSettings();

	/**
	 * Path for the model file. The model file will be a <i>DIMACS</i> model. This
	 * model should be sampled.
	 */
	public final Path getPathOfModelFile() {
		return fmFile;
	}

	/**
	 * The path for the output file. Keep in mind that the output file will be
	 * removed by {@link #postProcess()} after the process is finished.
	 */
	public final Path getPathOfOutputFile() {
		return outputFile;
	}

	/**
	 * Defines the t-value for the t-coverage.
	 */
	public final int getT() {
		return t;
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

	/**
	 * This method is used to compute the memory statistics for you sampling
	 * process.
	 * 
	 * @return All require memory statistics.
	 * @throws IOException
	 * @see SamplingMemoryResults#setStatisticCreatedBytesTotal(long)
	 * @see SamplingMemoryResults#setStatisticPauseTimeAvg(double)
	 * @see SamplingMemoryResults#setStatisticPauseTimeTotal(double)
	 * @see SamplingMemoryResults#setStatisticThroughput(double)
	 */
	public abstract SamplingMemoryResults parseMemory() throws IOException;

	/**
	 * This method is used to transform the sample computed by the sampling
	 * algorithm into the data structure used by our sampling framework. This method
	 * is called after the sampling process is finished.
	 * 
	 * @return The computed sample as {@link SolutionList}
	 * @throws IOException
	 */
	public abstract SolutionList parseResults() throws IOException;

	/**
	 * The post process is executed as the last method for an algorithm instance. It
	 * is supposed to clean all temporary or intermediate files.
	 * 
	 * @throws Exception
	 */
	public void postProcess() throws Exception {
		try {
			Files.deleteIfExists(getPathOfOutputFile());
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
	}

	/**
	 * The pre-process is executed at the first method for an algorithm instance. It
	 * is supposed to prepare the process and the command to start the algorithm.
	 * 
	 * @throws Exception
	 */
	public void preProcess() throws Exception {
		commandElements.clear();
		addCommandElements();
	}

	@Override
	public void readOutput(String line) throws Exception {
	}

	/**
	 * Markes the number of the currently iteration of the algorithm.
	 * 
	 * @param iterations The current iteration.
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public String toString() {
		return getFullName();
	}

}
