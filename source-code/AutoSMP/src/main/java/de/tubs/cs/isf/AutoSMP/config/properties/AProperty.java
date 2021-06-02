package de.tubs.cs.isf.AutoSMP.config.properties;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * Abstract implementation for each {@link IProperty}. Handles the usage of a
 * properties keys and their value objects.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 *
 * @param <T> Type of the {@link IProperty}.
 */
public abstract class AProperty<T> implements IProperty {

	/** The default value for this property when no value is given. */
	private final T defaultValue;
	/** Identifies the property. */
	private final String key;
	/** The value object for this property. */
	private T value;

	/**
	 * Constructor to create a new property with the given key. The default value
	 * for this property is <code>null</code>.Adds the new property to the list of
	 * all {@link IProperty} in {@link SamplingConfig}.
	 * 
	 * @param key Identifier for the new property.
	 */
	public AProperty(String key) {
		this(key, null);
	}

	/**
	 * Constructor to create a new property with the given key and default value.
	 * Adds the new property to the list of all {@link IProperty} in
	 * {@link SamplingConfig}.
	 * 
	 * @param key          Identifier for the new property.
	 * @param defaultValue Default value for the new property.
	 */
	public AProperty(String key, T defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
		SamplingConfig.addProperty(this);
	}

	/**
	 * Casts a string into the type of the property.
	 * 
	 * @param valueString The value that was read and should be casted into the type
	 *                    of the property.
	 * @return The value of <code>valueString</code> as type T.
	 * @throws Exception When <code>valueString</code> cannot be casted to T.
	 */
	protected abstract T cast(String valueString) throws Exception;

	/**
	 * Retrieves the default value for this property.
	 * 
	 * @return Default property as T.
	 */
	protected T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public T getValue() {
		return (value != null) ? value : defaultValue;
	}

	@Override
	public boolean setValue(String valueString) {
		if (valueString != null) {
			try {
				value = cast(valueString);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append(" = ");
		if (value != null) {
			sb.append(value.toString());
		} else if (defaultValue != null) {
			sb.append(defaultValue.toString());
			sb.append(" (default value)");
		} else {
			sb.append("null");
		}
		return sb.toString();
	}

}
