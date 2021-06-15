package de.tubs.cs.isf.samplingAlgorithms.util.logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * The logger is responsible to provide an easy way to print information to the
 * standard output. The logger is implemented as Singleton and the instance can
 * be accessed via {@link Logger#getInstance()}.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class Logger {

	/** Singleton instance of the logger. */
	private static final Logger INSTANCE = new Logger();

	/**
	 * @return A timestamp used for loggin in the following format
	 *         <code>"MM/dd/yyyy-HH:mm:ss"</code>.
	 */
	public static final String getCurTime() {
		return new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
	}

	/** Retrieves the current instance of the logger instance. */
	public static final Logger getInstance() {
		return INSTANCE;
	}

	/** Output stream for the error console output. */
	private final PrintStream errorStream;
	/** Reduced output stream for error console output. */
	private PrintStream errorStreamReduced;
	/** Output stream for the general console output. */
	private final PrintStream outStream;
	/** Reduced output stream for general console output. */
	private PrintStream outStreamReduced;

	/** Determines the verbosity of printed information. */
	public int verboseLevel = 0;

	private Logger() {
		outStream = System.out;
		errorStream = System.err;
		outStreamReduced = outStream;
		errorStreamReduced = errorStream;
	}

	/**
	 * @return The verbosity level.
	 */
	public int getVerbosityLevel() {
		return verboseLevel;
	}

	/**
	 * Set up the logger.
	 * 
	 * @param outputPath   Path to a folder that should contain all logs.
	 * @param verboseLevel The verbosity of printed information.
	 * @throws FileNotFoundException If the output Path is not a directory.
	 */
	public void install(Path outputPath, int verboseLevel) throws FileNotFoundException {
		this.verboseLevel = verboseLevel;

		FileOutputStream outFileStream = new FileOutputStream(outputPath.resolve("console_log.txt").toFile());
		FileOutputStream errFileStream = new FileOutputStream(outputPath.resolve("error_log.txt").toFile());
		FileOutputStream outReducedFileStream = new FileOutputStream(
				outputPath.resolve("console_log_reduced.txt").toFile());
		FileOutputStream errReducedFileStream = new FileOutputStream(
				outputPath.resolve("error_log_reduced.txt").toFile());

		outStreamReduced = new PrintStream(new MultiStream(outStream, outFileStream, outReducedFileStream));
		errorStreamReduced = new PrintStream(new MultiStream(errorStream, errFileStream, errReducedFileStream));

		if (verboseLevel >= 2) {
			System.setOut(new PrintStream(new MultiStream(outStream, outFileStream)));
			System.setErr(new PrintStream(new MultiStream(errorStream, errFileStream)));
		} else {
			System.setOut(new PrintStream(outFileStream));
			System.setErr(new PrintStream(errFileStream));
		}
	}

	/**
	 * Logs a message to the error stream.
	 * 
	 * @param message Message to log.
	 */
	public final void logError(String message) {
		println(errorStreamReduced, message, true);
	}

	/**
	 * Logs a message to the error stream.
	 * 
	 * @param message     Message to log.
	 * @param onlyVerbose {@link Boolean#FALSE} When the message should be printed
	 *                    independent from the verborsity level.
	 */
	public final void logError(String message, boolean onlyVerbose) {
		println(errorStreamReduced, message, onlyVerbose);
	}

	/**
	 * Logs a {@link Throwable} to the error stream.
	 * 
	 * @param error Error to log.
	 */
	public final void logError(Throwable error) {
		println(errorStreamReduced, error, false);
	}

	/**
	 * Logs a {@link Throwable} to the error stream.
	 * 
	 * @param error       Error to log.
	 * @param onlyVerbose {@link Boolean#FALSE} When the error should be printed
	 *                    independent from the verbosity level.
	 */
	public final void logError(Throwable error, boolean onlyVerbose) {
		println(errorStreamReduced, error, onlyVerbose);
	}

	/**
	 * Logs a message to the output stream.
	 * 
	 * @param message Message to log.
	 */
	public final void logInfo(String message) {
		println(outStreamReduced, message, true);
	}

	/**
	 * Logs a message to the output stream.
	 * 
	 * @param message     Message to log.
	 * @param onlyVerbose {@link Boolean#FALSE} When the message should be printed
	 *                    independent from the verbosity level.
	 */
	public final void logInfo(String message, boolean onlyVerbose) {
		logInfo(message, 0, onlyVerbose);
	}

	/**
	 * Prints a message with a given identation.
	 * 
	 * @param stream Stream to print to.
	 * @param tabs   Number of tabs to indent.
	 */
	public final void logInfo(String message, int tabs) {
		final StringBuilder sb = new StringBuilder(getCurTime());
		sb.append(" ");
		for (int i = 0; i < tabs; i++) {
			sb.append('\t');
		}
		sb.append(message);
		logInfo(sb.toString());
	}

	/**
	 * Logs a message to the output stream that can be indented.
	 * 
	 * @param message     Message to log.
	 * @param tabs        Number of tabs to indent.
	 * @param onlyVerbose {@link Boolean#FALSE} When the message should be printed
	 *                    independent from the verbosity level.
	 */
	public final void logInfo(String message, int tabs, boolean onlyVerbose) {
		if (tabs > 0) {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tabs; i++) {
				sb.append('\t');
			}
			sb.append(message);
			println(outStreamReduced, sb.toString(), onlyVerbose);
		} else {
			println(outStreamReduced, message, onlyVerbose);
		}
	}

	/**
	 * Prints a message to a given print stream.
	 * 
	 * @param stream      Stream to print to.
	 * @param message     The message to print.
	 * @param onlyVerbose {@link Boolean#FALSE} When the message should be printed
	 *                    independent from the verbosity level.
	 */
	private void println(PrintStream stream, String message, boolean onlyVerbose) {
		if (verboseLevel > 0 || !onlyVerbose) {
			stream.println(getCurTime() + " " + message);
		}
	}

	/**
	 * Prints an error to a given print stream.
	 * 
	 * @param stream      Stream to print to.
	 * @param message     The message to print.
	 * @param onlyVerbose {@link Boolean#FALSE} When the error should be printed
	 *                    independent from the verbosity level.
	 */
	private void println(PrintStream stream, Throwable error, boolean onlyVerbose) {
		if (verboseLevel > 0 || !onlyVerbose) {
			stream.print(getCurTime() + ":");
			error.printStackTrace(stream);
			stream.println(getCurTime());
		}
	}

	/**
	 * Removes the output and error stream of the logger from the system.
	 */
	public void uninstall() {
		System.setOut(outStream);
		System.setErr(errorStream);
		outStreamReduced = outStream;
		errorStreamReduced = errorStream;
	}

}
