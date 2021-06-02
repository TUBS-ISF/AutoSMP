package de.tubs.cs.isf.AutoSMP.config.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * A generic property for all {@link List} containing {@link String} values in
 * {@link SamplingConfig} files.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class StringListProperty extends AProperty<List<String>> {

	/**
	 * Creates a new {@link List}-based property with a given key.
	 * 
	 * @param key Identifer for the property.
	 */
	public StringListProperty(String key) {
		super(key, Collections.emptyList());
	}

	/**
	 * Creates a new {@link List}-based property with a given key and default value.
	 * 
	 * @param key          Identifer for the property.
	 * @param defaultValue Default value for the property.
	 */
	public StringListProperty(String key, List<String> defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected List<String> cast(String valueString) throws Exception {
		return Arrays.asList(valueString.split(","));
	}

}
