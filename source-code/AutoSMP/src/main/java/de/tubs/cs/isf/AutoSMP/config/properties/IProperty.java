package de.tubs.cs.isf.AutoSMP.config.properties;

import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;

/**
 * Interface for entries in the {@link SamplingConfig}.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public interface IProperty {

	/**
	 * Retrieves the identifier for this property.
	 * 
	 * @return The properties identifier as {@link String}.
	 */
	String getKey();

	/**
	 * Retrieves the object for this property.
	 * 
	 * @return The properties object.
	 */
	Object getValue();

	/**
	 * Sets the object for this property.
	 * 
	 * @return The properties object.
	 */
	boolean setValue(String valueString);

}
