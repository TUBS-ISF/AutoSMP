package de.tubs.cs.isf.AutoSMP.customRecommender;

import de.tubs.cs.isf.AutoSMP.AutoSMP;
import de.tubs.cs.isf.AutoSMP.logger.Logger;

public class Customrecommender {
	
	private AutoSMP sampler; 
	
	public Customrecommender(AutoSMP sampler) {
		this.sampler = sampler;
	}
	
	public void init() {
		Logger.getInstance().logInfo("Initializing recommender", false);
	}
	
	public void recommend() {
		Logger.getInstance().logInfo("Processing request", false);
	}
	
	

}
