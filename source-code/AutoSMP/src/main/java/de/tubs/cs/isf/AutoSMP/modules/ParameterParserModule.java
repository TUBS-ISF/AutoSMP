package de.tubs.cs.isf.AutoSMP.modules;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

public class ParameterParserModule {
	private static final String PARAMETER_ALGORITHM = "-alg";

	private static final String PARAMETER_COVERAGE = "-t";
	private static final String PARAMETER_INPUTSYSTEM_PATH = "-in";

	private static final String PARAMETER_OUTPUTSYSTEM_PATH = "-out";
	private static final String PARAMETER_STORE_MODE_PATH = "-store";
	
	private static final String PARAMETER_SAMPLING = "-sampling"; 
	private static final String PARAMETER_REQUEST = "-request";

	private final SamplingConfig config;
	private final AutoSMP sampler;

	public ParameterParserModule(AutoSMP sampler) {
		this.sampler = sampler;
		this.config = sampler.getConfig();
	}

	private boolean isAcceptedModel(Path file) {
		return file.toString().endsWith(".xml") || file.toString().endsWith(".dimacs");
	}

	private boolean isAlgorithmAvailable(String algorithmName) {
		return sampler.module_AlgorithmLoader.isAlgorithmAvailable(algorithmName);
	}

	/**
	 * Reads arguments and configures the program accordingly.
	 * 
	 * Valid Parameters: -alg: Determines the only algorithm to use (expects a class
	 * name extending the ATWiseSampling class)
	 * 
	 * -t: Determines the t coverage (1,2,3...)
	 * 
	 * -in: Determines the input path for systems to use (Excpect input path
	 * containing models)
	 * 
	 * -out: Determines the output path (valid system path)
	 * 
	 * @param args List containing all parameters
	 */
	public boolean parseParameter(String[] args) {
		Logger.getInstance().logInfo("Reading arguments...", 0, false);
		// Configure depending of program arguments
		List<String> arguments = Arrays.asList(args);

		// 1) (-alg) Algorithm (expects a class name extending the ATWiseSampling class)
		if (arguments.contains(PARAMETER_ALGORITHM)) {
			Logger.getInstance().logInfo("reading algos -alg...", false);
			int index = arguments.indexOf(PARAMETER_ALGORITHM);
			if ((index + 1) < arguments.size()) {
				String algorithmName = arguments.get(index + 1);
				;
				config.algorithms.setValue(algorithmName);
				if (isAlgorithmAvailable(algorithmName)) {
					Logger.getInstance().logInfo("[-alg] = : " + algorithmName + " was found", 1, false);
				} else {
					Logger.getInstance().logInfo("[Error] [-alg] = : " + algorithmName + " was not found!", 1, false);
					return false;
				}
			} else {
				Logger.getInstance().logInfo("[Error] [-alg] Value is missing (algorithm's absolute class name)", 1,
						false);
				return false;
			}
		}

		// 2) (-t) t_wise coverage
		if (arguments.contains(PARAMETER_COVERAGE)) {
			Logger.getInstance().logInfo("reading coverage -t...", false);
			int index = arguments.indexOf(PARAMETER_COVERAGE);
			if ((index + 1) < arguments.size()) {
				String value = arguments.get(index + 1);
				try {
					int coverage = Integer.parseInt(value);
					if (coverage >= 1 && coverage <= 5) {
						config.tCoverage.setValue(value);
						Logger.getInstance().logInfo("[-t] = : " + arguments.get(index), 1, false);
					} else {
						Logger.getInstance().logInfo("[Error] [-t] = \"" + arguments.get(index)
								+ "\" not valid. Please specify a valid coverage (1-5)", 1, false);
						return false;
					}
				} catch (Exception e) {
					Logger.getInstance().logInfo("[Error] [-t] = \"" + arguments.get(index)
							+ "\" not valid. Please specify a valid coverage (1-5)", 1, false);
					return false;
				}
			} else {
				Logger.getInstance().logInfo("[Error] [--t] Value is missing (1-5)", 1, false);
				return false;
			}
		}

		// 3) Specify systems path
		if (arguments.contains(PARAMETER_INPUTSYSTEM_PATH)) {
			Logger.getInstance().logInfo("reading system specifics -in...", false);
			int index = arguments.indexOf(PARAMETER_INPUTSYSTEM_PATH);
			if ((index + 1) < arguments.size()) {
				String absolutePathToSystems = arguments.get(index + 1);
				try {
					// Check that the path exist
					Path pathToSystems = Paths.get(absolutePathToSystems);
					if (Files.exists(pathToSystems)) {
						Logger.getInstance().logInfo("[-in] = \"" + absolutePathToSystems.toString() + "\"", 1, false);
						long count = 0;
						try (Stream<Path> paths = Files.walk(pathToSystems)) {
							count = paths.filter(this::isAcceptedModel).count();
						}
						if (count <= 0) {
							Logger.getInstance().logInfo("[Error] [-in] Input path does not contain any valid models.",
									2, false);
							return false;
						} else {
							Logger.getInstance().logInfo("Found " + count + " models.", 2, false);
							config.inputPath = pathToSystems;
						}
					} else {
						// Path is non-existent
						Logger.getInstance().logInfo("[Error] [-in] = \"" + absolutePathToSystems.toString()
								+ "\" is non-existent! (valid aboslute path)", 1, false);
						return false;
					}
				} catch (Exception e) {
					// String cannot be parsed to path or cannot be created => invalid
					Logger.getInstance().logInfo("[Error] [-in] = \"" + absolutePathToSystems.toString()
							+ "\" is invalid! (valid aboslute path)", 1, false);
					return false;
				}
			} else {
				Logger.getInstance().logInfo("[Error] [-in] Value is missing (valid absolute path)", 1, false);
				return false;
			}
		}

		// 4) Specify output path
		if (arguments.contains(PARAMETER_OUTPUTSYSTEM_PATH)) {
			Logger.getInstance().logInfo("reading ouput Paths -out...", false);
			int index = arguments.indexOf(PARAMETER_OUTPUTSYSTEM_PATH);
			if ((index + 1) < arguments.size()) {
				String absolutePathToOutput = arguments.get(index + 1);
				try {
					// Check that the path exist
					Path pathToOutput = Paths.get(absolutePathToOutput);
					if (Files.exists(pathToOutput)) {
						Logger.getInstance().logInfo("[-out] = \"" + absolutePathToOutput.toString() + "\"", 1, false);
						config.outputPath = pathToOutput;
					} else {
						// Path is not existent => create
						Logger.getInstance().logInfo("[-out] = \"" + absolutePathToOutput.toString()
								+ "\" is non-existent. Will be created...", 1, false);

						// Create directory
						Files.createDirectories(pathToOutput);
						Logger.getInstance().logInfo("Done!", 2, false);
						config.outputPath = pathToOutput;
					}
				} catch (Exception e) {
					// String cannot be parsed to path or cannot be created => invalid
					Logger.getInstance().logInfo("[Error] [-out] = \"" + absolutePathToOutput.toString()
							+ "\" is invalid! (valid aboslute path)", 1, false);
					return false;
				}
			} else {
				Logger.getInstance().logInfo("[Error] [-out] Value is missing (valid absolute path)", 1, false);
				return false;
			}
		}

		// 5) Identify store mode
		if (arguments.contains(PARAMETER_STORE_MODE_PATH)) {
			Logger.getInstance().logInfo("reading store method -store...", false);
			config.storeSamples.setValue("true");
			Logger.getInstance().logInfo("[-store] = true", false);
		}
		
		// 6) Defintion of request path
		if(arguments.contains(PARAMETER_REQUEST)) {
			Logger.getInstance().logInfo("Reading Request path.", false);
			int index = arguments.lastIndexOf(PARAMETER_REQUEST);
			if(arguments.size() >= index+1) {
				Logger.getInstance().logInfo(arguments.get(index+1), false);
				Path requestPath = Paths.get(arguments.get(index+1));
				config.requestPath = requestPath;
				config.doCustomRecommendation = true;
			}
			else {
				Logger.getInstance().logError("your defintion of the config path is of.");
			}
			
		}
		// 7) Defintion whether sampling will be executed
		if(arguments.contains(PARAMETER_REQUEST) && !(arguments.contains(PARAMETER_SAMPLING))) {
			config.doSampling = false; 
		}
		else {
			config.doSampling = true;
		}
		Logger.getInstance().logInfo(" ", 0, false);
		return true;
	}
}
