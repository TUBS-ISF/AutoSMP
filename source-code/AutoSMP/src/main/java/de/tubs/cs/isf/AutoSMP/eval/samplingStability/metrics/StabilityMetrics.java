package de.tubs.cs.isf.AutoSMP.eval.samplingStability.metrics;

public enum StabilityMetrics {
	FIMDC("fimdc"), ICST("icst"), MSOC("msoc"), ROIC("roic");

	private String metric = "";

	private StabilityMetrics(String metric) {
		this.metric = metric;
	}

	public String getMetricName() {
		return this.metric;
	}

}
