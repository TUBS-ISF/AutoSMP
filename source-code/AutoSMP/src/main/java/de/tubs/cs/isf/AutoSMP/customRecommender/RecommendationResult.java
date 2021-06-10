package de.tubs.cs.isf.AutoSMP.customRecommender;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.featureide.fm.benchmark.util.Logger;

public class RecommendationResult implements Comparable<RecommendationResult>{
	
	public String benchmark = "";
	public String algorithm = "";
	public String ID = "";
	
	public double publicScore = 0;
	private int score = 0;
	
	public List<Double> sizeList= new ArrayList<Double>(); 
	public List<Double> timeList= new ArrayList<Double>();
	public List<Double> stabilityList= new ArrayList<Double>();
	public List<Double> coverageList= new ArrayList<Double>();
	
	public double size = 0; 
	public double time = 0; 
	public double stability = 0;
	public double coverage = 0;
	
	/**
	 * Constructor
	 */
	public RecommendationResult() {
		
	}
	/**
	 * Constructor
	 */
	public RecommendationResult(String id) {
		this.ID = id;
	}
	
	public void calculateScore(double minSize, double minTime, double maxCover, double maxStab) {
		double size = this.size / minSize;
		double time = this.time / minTime; 
		double coverage = this.coverage / maxCover;
		double stability = this.stability / maxStab;
		
		this.publicScore = size + time + coverage + stability;
	}
	
	public void calcualteInterScore(int sizeWeight, int timeWeight, int coverageWeigth, int stabilityWeight) {
		Logger.getInstance().logInfo("Score calculation not implemented yet", false);
		size = arithmeticMean(sizeList) * sizeWeight;
		time = arithmeticMean(timeList) * timeWeight; 
		stability = arithmeticMean(stabilityList) * stabilityWeight; 
		coverage = arithmeticMean(coverageList) * coverageWeigth;
	}
	
	private double arithmeticMean(List<Double> list) {
		double mean = 0;
		double sum = 0;
		for(double d : list) {
			sum += d;
		}
		mean = sum / list.size();
		return mean;
	}

	@Override
	public int compareTo(RecommendationResult rr) {
		double compareResult = this.publicScore - rr.publicScore;
		if(compareResult < 0) {
			return -1;
		}
		else if (compareResult == 0) {
			return 0;
		}
		else if(compareResult > 0) {
			return 1;
		}
		return 0;
	}
	
	
	
	

}
