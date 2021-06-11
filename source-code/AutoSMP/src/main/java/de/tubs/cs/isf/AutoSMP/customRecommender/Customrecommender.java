package de.tubs.cs.isf.AutoSMP.customRecommender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.util.CSVWriter;

public class Customrecommender {
	
	private final String DELIMITER = ";";
	private AutoSMP sampler; 
	/**
	 * Contains configuration options for the recommender
	 */
	public RecommendationConfig requestConfig;
	
	public List<RecommendationResult> rankedList = new ArrayList<>();
//	public Map<String, RecommendationResult> recommendationMap = new HashMap<>();
	public Map<String, Map<String,RecommendationResult>> benchmarkMap = new HashMap<>();
	
	private double minSize = Integer.MAX_VALUE;
	private double minTime = Integer.MAX_VALUE;
	private double maxCover = 0;
	private double maxStab = 0; 
	
	public Customrecommender(AutoSMP sampler) {
		this.sampler = sampler;
	}
	
	public void init() {
		Logger.getInstance().logInfo("Initializing recommender", false);
		requestConfig = new RecommendationConfig(sampler.getConfig().requestPath);
	}
	
	public void recommend() {
		Logger.getInstance().logInfo("Processing request...", false);
		
		for(String benchmark : requestConfig.benchmarks) {
			Path evaluationResults = getEvalResPath(benchmark);
			
			if(!benchmarkMap.containsKey(benchmark)) {
				Map<String,RecommendationResult> recomMap = new HashMap<String, RecommendationResult>();
				benchmarkMap.put(benchmark, recomMap);
			}
			
			try {
				readEvaluationResult(evaluationResults, benchmark);
			} catch (IOException e) {
				Logger.getInstance().logError("Error while reading evaluation results", false);
				e.printStackTrace();
			}
			
			calculateScore(benchmark);
			writeRecommendation(benchmark);
		}
		Logger.getInstance().logInfo("Sucess!!", false);
	}
	
	
	private void calculateScore(String benchmark) {
		Map<String, RecommendationResult> recomMap = benchmarkMap.get(benchmark);
		for(RecommendationResult rr : recomMap.values()) {
			rr.calcualteInterScore(requestConfig.sizeWeight, requestConfig.timeWeight, requestConfig.coverageWeight, requestConfig.stabilityWeight);
			
			this.minSize = Math.min(this.minSize, rr.size);
			this.minTime = Math.min(this.minTime, rr.time);
			this.maxCover = Math.max(this.maxCover, rr.coverage);
			this.maxStab = Math.max(this.maxStab, rr.stability);
		}
		
		for(RecommendationResult rr : recomMap.values()) {
			rr.calculateScore(minSize, minTime, maxCover, maxStab);
		}
	}

	private void writeRecommendation(String benchmark) {
		CSVWriter writer = new CSVWriter();
		Map<String, RecommendationResult> recomMap = benchmarkMap.get(benchmark);
		List<RecommendationResult> recomResults = new ArrayList<>();
		recomResults.addAll(recomMap.values());
		Collections.sort(recomResults);
		
		List<String> header = new ArrayList<>();
		header.add("Rank");
		header.add("Algorithm");
		header.add("Score");
		header.add("Size");
		header.add("Time");
		header.add("Coverage");
		header.add("Stability");
		
		writer.setOutputPath(Paths.get(requestConfig.outPut.toString() + "/" + benchmark));
		writer.setFileName("recommendation.csv");
		writer.setHeader(header);
		
		for(RecommendationResult entry : recomResults) {
			List<String> line = new ArrayList<>();
			int rank = recomResults.indexOf(entry) + 1;
			line.add(rank+"");
			line.add(entry.algorithm);
			line.add(entry.publicScore+"");
			line.add(entry.size+"");
			line.add(entry.time+"");
			line.add(entry.coverage+"");
			line.add(entry.stability+"");
			writer.addLine(line);
		}
		writer.flush();
	}

	private Path getEvalResPath(String benchmark) {
		Path evalResPath = Paths.get("");
		Path evalRoot = sampler.getConfig().csvPath;
		for(File file : evalRoot.toFile().listFiles()) {
			if(!file.isDirectory()) {
				Logger.getInstance().logInfo("File name: " + file.getName(), true);
				if(file.getName().equals(benchmark+".csv")) {
					evalResPath = file.toPath();
				}
			}
		}
		return evalResPath;
	}

	private void readEvaluationResult(Path path, String benchmark) throws IOException {
		Scanner sc = new Scanner(path.toFile()); 
		sc.useDelimiter("\n");
		boolean header = true;
		while(sc.hasNext()) {
			if(header == true) {
				sc.next();
				header = false;
			}
			else {
				parseCSVLine(sc.next(), benchmark);
			}
		}
	}
	
	private void parseCSVLine(String line, String benchmark) {
		String[] lineValues = line.split(this.DELIMITER);
		RecommendationResult rr = new RecommendationResult(); 
		
		Map<String, RecommendationResult> recomMap = new HashMap<String, RecommendationResult>(); 
		if(benchmarkMap.containsKey(benchmark)) {
			recomMap = benchmarkMap.get(benchmark);
		}
		
		String algorithm = lineValues[1];	
		double size = Double.parseDouble(lineValues[12]);
		double time = Double.parseDouble(lineValues[11]);
		double coverage = Double.parseDouble(lineValues[13]);
		double stability = Double.parseDouble(lineValues[17]);
		
		if(recomMap.keySet().contains(algorithm)) {
			rr = recomMap.get(algorithm);
		}
		else {
			rr.algorithm = algorithm; 
			rr.ID = algorithm;
		}
		
		rr.benchmark = benchmark;
		rr.sizeList.add(size);
		rr.timeList.add(time);
		rr.coverageList.add(coverage);
		rr.stabilityList.add(stability);
		
		recomMap.put(algorithm, rr);
		benchmarkMap.put(benchmark, recomMap);
	}
	
	// Debug methods
	private void debugSorting() {
		RecommendationResult r1 = new RecommendationResult("rr1");
		RecommendationResult r2 = new RecommendationResult("rr2");
		RecommendationResult r3 = new RecommendationResult("rr3");
		RecommendationResult r4 = new RecommendationResult("rr4");
		
		r1.publicScore = 50;
		r2.publicScore = 13;
		r3.publicScore = 5;
		r4.publicScore = 120;
		
		rankedList.add(r2);
		rankedList.add(r3);
		rankedList.add(r1);
		rankedList.add(r4);
		
		Logger.getInstance().logInfo("List before sorting:", true);
		for(RecommendationResult rr : rankedList) {
			Logger.getInstance().logInfo(rr.ID + " // " + rr.publicScore, true);
		}
		
		Logger.getInstance().logInfo("List after sorting:", true);
		Collections.sort(rankedList);
		for(RecommendationResult rr : rankedList) {
			Logger.getInstance().logInfo(rr.ID + " // " + rr.publicScore, true);
		}
	}
	
	

}
