package de.tubs.cs.isf.AutoSMP;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Scanner;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.tubs.cs.isf.AutoSMP.util.FeatureModelReader;

public class ModelInfoPrinter {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Model Path:");
		String path = scanner.nextLine();
		try {
			System.out.println("\\begin{table}");
			System.out.println("\\centering");
			System.out.println("\\begin{tabular}{l|l|l|l}");
			System.out.println("\\toprule");
			System.out.println("Feature Model Name & \\#Features & \\#Constraints & Source \\\\");
			System.out.println("\\midrule");
			Files.walk(Paths.get(path))
			.filter(ModelInfoPrinter::isFile)
			.map(ModelInfoPrinter::getModel)
			.sorted(ModelInfoPrinter::sortTupleByFeatures)
			.forEach(ModelInfoPrinter::printModelInfo);
			System.out.println("\n\\bottomrule");
			System.out.println("\\end{tabular}");
			System.out.println("\\caption[SHORT]{LONG}");
			System.out.println("\\label{table:eval:data}");
			System.out.println("\\end{table}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFile(Path fm) {
		return Files.isRegularFile(fm) && fm.toString().contains(".xml");
	}
	
	public static int sortTupleByFeatures(Tuple<Path, IFeatureModel> tuple1, Tuple<Path, IFeatureModel> tuple2) {
		return tuple1.value.getNumberOfFeatures() >= tuple2.value.getNumberOfFeatures() ? 1 : -1;
	}
	
	public static int sortTupleByConstraints(Tuple<Path, IFeatureModel> tuple1, Tuple<Path, IFeatureModel> tuple2) {
		return tuple1.value.getConstraintCount() >= tuple2.value.getConstraintCount() ? 1 : -1;
	}
	
	public static Tuple<Path,IFeatureModel> getModel(Path fm) {
		FeatureModelReader reader = new FeatureModelReader();
		reader.setPathToModels(fm);
		return new Tuple<Path,IFeatureModel>(fm, FeatureModelManager.getInstance(fm).getObject());
	}
	
	public static void printModelInfo(Tuple<Path, IFeatureModel> tuple1) {
		System.out.format("\n %25s & %5s & %5s & \\\\", tuple1.key.getFileName().toString().replace(".xml", "").replace("_", "\\_"), tuple1.value.getNumberOfFeatures(), tuple1.value.getConstraintCount());
	}
}
