package de.tubs.cs.isf.AutoSMP;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.ClauseList;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.dimacs.DIMACSFormatCNF;
import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.tubs.cs.isf.AutoSMP.algorithms.ASamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;
import de.tubs.cs.isf.AutoSMP.config.properties.IProperty;
import de.tubs.cs.isf.AutoSMP.customRecommender.Customrecommender;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.modules.AlgorithmLoaderModule;
import de.tubs.cs.isf.AutoSMP.modules.ParameterParserModule;
import de.tubs.cs.isf.AutoSMP.modules.StabilityCalculatorModule;
import de.tubs.cs.isf.AutoSMP.modules.WriterModule;
import de.tubs.cs.isf.AutoSMP.process.SamplingProcessRunner;
import de.tubs.cs.isf.AutoSMP.process.SamplingResults;
import de.tubs.cs.isf.AutoSMP.util.FeatureModelReader;

/**
 * This class control the evaluation of the sampling framework.
 * 
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class AutoSMP {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			Logger.getInstance().logInfo("Configuration path and name not specified!", 0, false);
			return;
		}

		if (args.length < 2) {
			Logger.getInstance().logInfo("Configuration path or name not specified!" + "Your input was: " + args[0], 0,
					false);
			return;
		}

		final AutoSMP evaluator = new AutoSMP(args[0], args[1]);
		if (evaluator.parseParameter(args)) {
			evaluator.init();
			if(evaluator.config.doSampling) {
				evaluator.run();
			}
			if(evaluator.config.doCustomRecommendation) {
				evaluator.calculateCustomRecommendation();
			}
			evaluator.dispose();
			Logger.getInstance().logInfo("Shutting down AutoSMP successful!", false);
		} else {
			Logger.getInstance().logInfo("Stopping framework. Reason: see [Error] above!", 0, false);
		}
	}

	public static String toString(List<String> sample) {
		StringBuilder sb = new StringBuilder();
		for (String string : sample) {
			sb.append(string);
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/** Index of the currently evaluated algorithm */
	protected int algorithmIndex;

	/** The value of the current algorithm iteration. */
	protected int algorithmIteration;

	/** List containing all algorithms */
	protected List<ASamplingAlgorithm> algorithmList;
	/** The configuration for the sampling framework. */
	protected final SamplingConfig config;
	/**
	 * Points to a folder for the current model where the computed samples for the
	 * respective model should be stored.
	 */
	protected Path curSampleDir;
	/** The currently evaluated feature model in conjunctive normal form. */
	protected CNF modelCNF;

	public AlgorithmLoaderModule module_AlgorithmLoader;

	public ParameterParserModule module_ParameterParser;
	public StabilityCalculatorModule module_StabilityCalculator;

	public WriterModule module_Writer;
	
	public Customrecommender recommender;
	/**
	 * The currently evaluated feature model, randomized, in conjunctive normal
	 * form.
	 */
	protected CNF randomizedModelCNF;

	/** The results of the last evaluation run */
	protected SamplingResults result;

	/** The numerical index of the currently processed system. */
	protected int systemIndex;
	/** The value of the current system iteration. */
	protected int systemIteration;
	/**
	 * Contains all systems feature models to be used for the stability calculation
	 */
	public IFeatureModel[] systems = null;

	/**
	 * Creates a new {@link AutoSMP} that automatically read the configuration, set
	 * up every path, and do more things.
	 * 
	 * @param configName Name of the configuration to load.
	 * @throws Exception
	 */
	public AutoSMP(String configName) throws Exception {
		config = new SamplingConfig(configName);

		// Create modules
		module_ParameterParser = new ParameterParserModule(this);
		module_AlgorithmLoader = new AlgorithmLoaderModule(this);
		module_StabilityCalculator = new StabilityCalculatorModule(this);
		module_Writer = new WriterModule(this);
		recommender = new Customrecommender(this);
	}

	/**
	 * Creates a new {@link AutoSMP} that automatically read the configuration, set
	 * up every path, and do more things.
	 * 
	 * @param configPath Path of the configuration folder
	 * @param configName Name of the configuration to load.
	 * @throws Exception
	 */
	public AutoSMP(String configPath, String configName) throws Exception {
		config = new SamplingConfig(configPath, configName);

		// Create modules
		module_ParameterParser = new ParameterParserModule(this);
		module_AlgorithmLoader = new AlgorithmLoaderModule(this);
		module_StabilityCalculator = new StabilityCalculatorModule(this);
		module_Writer = new WriterModule(this);
		recommender = new Customrecommender(this);
	}

	protected CNF adaptModel() throws Exception {
		final CNF randomCNF = modelCNF.randomize(new Random(config.randomSeed.getValue() + systemIteration));
		final DIMACSFormatCNF format = new DIMACSFormatCNF();
		final Path fileName = config.tempPath.resolve("model" + "." + format.getSuffix());
		SimpleFileHandler.save(fileName, randomCNF, format);
		return randomCNF;
	}

	/**
	 * Deletes the entire temp folder and all it's content.
	 */
	private void deleteTempFolder() {
		try {
			Files.walkFileTree(config.tempPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.deleteIfExists(dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		Logger.getInstance().logInfo("Disposing AutoSMP", false);
		Logger.getInstance().uninstall();
		if (!config.debug.getValue()) {
			deleteTempFolder();
		}
		// remove csv files
		try {
			Files.walkFileTree(config.csvPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.getFileName().toString().contains("algorithms.csv")
							|| file.getFileName().toString().contains("models.csv")) {
						Files.deleteIfExists(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getAlgorithmIndex() {
		return algorithmIndex;
	}

	public int getAlgorithmIteration() {
		return algorithmIteration;
	}

	public List<ASamplingAlgorithm> getAlgorithmList() {
		return algorithmList;
	}

	public SamplingConfig getConfig() {
		return config;
	}

	public Path getCurSampleDir() {
		return curSampleDir;
	}

	public CNF getModelCNF() {
		return modelCNF;
	}

	public CNF getRandomizedModelCNF() {
		return randomizedModelCNF;
	}

	public int getSystemIndex() {
		return systemIndex;
	};

	public int getSystemIteration() {
		return systemIteration;
	};

	public IFeatureModel[] getSystems() {
		return systems;
	}

	/**
	 * Initializes everything for the evaluation.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		setupDirectories();
		// Create the csv writer and respective file.
		module_Writer.init();

		systems = new IFeatureModel[config.systemNames.size()];
		Logger.getInstance().logInfo("systemNames and size: " + config.systemNames + " // " + config.systemNames.size(), true);
		 module_StabilityCalculator.init();
		 
		if(this.config.doCustomRecommendation) {
			recommender.init();
		}

		Logger.getInstance().logInfo("Running " + this.getClass().getSimpleName(), false);
	}

	private void logRun() {
		StringBuilder sb = new StringBuilder();
		sb.append(systemIndex + 1);
		sb.append("/");
		sb.append(config.systemNames.size());
		sb.append(" | ");
		sb.append(systemIteration);
		sb.append("/");
		sb.append(config.systemIterations.getValue());
		sb.append(" | (");
		sb.append(algorithmIndex + 1);
		sb.append("/");
		sb.append(algorithmList.size());
		sb.append(") ");
		sb.append(algorithmList.get(algorithmIndex).getFullName());
		sb.append(" | ");
		sb.append(algorithmIteration);
		sb.append("/");
		sb.append(algorithmList.get(algorithmIndex).getIterations());
		Logger.getInstance().logInfo(sb.toString(), 2, false);
	}

	protected void logSystem() {
		StringBuilder sb = new StringBuilder();
		sb.append("Processing System: ");
		sb.append(config.systemNames.get(systemIndex));
		sb.append(" (");
		sb.append(systemIndex + 1);
		sb.append("/");
		sb.append(config.systemNames.size());
		sb.append(")");
		Logger.getInstance().logInfo(sb.toString(), 1, false);
	}

	/**
	 * Parse the parameters for the framework. For more detail see
	 * {@link ParameterParserModule}.
	 * 
	 * @param args Programm arguments
	 * @return {@link Boolean#TRUE} if the parsing went without errors.
	 */
	private boolean parseParameter(String[] args) {
		boolean value = module_ParameterParser.parseParameter(args);
		config.refreshPaths();
		return value;
	}

	protected CNF prepareModel() throws Exception {
		final String systemName = config.systemNames.get(systemIndex);

		FeatureModelReader fmReader = new FeatureModelReader();
		fmReader.setPathToModels(config.inputPath);
		IFeatureModel fm = fmReader.read(systemName);
		if (fm == null) {
			throw new NullPointerException();
		}
		systems[systemIndex] = fm;
		CNF modelCNF = new FeatureModelFormula(fm).getCNF();

		curSampleDir = config.samplesPath.resolve(systemName);
		Files.createDirectories(curSampleDir);
		final DIMACSFormatCNF format = new DIMACSFormatCNF();
		final Path fileName = curSampleDir.resolve("model." + format.getSuffix());
		if (config.storeSamples.getValue()) {
			SimpleFileHandler.save(fileName, modelCNF, format);
		}

		return modelCNF;
	}

	private void printConfigFile() {
		for (IProperty prop : SamplingConfig.getPropertyList()) {
			Logger.getInstance().logInfo(prop.toString(), 1, false);
		}
	}

	protected void randomizeConditions(List<List<ClauseList>> groupedConditions, Random random) {
		for (List<ClauseList> group : groupedConditions) {
			Collections.shuffle(group, random);
		}
		Collections.shuffle(groupedConditions, random);
	}

	public void run() {
		if (config.systemIterations.getValue() > 0) {
			Logger.getInstance().logInfo("Start", false);

			final SamplingProcessRunner processRunner = new SamplingProcessRunner();
			processRunner.setTimeout(config.timeout.getValue());

			int systemIndexEnd = config.systemNames.size();

			systemLoop: for (systemIndex = 0; systemIndex < systemIndexEnd; systemIndex++) {
				logSystem();
				try {
					// Load and prepare all algorithms that are registered in the config file
					algorithmList = module_AlgorithmLoader.loadAndPrepareAlgorithms();

					// As we start with a new system, we need to refresh the cached samples for the
					// stability calculation
					module_StabilityCalculator.prepareSystem(systemIteration, algorithmList.size());
				} catch (Exception e) {
					Logger.getInstance().logError(e);
					continue systemLoop;
				}
				algorithmIndex = 0;
				for (ASamplingAlgorithm algorithm : algorithmList) {
					if (algorithm.getIterations() < 0) {
						algorithm.setIterations(config.algorithmIterations.getValue());
					}
					algorithmIndex++;
				}
				try {
					modelCNF = prepareModel();
				} catch (Exception e) {
					Logger.getInstance().logError(e);
					continue systemLoop;
				}
				for (systemIteration = 1; systemIteration <= config.systemIterations.getValue(); systemIteration++) {
					try {
						randomizedModelCNF = adaptModel();
					} catch (Exception e) {
						Logger.getInstance().logError(e);
						continue systemLoop;
					}
					config.algorithmIterations.getValue();
					algorithmIndex = -1;
					algorithmLoop: for (ASamplingAlgorithm algorithm : algorithmList) {
						algorithmIndex++;
						for (algorithmIteration = 1; algorithmIteration <= algorithm
								.getIterations(); algorithmIteration++) {
							try {
								logRun();
								this.result = processRunner.run(algorithm);
								module_Writer.writeCSV(
										(x) -> this.module_Writer.writeData(module_Writer.getDataCSVWriter(), result));
							} catch (Exception e) {
								e.printStackTrace();
								Logger.getInstance().logError(e);
								continue algorithmLoop;
							}
						}
					}
				}
			}
			Logger.getInstance().logInfo("Finished", false);
		} else {
			Logger.getInstance().logInfo("Nothing to do", false);
		}
	}
	
	public void calculateCustomRecommendation() {
		recommender.recommend();
	}

	/**
	 * Create the directories for the different required folders.
	 * 
	 * @throws IOException
	 */
	private void setupDirectories() throws IOException {
		try {
			Files.createDirectories(config.algorithmPath);
			Files.createDirectories(config.outputPath);
			Files.createDirectories(config.csvPath);
			Files.createDirectories(config.tempPath);
			Files.createDirectories(config.logPath);
			Files.createDirectories(config.samplesPath);
			Logger.getInstance().install(config.logPath, config.verbosity.getValue());
		} catch (IOException e) {
			Logger.getInstance().logError("Could not create output directory.");
			Logger.getInstance().logError(e);
			throw e;
		}
	}
}
