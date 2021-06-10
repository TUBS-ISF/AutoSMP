package de.tubs.cs.isf.AutoSMP.customRecommender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.tubs.cs.isf.AutoSMP.config.properties.IProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.IntProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.PathProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.StringListProperty;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

public class RecommendationConfig {

	// ####### Defintion of default values
	public final int DEFAULT_SIZE_WEIGHT = 1;
	public final int DEFAULT_COVERAGE_WEIGHT = 1;
	public final int DEFAULT_TIME_WEIGHT = 1;
	public final int DEFAULT_STABILITY_WEIGHT = 1;
	public final List<String> DEFAULT_BENCHMARKS = new ArrayList<String>();
	public final String DEFAULT_OUT = "./";
	
	/** Contains all properties after reading the configuration file. */
	protected static final List<IProperty> propertyList = new LinkedList<>();
	
	// ###### Define puplic static methods to work with the propertyList
		/**
		 * Adds a property to the complete list of all properties.
		 * 
		 * @param property Property to add.
		 */
		public static void addProperty(IProperty property) {
			propertyList.add(property);
		}

		/**
		 * @return A list with all properties.
		 */
		public static List<IProperty> getPropertyList() {
			return propertyList;
		}
		
	//######## Define runtime properties
		/**
		 * Definition of which benchmarks will be analysed
		 */
		public StringListProperty benchmarksProp = new StringListProperty("benchmarks", DEFAULT_BENCHMARKS);
		
	// Defines custom weights
		/**
		 * Definition of the weigth factor for size
		 */
		public IntProperty size = new IntProperty("size", DEFAULT_SIZE_WEIGHT);
		
		/**
		 * Definition of the weigth factor for coverage
		 */
		public IntProperty coverage = new IntProperty("coverage", DEFAULT_COVERAGE_WEIGHT);
		
		/**
		 * Definition of the weigth factor for run-time
		 */
		public IntProperty time = new IntProperty("time", DEFAULT_TIME_WEIGHT);
		
		/**
		 * Definition of the weigth factor for stability
		 */
		public IntProperty stability = new IntProperty("stability", DEFAULT_STABILITY_WEIGHT);
		
		/**
		 * Pathproperty for the output Path
		 */
		public PathProperty outputDir = new PathProperty("output", DEFAULT_OUT);
		
	//###### Field Declaration
		public int sizeWeight = DEFAULT_SIZE_WEIGHT;
		public int coverageWeight = DEFAULT_COVERAGE_WEIGHT;
		public int timeWeight = DEFAULT_TIME_WEIGHT;
		public int stabilityWeight = DEFAULT_STABILITY_WEIGHT;
		public List<String> benchmarks = new ArrayList<String>();
		public Path outPut = Paths.get(DEFAULT_OUT);
		
	// ####### Constructor Definition
		public RecommendationConfig(Path requestPath) {
			init();
			readRequest(requestPath);
			setRequestProperties();
		}

	// ####### init method to fill propety list
		private void init() {
			addProperty(benchmarksProp);
			addProperty(coverage);
			addProperty(size);
			addProperty(stability);
			addProperty(time);
			addProperty(outputDir);
		}
		
	// ####### private method to set request properties
		private void setRequestProperties() {
			outPut = outputDir.getValue();
			sizeWeight = size.getValue();
			coverageWeight = coverage.getValue();
			timeWeight = time.getValue();
			stabilityWeight = stability.getValue();
			benchmarks = benchmarksProp.getValue();
		}
	// ####### private method declaration
		private void readRequest(Path requestPath) {
			try {
				readRequestFile(requestPath);
			} catch (Exception e) {
			}
		}

		/**
		 * Reads the content of a configuration file.
		 * 
		 * @param path Path to the configuration file.
		 * @return A persistent set of {@link Properties} containing the properties of
		 *         the configuration file.
		 * @throws Exception When path is invalid or configuration is invalid.
		 */
		private Properties readRequestFile(final Path path) throws Exception {
			Logger.getInstance().logInfo("Reading request config file. (" + path.toString() + ") ... ", false);
			final Properties properties = new Properties();
			try {
				properties.load(Files.newInputStream(path));
				for (IProperty prop : propertyList) {
					String value = properties.getProperty(prop.getKey());
					if (value != null) {
						prop.setValue(value);
					}
					Logger.getInstance()
					.logInfo("Property contained in prop list: " + prop.getKey() + "=" + prop.getValue(), true);
				}
				Logger.getInstance().logInfo("Success!", false);
				return properties;
			} catch (IOException e) {
				Logger.getInstance().logInfo("Fail! -> " + e.getMessage(), false);
				Logger.getInstance().logError(e);
				throw e;
			}
		}
}
