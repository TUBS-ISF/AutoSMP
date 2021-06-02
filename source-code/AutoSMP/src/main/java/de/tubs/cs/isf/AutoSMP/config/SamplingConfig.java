/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.tubs.cs.isf.AutoSMP.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import de.tubs.cs.isf.AutoSMP.config.properties.BoolProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.IProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.IntProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.LongProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.StringListProperty;
import de.tubs.cs.isf.AutoSMP.config.properties.StringProperty;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

/**
 * Contains the configurable options for the sampling framework.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class SamplingConfig {

	/**
	 * The default file extension for a SamplingConfig.
	 */
	private static final String CONFIG_FILE_EXTENSION = ".properties";
	/**
	 * The default path for the algorithms directory relative to the application
	 * path.
	 */
	private static final String DEFAULT_ALGORITHMS_DIRECTORY = "algorithms";
	/**
	 * The default path for the configuration directory relative to the application
	 * path.
	 */
	private static final String DEFAULT_CONFIG_DIRECTORY = "config";
	/**
	 * The default name for the configuration file for the sampling framework.
	 */
	private static final String DEFAULT_CONFIG_NAME = "framework_config";
	/**
	 * The default path for the data directory relative to the output path.
	 */
	private static final String DEFAULT_DATA_DIRECTORY = "data";
	/**
	 * The default path for the input model directory relative to the application
	 * path.
	 */
	private static final String DEFAULT_INPUT_DIRECTORY = "models";
	/**
	 * The default path for the output directory relative to the application path.
	 */
	private static final String DEFAULT_OUTPUT_DIRECTORY = "output";
	/**
	 * The default path for the samples directory relative to the output path.
	 */
	private static final String DEFAULT_SAMPLES_DIRECTORY = "samples";

	/** Contains all properties after reading the configuration file. */
	protected static final List<IProperty> propertyList = new LinkedList<>();

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

	/** {@link IntProperty} indicates the verbosity of output information. */
	public final IntProperty algorithmIterations = new IntProperty("algorithmIterations", 1);
	/** Path to the folder containing sampling algorithm implementations files. */
	public Path algorithmPath;

	/**
	 * {@link BoolProperty} indicates whether computed samples should be saved or
	 * not.
	 */
	public final StringListProperty algorithms = new StringListProperty("algorithms");
	/** {@link StringProperty} indicates the author of the current benchmark. */
	public final StringProperty author = new StringProperty("author");
	/**
	 * {@link BoolProperty} indicates whether computed samples should be saved or
	 * not.
	 */
	public final StringProperty calculateStability = new StringProperty("calculateStability", "");
	/** Path to the folder containing configuration files. */
	public Path configPath;

	/** Path to the folder containing <code>.csv</code> files. */
	public Path csvPath;
	/**
	 * {@link BoolProperty} indicates whether temporary files should be deleted or
	 * not.
	 */
	public final BoolProperty debug = new BoolProperty("debug", false);
	/** Path to the folder containing model files. */
	public Path inputPath;
	/** Path to the folder containing log files. */
	public Path logPath;
	/** {@link StringProperty} indicates the author of the current benchmark. */
	public final StringProperty maximumMemoryAllocation = new StringProperty("maxAlloc", "Xmx4g");

	/** {@link StringProperty} indicates the author of the current benchmark. */
	public final StringProperty minimumMemoryAllocation = new StringProperty("minAlloc", "Xms2g");
	/** Path to the folder containing output files.. */
	public Path outputPath;
	/** {@link Seed} determines the seed for each randomized operation. */
	public final LongProperty randomSeed = new LongProperty("seed", System.currentTimeMillis());
	/** Path to the folder containing the computed sample files. */
	public Path samplesPath;
	/**
	 * {@link BoolProperty} indicates whether computed samples should be saved or
	 * not.
	 */
	public final BoolProperty storeSamples = new BoolProperty("storeSamples", false);
	/**
	 * List containing the IDS of all systems that should be used in the current
	 * benchmark.
	 */
	public List<Integer> systemIDs;
	/**
	 * {@link IntProperty} indicates how often a certain system is repeated in the
	 * benchmark.
	 */
	public final IntProperty systemIterations = new IntProperty("systemIterations", 1);
	/**
	 * List containing the names of all systems that should be used in the current
	 * benchmark.
	 */
	public List<String> systemNames = new ArrayList<>();
	/**
	 * {@link IntProperty} indicates the degree of t-wise coverage that should be
	 * achieved.
	 */
	public final IntProperty tCoverage = new IntProperty("t");
	/** Path to the folder containing temporary files. */
	public Path tempPath;

	/** {@link Timeout} determines the timeout for each algorithm iteration. */
	public final LongProperty timeout = new LongProperty("timeout", Long.MAX_VALUE);

	/** {@link IntProperty} indicates the verbosity of output information. */
	public final IntProperty verbosity = new IntProperty("verbosity", 0);

	/**
	 * Creates a {@link SamplingConfig} at the
	 * {@link SamplingConfig#DEFAULT_CONFIG_DIRECTORY} and reads the default
	 * configuration {@link SamplingConfig#DEFAULT_CONFIG_NAME}.
	 * 
	 * @param configPath Path to the configuration file.
	 */
	public SamplingConfig() {
		this.configPath = Paths.get(DEFAULT_CONFIG_DIRECTORY);
		readConfig(DEFAULT_CONFIG_NAME);
	}

	/**
	 * Creates a {@link SamplingConfig} at the
	 * {@link SamplingConfig#DEFAULT_CONFIG_DIRECTORY} and reads the configuration
	 * with the given <code>configName</code>.
	 * 
	 * @param configPath File name of the configuration.
	 */
	public SamplingConfig(String configName) {
		this.configPath = Paths.get(DEFAULT_CONFIG_DIRECTORY);
		readConfig(configName);
	}

	/**
	 * Return the file name for a given path if the {@link Path} points to a
	 * {@link Files}.
	 * 
	 * @param modelFile Path to the file.
	 * @return File name if <code>modelFile</code> points to {@link Files},
	 *         otherwise <code>null</code>
	 */
	public String getFileName(Path filePath) {
		return filePath.toFile().getName().replaceFirst("[.][^.]+$", "");
	}

	/**
	 * Check if the given paths is a valid input model.
	 * 
	 * @param filePath Path to file.
	 * @return true when <code>filePath</code> points to valid input model.
	 */
	public boolean isAcceptedModel(Path filePath) {
		return filePath.toString().endsWith(".xml") || filePath.toString().endsWith(".dimacs")
				|| filePath.toString().endsWith(".m");
	}

	/**
	 * Reads the configuration file with the given name.
	 * 
	 * @param configName File name of the configuration to be read.
	 */
	private void readConfig(String configName) {
		try {
			readConfigFile(this.configPath.resolve(configName + CONFIG_FILE_EXTENSION));
		} catch (Exception e) {
		}
		inputPath = Paths.get(DEFAULT_INPUT_DIRECTORY);
		algorithmPath = Paths.get(DEFAULT_ALGORITHMS_DIRECTORY);
		outputPath = Paths.get(DEFAULT_OUTPUT_DIRECTORY);
		csvPath = outputPath.resolve(DEFAULT_DATA_DIRECTORY);
		samplesPath = outputPath.resolve(DEFAULT_SAMPLES_DIRECTORY);
		tempPath = outputPath.resolve("temp");
		logPath = outputPath.resolve("log-" + System.currentTimeMillis());
	}

	/**
	 * Reads the content of a configuration file.
	 * 
	 * @param path Path to the configuration file.
	 * @return A persistent set of {@link Properties} containing the properties of
	 *         the configuration file.
	 * @throws Exception When path is invalid or configuration is invalid.
	 */
	private Properties readConfigFile(final Path path) throws Exception {
		Logger.getInstance().logInfo("Reading config file. (" + path.toString() + ") ... ", false);
		final Properties properties = new Properties();
		try {
			properties.load(Files.newInputStream(path));
			for (IProperty prop : propertyList) {
				String value = properties.getProperty(prop.getKey());
				if (value != null) {
					prop.setValue(value);
				}
			}
			Logger.getInstance().logInfo("Success!", false);
			return properties;
		} catch (IOException e) {
			Logger.getInstance().logInfo("Fail! -> " + e.getMessage(), false);
			Logger.getInstance().logError(e);
			throw e;
		}
	}

	/**
	 * Creates a list of all available model names in the input directory.
	 */
	public void readSystemNames() {
		try {
			try (Stream<Path> paths = Files.walk(inputPath)) {
				paths.filter(this::isAcceptedModel).forEach((x) -> systemNames.add(getFileName(x)));
			}
		} catch (IOException e) {
			Logger.getInstance().logError("No feature models specified!");
			Logger.getInstance().logError(e);
		}

		if (systemNames.isEmpty()) {
			systemIDs = new ArrayList<>(systemNames.size());
			for (int i = 0; i < systemNames.size(); i++) {
				systemIDs.add(i);
			}
		} else {
			systemIDs = new ArrayList<>();
			Logger.getInstance().logError("No feature models found in input path: \"" + inputPath.toString() + "\"!");
		}
	}

	public void refreshPaths() {
		csvPath = outputPath.resolve(DEFAULT_DATA_DIRECTORY);
		samplesPath = outputPath.resolve(DEFAULT_SAMPLES_DIRECTORY);
		tempPath = outputPath.resolve("temp");
		logPath = outputPath.resolve("log-" + System.currentTimeMillis());
		readSystemNames();
	}

}
