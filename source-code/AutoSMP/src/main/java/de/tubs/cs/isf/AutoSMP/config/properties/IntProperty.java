package de.tubs.cs.isf.AutoSMP.config.properties;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * A generic property for all {@link Integer}-based values in
 * {@link SamplingConfig} files.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class IntProperty extends AProperty<Integer> {

	/**
	 * Creates a new {@link Integer}-based property with a given key.
	 * 
	 * @param key Identifer for the property.
	 */
	public IntProperty(String key) {
		super(key, 0);
	}

	/**
	 * Creates a new {@link Integer}-based property with a given key and default
	 * value.
	 * 
	 * @param key          Identifer for the property.
	 * @param defaultValue Default value for the property.
	 */
	public IntProperty(String key, int defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Integer cast(String valueString) throws Exception {
		return Integer.parseInt(valueString);
	}

}
