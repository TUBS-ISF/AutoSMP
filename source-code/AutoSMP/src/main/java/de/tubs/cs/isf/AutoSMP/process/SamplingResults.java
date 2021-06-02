package de.tubs.cs.isf.AutoSMP.process;

import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;

/**
 * Data class storing the results for one evaluation run.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class SamplingResults {

	public static long INVALID_TIME = -1;

	private SamplingMemoryResults memoryResults = new SamplingMemoryResults();

	private SolutionList resultComputedSample = null;

	private boolean resultNoErrorOccured = false;

	private long resultRuntime = INVALID_TIME;
	private boolean resultTerminatedInTime = false;

	/**
	 * Gets the memory results for this run.
	 * 
	 * @return Memory Results
	 */
	public SamplingMemoryResults getMemoryResults() {
		return memoryResults;
	}

	/**
	 * @return The computed sample of the run.
	 */
	public SolutionList getResult() {
		return resultComputedSample;
	}

	/**
	 * @return The elapsed time of the sampling algorithm.
	 */
	public long getSamplingTime() {
		return resultRuntime;
	}

	/**
	 * @return {@link Boolean#TRUE} when no error occurred in the sampling process,
	 *         otherwise {@link Boolean#FALSE}.
	 */
	public boolean isNoErrorOccured() {
		return resultNoErrorOccured;
	}

	/**
	 * @return {@link Boolean#TRUE} if sampling process terminated in time,
	 *         otherwise {@link Boolean#FALSE}.
	 */
	public boolean isTerminatedInTime() {
		return resultTerminatedInTime;
	}

	/**
	 * Sets the sample computed by the sampling process.
	 * 
	 * @param result The computed sample.
	 */
	public void setComputedSample(SolutionList result) {
		this.resultComputedSample = result;
	}

	/**
	 * Sets the memory results for this run.
	 * 
	 * @param memoryResults Memory results to set.
	 */
	public void setMemoryResults(SamplingMemoryResults memoryResults) {
		this.memoryResults = memoryResults;
	}

	/**
	 * Sets whether an error occurred during the sampling process.
	 * 
	 * @param noError Indicator.
	 */
	public void setNoErrorOccured(boolean noError) {
		this.resultNoErrorOccured = noError;
	}

	/**
	 * Sets the runtime needed by the sampling process.
	 * 
	 * @param time The runtime.
	 */
	public void setRuntime(long time) {
		this.resultRuntime = time;
	}

	/**
	 * Sets whether the sampling process terminated in time.
	 * 
	 * @param terminatedInTime Indicator.
	 */
	public void setTerminatedInTime(boolean terminatedInTime) {
		this.resultTerminatedInTime = terminatedInTime;
	}

}
