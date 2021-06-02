package de.tubs.cs.isf.AutoSMP.config.properties;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * A generic property for all {@link String}-based values in
 * {@link SamplingConfig} files.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class StringProperty extends AProperty<String> {

	/**
	 * Creates a new {@link String}-based property with a given key.
	 * 
	 * @param key Identifer for the property.
	 */
	public StringProperty(String key) {
		super(key, "");
	}

	/**
	 * Creates a new {@link String}-based property with a given key and default
	 * value.
	 * 
	 * @param key          Identifer for the property.
	 * @param defaultValue Default value for the property.
	 */
	public StringProperty(String key, String defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected String cast(String valueString) throws Exception {
		return valueString;
	}

}
