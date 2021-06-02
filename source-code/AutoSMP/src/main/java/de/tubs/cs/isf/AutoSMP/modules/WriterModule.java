package de.tubs.cs.isf.AutoSMP.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.twise.TWiseConfigurationGenerator;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.twise.TWiseConfigurationTester;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.twise.test.CoverageStatistic;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.algorithms.ASamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.config.SamplingConfig;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.Sample;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.SamplingStabilityEvaluator;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.SamplingStabilityEvaluator.SampleSimilarityResult;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.process.SamplingResults;
import de.tubs.cs.isf.AutoSMP.util.CSVWriter;

/**
 * This module is responsible to create, write, and export csv files with
 * evaluation results.
 * 
 * @author Joshua Sprey
 */
public class WriterModule {

	private final SamplingConfig config;
	/**
	 * The csv writer that can be used to store the data results for our evaluation
	 */
	private CSVWriter dataCSVWriter;
	private final AutoSMP sampler;

	public WriterModule(AutoSMP sampler) {
		this.sampler = sampler;
		this.config = sampler.getConfig();
	}

	public CSVWriter getDataCSVWriter() {
		return dataCSVWriter;
	}

	public void init() {
		dataCSVWriter = new CSVWriter();
		dataCSVWriter.setAppend(true);
		dataCSVWriter.setOutputPath(config.csvPath);
		dataCSVWriter.setFileName("data.csv");
		dataCSVWriter.setKeepLines(false);
		dataCSVWriter.setHeader(Arrays.asList("Author", "AlgorithmID", "ModelID", "ModelName", "Model_Features",
				"Model_Constraints", "SystemIteration", "AlgorithmIteration", "Timeout", "InTime", "NoError", "Time",
				"Size", "T-Value", "Validity", "Valid Conditions", "Coverage", "ROIC", "MSOC", "FIMD", "ICST",
				"Runtime", "Throughput", "TotalCreatedBytes", "TotalPauseTime", "AveragePauseTime"));
		dataCSVWriter.flush();
	}

	public final void writeCSV(Consumer<CSVWriter> writing) {
		dataCSVWriter.createNewLine();
		try {
			writing.accept(dataCSVWriter);
		} catch (Exception e) {
			dataCSVWriter.resetLine();
			throw e;
		}
		dataCSVWriter.flush();
	}

	public void writeData(CSVWriter dataCSVWriter, SamplingResults result) {
		// 0. Author
		dataCSVWriter.addValue(config.author.getValue());

		// 1. First write algorithm info
		final ASamplingAlgorithm algorithm = sampler.getAlgorithmList().get(sampler.getAlgorithmIndex());
		dataCSVWriter.addValue(algorithm.getFullName());

		// 2. Write model info
		dataCSVWriter.addValue(sampler.getSystemIndex());
		dataCSVWriter.addValue(config.systemNames.get(sampler.getSystemIndex()));
		dataCSVWriter.addValue(sampler.getModelCNF().getVariables().size());
		dataCSVWriter.addValue(sampler.getRandomizedModelCNF().getClauses().size());

		// 3. Iteration info
		dataCSVWriter.addValue(sampler.getSystemIteration());
		dataCSVWriter.addValue(sampler.getAlgorithmIteration());

		// 4. Time results
		dataCSVWriter.addValue(config.timeout.getValue());
		dataCSVWriter.addValue(result.isTerminatedInTime());
		dataCSVWriter.addValue(result.isNoErrorOccured());
		dataCSVWriter.addValue(result.getSamplingTime());

		final SolutionList configurationList = result.getResult();
		if (configurationList != null) {

			// Create sample from solution list
			Sample sample = new Sample();
			for (LiteralSet config : configurationList.getSolutions()) {
				List<String> configList = new ArrayList<>(config.size());
				for (int lit : config.getLiterals()) {
					String name = sampler.getRandomizedModelCNF().getVariables().getName(lit);
					if (lit < 0) {
						configList.add("-" + name);
					} else {
						configList.add(name);
					}
				}
				sample.add(configList);
			}
			// Cache sample
			sampler.module_StabilityCalculator.cacheCurrentSample(sampler.getSystemIteration() - 1,
					sampler.getAlgorithmIndex(), sample);

			// 5. Write sample metrics
			writeSamplesInfo(dataCSVWriter, result);
			// 6. Write memory metrics
			writeMemory(dataCSVWriter, result);
			// Save sample
			if (config.storeSamples.getValue()) {
				writeSamples(config.systemNames.get(sampler.getSystemIndex()) + "_"
						+ sampler.getAlgorithmList().get(sampler.getAlgorithmIndex()) + "_"
						+ sampler.getSystemIteration() + "_" + sampler.getAlgorithmIteration(), sample);
			}
		} else {
			// Write default values
			for (int i = 0; i < 8; i++) {
				dataCSVWriter.addValue(-1);
			}
		}
	}

