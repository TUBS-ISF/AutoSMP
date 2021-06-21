package de.tubs.cs.isf.AutoSMP.modules;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.algorithms.AJavaMemoryTWiseSamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.algorithms.ASamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

/**
 * This module is part of {@link AutoSMP} and is responsible to
 * load external algorithms. Also, it needs to pepare the algorithms for their
 * execution.
 * 
 * @author Joshua Sprey
 *
 */
public class AlgorithmLoaderModule {

	private final SamplingConfig config;
	private final AutoSMP sampler;

	public AlgorithmLoaderModule(AutoSMP sampler) {
		this.sampler = sampler;
		this.config = sampler.getConfig();
	}

	/**
	 * Check whether an algorithm is available in the
	 * {@link SamplingConfig#algorithmPath}.
	 * 
	 * @param algorithmName Class name of the algorithm to search. This should
	 *                      include the package names.
	 * @return {@link Boolean#TRUE} if the algorithm implementation can be found.
	 */
	@SuppressWarnings({ "unchecked", "resource", "unused" })
	public boolean isAlgorithmAvailable(String algorithmName) {
		ClassLoader cl = null;
		try {
			// Load all external algorithms
			File file = config.algorithmPath.toFile();
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			cl = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			return false;
		}
		try {
			if (cl != null) {
				Class<AJavaMemoryTWiseSamplingAlgorithm> cls;
				cls = (Class<AJavaMemoryTWiseSamplingAlgorithm>) cl.loadClass(algorithmName);
				return cls != null;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
		return false;
	}

	/**
	 * Loads all algorithm that should be evaluated for the given system iteration.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ASamplingAlgorithm> loadAndPrepareAlgorithms() {
		ArrayList<ASamplingAlgorithm> algorithms = new ArrayList<>();

		ClassLoader cl = null;
		try {
			// Load all external algorithms
			File file = config.algorithmPath.toFile();
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			cl = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			Logger.getInstance().logError(e);
		}

		for (String algorithmName : config.algorithms.getValue()) {
			final int tValue = config.tCoverage.getValue();
			String modelName = sampler.currentSystemName;
			final Path sampleFile = config.tempPath.resolve(modelName);
			final Path modelFile = config.tempPath.resolve(modelName + ".dimacs");
			final Path gcCollectorFile = config.tempPath.resolve("runtimeGC.log");
			final String minAllocation = config.minimumMemoryAllocation.getValue();
			final String maxAllocation = config.maximumMemoryAllocation.getValue();
			final int maxSize = config.maxSize.getValue();
			final int randomSeed = config.randomSeed.getValue();
			final Path algoPath = config.algorithmPath;
			// Try if the given string is a class name for an external algorithm
			try {
				if (cl != null) {

					Class<ASamplingAlgorithm> cls;
					cls = (Class<ASamplingAlgorithm>) cl.loadClass(algorithmName);
					try {
						// First try to get a declared constructor for AJavaMemoryTWiseSamplingAlgorithm
						algorithms.add(cls.getDeclaredConstructor(Path.class, Path.class, Path.class, int.class, int.class, int.class, Path.class,
								String.class, String.class).newInstance(algoPath, modelFile, sampleFile, tValue, maxSize, randomSeed, gcCollectorFile,
										minAllocation, maxAllocation));
					} catch (Exception e) {
						try {
							algorithms.add(cls.getDeclaredConstructor(Path.class, Path.class, Path.class, int.class, int.class, int.class)
									.newInstance(algoPath, modelFile, sampleFile, tValue, maxSize, randomSeed));
						} catch (Exception e2) {
							// When no constructor was found report.
							Logger.getInstance().logError(e);
							Logger.getInstance().logError(e2);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				Logger.getInstance().logError(e);
			}
		}
		return algorithms;
	}

}
