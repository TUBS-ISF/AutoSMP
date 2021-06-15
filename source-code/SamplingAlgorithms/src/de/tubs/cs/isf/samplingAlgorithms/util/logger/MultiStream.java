package de.tubs.cs.isf.samplingAlgorithms.util.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * A {@link MultiStream} writes not to only one output stream but to a number of
 * registered output streams. These can be registered when a new
 * {@link MultiStream} is created.
 * 
 * @see #MultiStream(OutputStream...)
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class MultiStream extends OutputStream {

	private final List<OutputStream> streamList = new ArrayList<>();

	public MultiStream(OutputStream... streamList) {
		super();
		this.streamList.addAll(Arrays.asList(streamList));
	}

	/**
	 * Flushes every registered output stream.
	 */
	@Override
	public void flush() throws IOException {
		for (OutputStream outputStream : streamList) {
			try {
				outputStream.flush();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream outputStream : streamList) {
			try {
				outputStream.write(b);
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) throws IOException {
		for (OutputStream outputStream : streamList) {
			try {
				outputStream.write(buf, off, len);
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream outputStream : streamList) {
			try {
				outputStream.write(b);
			} catch (IOException e) {
			}
		}
	}

}
