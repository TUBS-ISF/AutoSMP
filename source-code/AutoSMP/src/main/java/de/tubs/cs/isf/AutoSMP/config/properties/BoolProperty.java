package de.tubs.cs.isf.AutoSMP.config.properties;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * A generic property for all {@link Boolean}-based values in
 * {@link SamplingConfig} files.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class BoolProperty extends AProperty<Boolean> {

	/**
	 * Creates a new {@link Boolean}-based property with a given key.
	 * 
	 * @param key Identifer for the property.
	 */
	public BoolProperty(String key) {
		super(key, Boolean.FALSE);
	}

	/**
	 * Creates a new {@link Boolean}-based property with a given key and default
	 * value.
	 * 
	 * @param key          Identifer for the property.
	 * @param defaultValue Default value for the property.
	 */
	public BoolProperty(String key, Boolean defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Boolean cast(String valueString) throws Exception {
		return Boolean.parseBoolean(valueString);
	}

}
