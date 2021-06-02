package de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;

public class FIMDC extends MSOC {

	public FIMDC() {

	}

	@Override
	public double analyze(IFeatureModelManager fm1, List<List<String>> sample1List, IFeatureModelManager fm2,
			List<List<String>> sample2List) {
		this.sample_old = SampleListToSet(sample1List);
		this.sample_new = SampleListToSet(sample2List);

		getCoreDead(1, fm1);
		getCoreDead(2, fm2);

		removeCoreDead(this.sample_old);
		removeCoreDead(this.sample_new);

		combinedFeatureSet = buildCombinedFS(fm1, fm2);

		Set<Set<String>> sample1copy = new HashSet<>(sample_old);
		Set<Set<String>> sample2copy = new HashSet<>(sample_new);

		Set<Set<String>> intersect = intersect(sample1copy, sample2copy);
		Set<Set<String>> intersect2 = new HashSet<>();
		intersect2.addAll(intersect);

		saveIdentical(intersect);
		sample_old.removeAll(intersect);

		sample_new.removeAll(intersect2);

		sampleMap_old = generateSampleMap(sample_old);
		sampleMap_new = generateSampleMap(sample_new);
		findConfigurationPairs();

		return simAgregation(pairList);
	}

	private void saveIdentical(Set<Set<String>> identicalConfs) {
		for (Set<String> s : identicalConfs) {
			pairList.add(new ConfigurationPair(0, 0, 1));
		}
	}

}
