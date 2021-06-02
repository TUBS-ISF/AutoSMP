package de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.ovgu.featureide.fm.core.job.monitor.ConsoleMonitor;
import de.ovgu.featureide.fm.core.job.monitor.IMonitor;

public abstract class AStabilityMetric implements IStabilityMetric {

	public static List<List<String>> checkCoreDead(IFeatureModelManager fm, IMonitor<LiteralSet> monitor) {
		List<List<String>> coreDead = new ArrayList<List<String>>();
		List<IFeature> featureDead = fm.getVariableFormula().getAnalyzer().getDeadFeatures(monitor);
		coreDead.add(featureDead.stream().map(f -> f.getName()).collect(Collectors.toList()));
		List<IFeature> featureCore = fm.getVariableFormula().getAnalyzer().getCoreFeatures(monitor);
		coreDead.add(featureCore.stream().map(f -> f.getName()).collect(Collectors.toList()));
		monitor.checkCancel();
		return coreDead;
	}

	public static Set<Set<String>> intersect(Set<Set<String>> sample1, Set<Set<String>> sample2) {
		Set<Set<String>> intersection = new HashSet<>();
		for (Set<String> conf1 : sample1) {
			for (Set<String> conf2 : sample2) {
				if (conf1.size() == conf2.size()) {
					Set<String> checkSet = new HashSet<>();
					checkSet.addAll(conf1);
					checkSet.removeAll(conf2);
					if (checkSet.size() == 0) {
						intersection.add(conf1);
						break;
					}
				}
			}
		}
		return intersection;
	}

	protected Set<String> core_new;

	protected Set<String> core_old;
	protected Set<String> dead_new;

	protected Set<String> dead_old;
	protected IMonitor<LiteralSet> monitor = new ConsoleMonitor<LiteralSet>();

	protected Set<Set<String>> sample_new;

	protected Set<Set<String>> sample_old;

	protected void getCoreDead(int olNewIdentifier, IFeatureModelManager fm) {
		List<List<String>> coreDead1 = checkCoreDead(fm, monitor);
		Set<String> core = ListToSet(coreDead1.get(1));
		Set<String> dead = ListToSet(coreDead1.get(0));

		if (olNewIdentifier == 1) {
			core_old = core;
			dead_old = dead;
		} else if (olNewIdentifier == 2) {
			core_new = core;
			dead_new = dead;
		}
	}

	protected Set<String> ListToSet(List<String> list) {
		Set<String> set = new HashSet<>();
		for (String s : list) {
			set.add(s);
		}
		return set;
	}

	protected void removeCoreDead(Set<Set<String>> sample) {
		for (Set<String> set : sample) {
			set.removeAll(core_old);
			set.removeAll(core_new);
			set.removeAll(dead_old);
			set.removeAll(dead_new);
		}
	}

	protected Set<Set<String>> SampleListToSet(List<List<String>> sample) {
		Set<Set<String>> sampleSet = new HashSet<>();
		for (List<String> conf : sample) {
			Set<String> setConf = new HashSet<String>();
			for (String f : conf) {
				setConf.add(f);
			}
			sampleSet.add(setConf);
		}
		return sampleSet;
	}

	protected Set<Set<String>> union(Set<Set<String>> sample1, Set<Set<String>> sample2) {
		Set<Set<String>> union = new HashSet<>();
		union.addAll(sample1);
		union.addAll(sample2);
		return union;
	}

}
