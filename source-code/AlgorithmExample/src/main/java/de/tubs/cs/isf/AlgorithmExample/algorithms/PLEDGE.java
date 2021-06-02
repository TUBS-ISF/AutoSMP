package de.tubs.cs.isf.AlgorithmExample.algorithms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet.Order;
import de.ovgu.featureide.fm.core.analysis.cnf.SolutionList;
import de.ovgu.featureide.fm.core.analysis.cnf.Variables;
import de.tubs.cs.isf.AutoSMP.algorithms.AJavaMemoryTWiseSamplingAlgorithm;

public class PLEDGE extends AJavaMemoryTWiseSamplingAlgorithm {
	private long numberOfConfigurations = 10;

	public PLEDGE(Path fmFile, Path outputFile, int t, Path gcCollectorPath, String minimumMemoryAllocation, String maximumMemoryAllocation) {
		super(fmFile, outputFile, t, gcCollectorPath, minimumMemoryAllocation, maximumMemoryAllocation);
	}

	@Override
	protected void addCommandElements() {
		addCommandElement("-cp");
		addCommandElement("algos/tools/PLEDGE/*");
		addCommandElement("pledge.Main");
		addCommandElement("generate_products");
		addCommandElement("-dimacs");
		addCommandElement("-fm");
		addCommandElement(fmFile.toString());
		addCommandElement("-o");
		addCommandElement(outputFile.toString());
		addCommandElement("-timeAllowedMS");
		addCommandElement(Long.toString(86400000));
		addCommandElement("-nbProds");
		addCommandElement(Long.toString(numberOfConfigurations));
	}

	@Override
	public String getName() {
		return "Pledge";
	}


	public long getNumberOfConfigurations() {
		return numberOfConfigurations;
	}


	@Override
	public String getParameterSettings() {
		return "";
	}

	@Override
	public SolutionList parseResults() throws IOException {
		if (!Files.isReadable(outputFile)) {
			return null;
		}

		List<String> lines = Files.readAllLines(outputFile);
		if (lines.isEmpty()) {
			return null;
		}

		final Pattern variableNamePattern = Pattern.compile("\\A\\d+->(.*)\\Z");

		final ArrayList<String> featureNames = new ArrayList<>();
		final ListIterator<String> it = lines.listIterator();
		while (it.hasNext()) {
			final String line = it.next().trim();
			Matcher matcher = variableNamePattern.matcher(line);
			if (matcher.matches()) {
				featureNames.add(matcher.group(1));
			} else {
				it.previous();
				break;
			}
		}
		final Variables variables = new Variables(featureNames);

		final ArrayList<LiteralSet> configurationList = new ArrayList<>();
		while (it.hasNext()) {
			final String line = it.next().trim();
			int[] configurationArray = new int[variables.size()];
			String[] featureSelections = line.split(";");
			for (int i = 0; i < configurationArray.length; i++) {
				configurationArray[i] = Integer.parseInt(featureSelections[i]);
			}
			configurationList.add(new LiteralSet(configurationArray, Order.INDEX));
		}
		return new SolutionList(variables, configurationList);
	}

	public void setNumberOfConfigurations(long numberOfConfigurations) {
		this.numberOfConfigurations = numberOfConfigurations;
	}

}