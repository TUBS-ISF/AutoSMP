package de.tubs.cs.isf.AutoSMP.logger;

/**
 * This interfaces is used by to read the output of streams.
 * 
 * @author Joshua
 *
 */
public interface IOutputReader {

	/**
	 * Is called whenever a stream writes anything in the output.
	 * 
	 * @param line Text of the line.
	 * @throws Exception
	 */
	public void readOutput(String line) throws Exception;

}
