package de.tubs.cs.isf.samplingAlgorithms.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.ovgu.featureide.fm.benchmark.util.Logger;
import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.tubs.cs.isf.samplingAlgorithms.util.FeatureModelReader;

public abstract class ASamplingAlgorithm {
	
	public String INPUTDIR = "";
	public String OUTPUTDIR = "";
	public String MODELEXT = ".xml";
	public String CONFEXT = ".config";
	private FeatureModelReader fmReader = new FeatureModelReader();
	public Random randomSeed = new Random(0);
	public int coverage = 2;
	public int maxSampleSize = 1000;
	
	public CNF modelCNF = null;
	public CNF randomCNF = null;
	public IFeatureModel fm = null;
	public List<LiteralSet> sample = new ArrayList<>();
	
	
	public ASamplingAlgorithm() {
		
	}
	
	public abstract void runSampling();
	public abstract void init(String in, String out, String coverage, String maxSize, String rSeed);
	
	public void initializeUtils() {
		
	}
	
	public void loadFeatureFM() {
		try {
			fm = fmReader.loadFile(Paths.get(INPUTDIR));
			modelCNF = new FeatureModelFormula(fm).getCNF();
			randomCNF = modelCNF.randomize(randomSeed);
		}
		catch(NullPointerException npe) {
			Logger.getInstance().logError("Nullpointer for Path: " + INPUTDIR);
			Logger.getInstance().logError(npe.toString());
		}
	}
	
	public void writeSample() {
		List<Configuration> configs = new ArrayList<>();
		FeatureModelFormula fmForm = new FeatureModelFormula(fm);
		
		for (LiteralSet literalSet : sample) {
			Configuration configuration = new Configuration(fmForm);

			for (final int selection : literalSet.getLiterals()) {
				final String name = modelCNF.getVariables().getName(selection);
				configuration.setManual(name, selection > 0 ? Selection.SELECTED : Selection.UNSELECTED);
			}
			configs.add(configuration);
		}
		
		int counter = 0;
		for (Configuration conf : configs) {
			String fileName = "";
			if (counter >= 0 && counter < 10) {
				fileName = "000" + counter + CONFEXT;
			} else if (counter >= 10 && counter < 100) {
				fileName = "00" + counter + CONFEXT;
			} else if (counter >= 100 && counter < 1000) {
				fileName = "0" + counter + CONFEXT;
			} else {
				fileName = counter + CONFEXT;
			}

			StringBuilder configBuilder = new StringBuilder();
			for (String feature : conf.getSelectedFeatureNames()) {
				System.out.println(feature);
				if (configBuilder.length() != 0) {
					configBuilder.append("\n");
				}
				configBuilder.append(feature);
			}
			try {
				File configurationDir = new File(OUTPUTDIR);
				configurationDir.mkdirs();
				File configurationFile = new File(configurationDir + "/" + fileName);
				if (!configurationFile.exists()) {
					configurationFile.createNewFile();
				}

				FileWriter fw = new FileWriter(configurationFile);
				BufferedWriter bw = new BufferedWriter(fw);

				bw.write(configBuilder.toString());
				bw.flush();
				bw.close();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
			counter++;
		}
	}

}
