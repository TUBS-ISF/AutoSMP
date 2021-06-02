package de.tubs.cs.isf.AutoSMP.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Redirects the input of one stream to a list output {@link IOutputReader}.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class StreamRedirector implements Runnable {

	private InputStream in;
	private final List<IOutputReader> outputReaderList;

	public StreamRedirector(List<IOutputReader> outputReaderList) {
		this.outputReaderList = outputReaderList;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				for (IOutputReader outputReader : outputReaderList) {
					try {
						outputReader.readOutput(line);
					} catch (Exception e) {
					}
				}
			}
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
	}

	/**
	 * Sets the input stream that should be redirected.
	 * 
	 * @param in Input stream to be redirected.
	 */
	public void setInputStream(InputStream in) {
		this.in = in;
	}

}
