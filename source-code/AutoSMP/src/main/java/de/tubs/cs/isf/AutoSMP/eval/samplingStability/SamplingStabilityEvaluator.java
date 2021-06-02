package de.tubs.cs.isf.AutoSMP.eval.samplingStability;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.ConfigurationAnalyzer;
import de.ovgu.featureide.fm.core.configuration.FeatureNotFoundException;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics.FIMDC;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics.ICSTMetric;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics.MSOC;
import de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics.ROIC;

/**
 * Computes the sampling stability between two samples.
 * 
 * @author Joshua Sprey
 */
public class SamplingStabilityEvaluator {

	/**
	 * Contain the similarity results.
	 * 
	 * @author Joshua Sprey
	 */
	public class SampleSimilarityResult {
		public double resultFIMDC = -1;
		public double resultICST = -1;
		public double resultMSOC = -1;
		public double resultROIC = -1;
	}

	/** The feature model of the new samples. */
	private IFeatureModelManager fmNew;
	/** The feature model of the old samples. */
	private IFeatureModelManager fmOld;
	/** The samples of model after a change. */
	private Sample sampleNew = null;
	/** The samples of model before a change. */
	private Sample sampleOld = null;

	/**
	 * Creates a new stability evaluator.
	 * 
	 * @param fmOld     Old model.
	 * @param sampleOld Old sample.
	 * @param fmNew     New model.
	 * @param sampleNew New sample.
	 */
	public SamplingStabilityEvaluator(IFeatureModelManager fmOld, Sample sampleOld, IFeatureModelManager fmNew,
			Sample sampleNew) {
		this.fmOld = fmOld;
		this.sampleOld = sampleOld.omitNegatives();
		this.fmNew = fmNew;
		this.sampleNew = sampleNew.omitNegatives();
	}

	/**
	 * Transform the given configuration into a list a list of strings.
	 * 
	 * @param c Configuration that should be transformed.
	 * @return List containing all selected and deselected feature of a
	 *         configuration.
	 */
	private List<String> confToString(Configuration c) {
		List<String> list = new ArrayList<>();
		for (IFeature sf : c.getSelectedFeatures()) {
			list.add(sf.getName());
		}
		return list;
	}

	/**
	 * Computes all similarity metrics.
	 * 
	 * @return {@link SampleSimilarityResult} containting the results for all
	 *         metrics.
	 */
	public SampleSimilarityResult execut() {
		SampleSimilarityResult result = new SampleSimilarityResult();
		// 1) ROIC
		ROIC roic = new ROIC();
		result.resultROIC = roic.analyze(fmOld, sampleOld, fmNew, sampleNew);

		// 2) MSOC
		MSOC msoc = new MSOC();
		result.resultMSOC = msoc.analyze(fmOld, sampleOld, fmNew, sampleNew);

		// 3) FIMDC
		FIMDC fimdc = new FIMDC();
		result.resultFIMDC = fimdc.analyze(fmOld, sampleOld, fmNew, sampleNew);

		// 4) ICSTMetric
		ICSTMetric icst = new ICSTMetric();
		result.resultICST = icst.analyze(fmOld, sampleOld, fmNew, sampleNew);
		return result;
	}

	/**
	 * TODO MASTER ??
	 * 
	 * @param sample
	 * @param fm
	 * @return
	 */
	private List<List<String>> getValidConf(List<List<String>> sample, FeatureModelManager fm) {
		List<List<String>> validConfs = new ArrayList<>();

		for (List<String> c : sample) {
			try {
				Configuration conf = listToConfig(c, fm);
				ConfigurationAnalyzer analyszer = new ConfigurationAnalyzer(fm.getVariableFormula(), conf);
				if (analyszer.isValid()) {
					List<String> featureList = confToString(conf);
					validConfs.add(featureList);
				} else {
					System.out.println("Invalid Conf found");
				}
			} catch (FeatureNotFoundException fnfEx) {
				System.out.println("Feature not Found exception");
				continue;
			}

		}
		return validConfs;
	}

	/**
	 * Transforms a list of feature into a valid {@link Configuration}.
	 * 
	 * Inverse method of
	 * {@link SamplingStabilityEvaluator#confToString(Configuration)}
	 * 
	 * @param list List of feature.
	 * @param fm   The respective feature model.
	 * @return A valid configuration.
	 */
	private Configuration listToConfig(List<String> list, FeatureModelManager fm) {
		final Configuration configuration = new Configuration(fm.getVariableFormula());
		for (final String selection : list) {
			configuration.setManual(selection, Selection.SELECTED);
		}
		return configuration;
	}

}
