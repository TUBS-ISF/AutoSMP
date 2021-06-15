package de.tubs.cs.isf.samplingAlgorithms.util.logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads an output and collects each line as an error list.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class ErrStreamCollector implements IOutputReader {

	private final List<String> errList = new ArrayList<>();

	/**
	 * @return Returns the error list containing all collected errors.
	 */
	public List<String> getErrList() {
		return errList;
	}

	@Override
	public void readOutput(String line) throws Exception {
		errList.add(line);
	}

}
