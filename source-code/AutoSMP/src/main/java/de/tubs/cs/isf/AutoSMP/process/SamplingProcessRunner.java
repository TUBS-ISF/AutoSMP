package de.tubs.cs.isf.AutoSMP.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import de.tubs.cs.isf.AutoSMP.algorithms.ASamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.logger.ErrStreamCollector;
import de.tubs.cs.isf.AutoSMP.logger.ErrStreamReader;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.logger.OutStreamReader;
import de.tubs.cs.isf.AutoSMP.logger.StreamRedirector;

/**
 * Controls the instantiation, execution and monitoring of the sampling process.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class SamplingProcessRunner {

	private long timeout = Long.MAX_VALUE;

	/**
	 * @return The timeout for the sampling process.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Executes a sampling process via a {@link Process}. Every kind of command can
	 * be used, and thus, all kind of programs are supported.
	 * 
	 * @param algorithm The algorithm to evaluate.
	 * @param result    Empty result object that should be filled.
	 */
	public SamplingResults run(ASamplingAlgorithm algorithm) {
		SamplingResults result = new SamplingResults();
		Logger.getInstance().logInfo("Running External Process", false);
//		List<String> command = new ArrayList<String>();
//		command.add("powershell.exe");
//		command.add("java");
//		command.add("-jar");
//		command.add("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/saob.jar");
//		ProcessBuilder pb = new ProcessBuilder(command);
////		pb.command("powershell.exe", "/c", "ping -n 3 google.com");
////		pb.command("bash.exe", "/c", "java -jar A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/saob.jar");
////		pb.command("git-bash.exe", "echo 'hello bash'");
////		pb.command("powershell.exe", "java --version","echo 'hello powershell'");
//		try {
//			Logger.getInstance().logInfo("OS Name: " + System.getProperty("os.name"), false);
//			Process process = pb.start();
//			// blocked :(
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line;
//			while ((line = reader.readLine()) != null) {
//				Logger.getInstance().logInfo(line, false);
//			}
//			int exitCode = process.waitFor();
//			System.out.println("\nExited with error code : " + exitCode);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		boolean terminatedInTime = false;
		long startTime = 0, endTime = 0;
		try {
			System.gc();
			algorithm.preProcess();

			Logger.getInstance().logInfo(algorithm.getCommand(), 1, false);

			final List<String> command = new ArrayList<>(); 
			if(System.getProperty("os.name").contains("Windows 10")) {
				Logger.getInstance().logInfo("OS Name: " + System.getProperty("os.name"), false);
				command.add("powershell.exe");
			}
			command.addAll(algorithm.getCommandElements());
//			command.add("java");
//			command.add("-da");
//			command.add("-Xmx4g");
//			command.add("-Xms2g");
//			command.add("-cp");
//			command.add("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/*");
//			command.add("-jar");
//			command.add("saob.jar");
//			command.add("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/saob.jar");

			if (!command.isEmpty()) {
				Logger.getInstance().logInfo(command.toString(), false);
				final ProcessBuilder processBuilder = new ProcessBuilder(command);
				Logger.getInstance().logInfo(processBuilder.command().toString(), false);
				Process process = null;

				final ErrStreamCollector errStreamCollector = new ErrStreamCollector();
				final StreamRedirector errRedirector = new StreamRedirector(
						Arrays.asList(new ErrStreamReader(), errStreamCollector));
				final StreamRedirector outRedirector = new StreamRedirector(
						Arrays.asList(new OutStreamReader(), algorithm));
				final Thread outThread = new Thread(outRedirector);
				final Thread errThread = new Thread(errRedirector);
				try {
					startTime = System.nanoTime();
					process = processBuilder.start();
					outRedirector.setInputStream(process.getInputStream());
					errRedirector.setInputStream(process.getErrorStream());
					outThread.start();
					errThread.start();

					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while((line = reader.readLine()) != null) {
						Logger.getInstance().logInfo(line, false);
					}
					terminatedInTime = process.waitFor(timeout, TimeUnit.MILLISECONDS);
					endTime = System.nanoTime();
					
					result.setTerminatedInTime(terminatedInTime);
					result.setNoErrorOccured(errStreamCollector.getErrList().isEmpty());
					result.setRuntime((endTime - startTime) / 1_000_000L);
				} finally {
					if (process != null) {
						process.destroyForcibly();
					}
				}
			} else {
				result.setTerminatedInTime(false);
				result.setNoErrorOccured(false);
				result.setRuntime(SamplingResults.INVALID_TIME);
			}
		} catch (Exception e) {
			Logger.getInstance().logError(e, true);
			result.setTerminatedInTime(false);
			result.setNoErrorOccured(false);
			result.setRuntime(SamplingResults.INVALID_TIME);
		}
		try {
			setResult(algorithm, result);
		} catch (Exception e) {
			Logger.getInstance().logError(e, true);
			if (terminatedInTime) {
				result.setNoErrorOccured(false);
			}
		}
		try {
			algorithm.postProcess();
		} catch (Exception e) {
			Logger.getInstance().logError(e);
		}
		return result;
	}

	/**
	 * Retrieves the results from the algorithm and saves them in the results data
	 * structure.
	 * 
	 * @param algorithm The evaluated algorithm.
	 * @param result    The results structure waiting to be filled.
	 * @throws IOException
	 */
	protected void setResult(ASamplingAlgorithm algorithm, SamplingResults result) throws IOException {
		result.setComputedSample(algorithm.parseResults());
		result.setMemoryResults(algorithm.parseMemory());
	}

	/**
	 * Sets the timeout for the sampling process.
	 * 
	 * @param timeout Timeout to set.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
