package de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics;

import java.util.List;
import java.util.Set;

import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;

public class ROIC extends AStabilityMetric {

	public ROIC() {
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

		Set<Set<String>> intersect = intersect(this.sample_old, this.sample_new);
		Set<Set<String>> union = union(this.sample_old, this.sample_new);

		// Use max sample size instead of union.size
		double stability = (double) intersect.size() / Math.max(this.sample_old.size(), this.sample_new.size());

		return stability;
	}

}
