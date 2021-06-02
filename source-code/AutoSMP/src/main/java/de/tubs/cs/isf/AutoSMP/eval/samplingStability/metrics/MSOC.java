package de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;

public class MSOC extends AStabilityMetric {

	protected Set<String> combinedFeatureSet;
	protected List<ConfigurationPair> pairList = new ArrayList<>();

	protected HashMap<Integer, Set<String>> sampleMap_new;

	protected HashMap<Integer, Set<String>> sampleMap_old;

	public MSOC() {

	}

	@Override
	public double analyze(IFeatureModelManager fm1, List<List<String>> sample1List, IFeatureModelManager fm2,
			List<List<String>> sample2List) {
		// transform sample lists to sets
		this.sample_old = SampleListToSet(sample1List);
		this.sample_new = SampleListToSet(sample2List);
		// build combined feature set
		combinedFeatureSet = buildCombinedFS(fm1, fm2);
		// find core and dead features for the feature models
		getCoreDead(1, fm1);
		getCoreDead(2, fm2);
		// remove core and dead features from input samples
		removeCoreDead(sample_old);
		removeCoreDead(sample_new);
		// transform set of sets to map of sets
		this.sampleMap_old = generateSampleMap(sample_old);
		this.sampleMap_new = generateSampleMap(sample_new);

		findConfigurationPairs();

		return simAgregation(pairList);
	}

	protected Set<String> buildCombinedFS(IFeatureModelManager fm1, IFeatureModelManager fm2) {

		Set<String> fs1 = ListToSet(fm1.getVarObject().getFeatureOrderList());
		Set<String> fs2 = ListToSet(fm2.getVarObject().getFeatureOrderList());

		return Sets.union(fs1, fs2);
	}

	protected double calcConfSim(Set<String> conf1, Set<String> conf2) {

		Set<String> difFSconf1 = Sets.difference(combinedFeatureSet, conf1);
		Set<String> difFSconf2 = Sets.difference(combinedFeatureSet, conf2);
		double absConfIntersec = Sets.intersection(conf1, conf2).size();
		double absDifIntersec = Sets.intersection(difFSconf1, difFSconf2).size();

		return (absConfIntersec + absDifIntersec) / combinedFeatureSet.size();
	}

	protected void findConfigurationPairs() {
		Set<Integer> keySet1 = new HashSet<>();
		keySet1.addAll(sampleMap_old.keySet());

		Set<Integer> keySet2 = new HashSet<>();
		keySet2.addAll(sampleMap_new.keySet());

		for (int key1 : keySet1) {
			double maxSimilarity = 0;
			ConfigurationPair confPair = new ConfigurationPair();
			for (int key2 : keySet2) {
				double confSim = calcConfSim(sampleMap_old.get(key1), sampleMap_new.get(key2));
				if (confSim > maxSimilarity) {
					maxSimilarity = confSim;
					confPair = new ConfigurationPair(key1, key2, confSim);
				}
			}
			keySet2.remove(confPair.getKey2());
			this.pairList.add(confPair);
		}

		for (ConfigurationPair pair : pairList) {
			keySet1.remove(pair.getKey1());
		}

		if (!keySet1.isEmpty()) {
			for (int key : keySet1) {
				this.pairList.add(new ConfigurationPair(key, 0, 0));
			}
		}
		if (!keySet2.isEmpty()) {
			for (int key : keySet2) {
				this.pairList.add(new ConfigurationPair(0, key, 0));
			}
		}
	}

	protected HashMap<Integer, Set<String>> generateSampleMap(Set<Set<String>> sample) {
		HashMap<Integer, Set<String>> map = new HashMap<>();
		int index = 1;
		for (Set<String> conf : sample) {
			Integer key = index++;
			map.put(key, conf);
		}
		return map;
	}

	protected double simAgregation(List<ConfigurationPair> pairList) {
		double simSum = 0;
		for (ConfigurationPair pair : pairList) {
			simSum += pair.getSimilarity();
		}

		return simSum / pairList.size();
	}

}
