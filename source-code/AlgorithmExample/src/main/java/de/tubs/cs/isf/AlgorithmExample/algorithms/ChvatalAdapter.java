package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.ovgu.featureide.fm.core.analysis.cnf.Variables;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet.Order;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.tubs.cs.isf.AutoSMP.algorithms.AJavaMemoryTWiseSamplingAlgorithm;
import de.tubs.cs.isf.AutoSMP.logger.Logger;
import de.tubs.cs.isf.AutoSMP.util.FeatureModelReader;

public class ChvatalAdapter extends AJavaMemoryTWiseSamplingAlgorithm {
	
	private FeatureModelReader fmReader = new FeatureModelReader();
	private String outPutPath = "";

	public ChvatalAdapter(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation,
			String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}
	
	@Override
	protected void addCommandElements() {
		// initializing jar execution
		addCommandElement("-jar");
		// path to jar file
		addCommandElement("A:/210_Research/206_AutoSMP_SRC_Repo/algorithms/tools/chvatal.jar");
		// path to feature model
		Logger.getInstance().logInfo("Path of model file: " + getPathOfModelFile(), false);
		addCommandElement(getPathOfModelFile().toString());
//		addCommandElement("A:\\210_Research\\206_AutoSMP_SRC_Repo\\benchmarks\\Test_Car\\Car.xml");
		// path to output directory
		this.outPutPath = getPathOfOutputFile().toString() + "_" + this.getName() +"_" + "t" + getT();
		Logger.getInstance().logInfo("Path of outpur dir: " + this.outPutPath, false);
		
		addCommandElement(this.outPutPath);
//		addCommandElement("A:\\210_Research\\206_AutoSMP_SRC_Repo\\output\\samples\\TestCar");
		// t-wise coverage
		addCommandElement(getT()+"");
		// max sample size
		addCommandElement("1000");
		// random seed
		addCommandElement("100");
	}

	@Override
	public String getName() {
		return "Chvatal";
	}

	@Override
	public String getParameterSettings() {
		return "t="+t;
	}

	@Override
	public SolutionList parseResults() throws IOException {
		Logger.getInstance().logInfo("parsing solution list", false);
		IFeatureModel fm = fmReader.loadFile(getPathOfModelFile());
		FeatureModelFormula fmForm = new FeatureModelFormula(fm); 
		CNF modelCNF = new FeatureModelFormula(fm).getCNF();
		
		int numberOfConfigurations = 0;
		int numberOfFeatures = 0;
		List<String> featureNames = new ArrayList<>();
		
		List<Configuration> configs = new ArrayList<>();
		Logger.getInstance().logInfo("outPath: " + outPutPath, false);
		
		// read the configurations from file
		try {
			DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(outPutPath));
			for(Path filePath : dirStream) {
				Configuration config = new Configuration(fmForm);
				// set all features of the feature model as unselected
				for(IFeature feature : fm.getFeatures()) {
					config.setManual(feature.getName(), Selection.UNSELECTED);
				}
				Logger.getInstance().logInfo("Config Name: " + filePath.getFileName(), true);
				File persConfig = filePath.toFile();
				Scanner scanner = new Scanner(new BufferedReader(new FileReader(persConfig)));
				// set features of the persistent configuration as selected
				while(scanner.hasNext()) {
					config.setManual(scanner.next(), Selection.SELECTED);
				}
				Logger.getInstance().logInfo("Selected Features: " + config.getSelectedFeatureNames().toString(), true);
				Logger.getInstance().logInfo("Unselected Features: " + config.getUnSelectedFeatures().toString(), true);
				configs.add(config);
			}
		}
		catch(IOException | DirectoryIteratorException x) {
		}
		
/////////// creating a solutionList
		
		// get configuration number
		numberOfConfigurations = configs.size();
		// get feature number
		numberOfFeatures = fm.getFeatures().size();
		// get feature names from fm
		for(IFeature feature : fm.getFeatures()) {
			featureNames.add(feature.getName());
		}
		// create variables 
		final Variables variables = new Variables(featureNames);
		// create int arry configurations
		List<int[]> intArrayConfigs = new ArrayList<>();
		for(Configuration conf : configs) {
			int[] intArrayConf = new int[variables.size()];
			for(String feature : featureNames) {
				int variable = variables.getVariable(feature);
				int variableIndex = variable - 1;
				// check if feature is selected of deselected; set it in intArrayConf respectively
				if(conf.getSelectedFeatureNames().contains(feature)) {
					intArrayConf[variableIndex] = variable;
				}
				else {
					intArrayConf[variableIndex] = -variable;
				}
			}
			intArrayConfigs.add(intArrayConf);
		}
		
		// create list of literal sets
		final ArrayList<LiteralSet> literalSetConfigs = new ArrayList<>(numberOfConfigurations);
		for (int[] configuration : intArrayConfigs) {
			literalSetConfigs.add(new LiteralSet(configuration, Order.INDEX));
		}
		
		return new SolutionList(variables, literalSetConfigs);
	}

}
