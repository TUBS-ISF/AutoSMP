package de.tubs.cs.isf.AutoSMP.logger;

/**
 * Stream that logs every read line with the {@link Logger} as an error.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class ErrStreamReader implements IOutputReader {

	@Override
	public void readOutput(String line) throws Exception {
		Logger.getInstance().logError(line, true);
	}

}
