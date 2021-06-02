package de.tubs.cs.isf.AutoSMP;

public class Tuple<S1, S2> {

	public S1 key;
	public S2 value;
	
	public Tuple(S1 key, S2 value) {
		this.key = key;
		this.value = value;
	}
}