	public void writeMemory(CSVWriter memoryCSVWriter, SamplingResults result) {
		// Add memory data to memory
		NumberFormat df = NumberFormat.getNumberInstance(Locale.US);
		df.setGroupingUsed(false); // remove the dots grouping each 3 digits for CSV format
		df.setMaximumFractionDigits(5); // remove the fraction digits

		memoryCSVWriter.addValue(df.format(-1)); // Do not remove
		memoryCSVWriter.addValue(df.format(result.getMemoryResults().getStatisticThroughput()));
		memoryCSVWriter.addValue(df.format(result.getMemoryResults().getStatisticCreatedBytesTotal()));
		memoryCSVWriter.addValue(df.format(result.getMemoryResults().getStatisticPauseTimeTotal()));
		memoryCSVWriter.addValue(df.format(result.getMemoryResults().getStatisticPauseTimeAvg()));
	}

	public void writeSamples(final String sampleMethod, final Sample sample) {
		try {
			Files.write(sampler.getCurSampleDir().resolve(sampleMethod + ".sample"), sample.stream().map((x) -> {
				Collections.sort(x);
				return x;
			}).map(AutoSMP::toString).collect(Collectors.toList()));
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
	}

	/**
	 * Writes information about the samples (Size, Validity, Coverage Completeness)
	 * 
	 * @param dataCSVWriter writer
	 */
	public void writeSamplesInfo(CSVWriter dataCSVWriter, SamplingResults result) {
		final SolutionList configurationList = result.getResult();
		// Size
		dataCSVWriter.addValue(configurationList.getSolutions().size());
		dataCSVWriter.addValue(config.tCoverage.getValue());
		if (configurationList.getSolutions().size() > 0) {
			// Validity
			List<LiteralSet> samples = configurationList.getSolutions();
			TWiseConfigurationTester tester = new TWiseConfigurationTester(sampler.getRandomizedModelCNF());
			tester.setNodes(TWiseConfigurationGenerator
					.convertLiterals(sampler.getRandomizedModelCNF().getVariables().getLiterals()));
			tester.setT(config.tCoverage.getValue());
			tester.setSample(samples);

			Logger.getInstance().logInfo("\tTesting configuration validity...", 2, true);
			dataCSVWriter.addValue(tester.getValidity().getValidInvalidRatio());

			// Possible interactions + Completeness
			Logger.getInstance().logInfo("\tCalculating configuration coverage...", 2, true);
			CoverageStatistic coverageStat = tester.getCoverage();
			dataCSVWriter.addValue(coverageStat.getNumberOfValidConditions());
			dataCSVWriter.addValue(coverageStat.getCoverage());

			// Stability
			if (config.calculateStability.getValue().toLowerCase().equals("true")) {
				if (sampler.getSystemIndex() >= 1) {
					Sample currentSample = sampler.module_StabilityCalculator
							.getCurrentCachedSample(sampler.getSystemIteration() - 1, sampler.getAlgorithmIndex());
					Sample previousSample = sampler.module_StabilityCalculator
							.getPreviousCachedSample(sampler.getSystemIteration() - 1, sampler.getAlgorithmIndex());
					if (currentSample != null && previousSample != null) {
						IFeatureModelManager currentFM = FeatureModelManager
								.getInstance(sampler.getSystems()[sampler.getSystemIndex()]);
						IFeatureModelManager previousFM = FeatureModelManager
								.getInstance(sampler.getSystems()[sampler.getSystemIndex() - 1]);
						SamplingStabilityEvaluator core = new SamplingStabilityEvaluator(previousFM, previousSample,
								currentFM, currentSample);
						SampleSimilarityResult similarityResult = core.execut();
						NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
						nf.setGroupingUsed(false); // remove the dots grouping each 3 digits for CSV format
						nf.setMaximumFractionDigits(5); // remove the fraction digits
						dataCSVWriter.addValue(nf.format(similarityResult.resultROIC));
						dataCSVWriter.addValue(nf.format(similarityResult.resultMSOC));
						dataCSVWriter.addValue(nf.format(similarityResult.resultFIMDC));
						dataCSVWriter.addValue(nf.format(similarityResult.resultICST));
					} else {
						// Just print -1 for skipped iterations
						for (int i = 0; i < 4; i++) {
							dataCSVWriter.addValue(-1);
						}
					}
				} else {
					// Just print -1 for first iteration
					for (int i = 0; i < 4; i++) {
						dataCSVWriter.addValue(-1);
					}
				}
			} else {
				// Just print -1 for non stability mode
				for (int i = 0; i < 4; i++) {
					dataCSVWriter.addValue(-1);
				}
			}
		} else {
			for (int i = 0; i < 8; i++) {
				dataCSVWriter.addValue(-1);
			}
		}
	}
}
