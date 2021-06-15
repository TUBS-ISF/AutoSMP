package de.tubs.cs.isf.samplingAlgorithms.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.init.FMCoreLibrary;
import de.ovgu.featureide.fm.core.init.LibraryManager;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.cs.isf.samplingAlgorithms.util.logger.Logger;

public class FeatureModelReader {
	static {
		// Necessary call for the FeatureIDE API
		LibraryManager.registerLibrary(FMCoreLibrary.getInstance());
	}

	/** The name of the model that should be read. */
	protected String modelFileName = "model.xml";
	/** Path to the model/models. */
	protected Path pathToModels;

	/**
	 * @return The path to the location containing the model/models.
	 */
	public Path getPathToModels() {
		return pathToModels;
	}

	/**
	 * Loads a feature model from a given path pointing to a specific file.
	 * 
	 * @param path Path to the model.
	 * @return Model as {@link IFeatureModel}, otherwise null
	 */
	public IFeatureModel loadFile(final Path path) {
		final FileHandler<IFeatureModel> fh = FeatureModelManager.getFileHandler(path);
		if (fh.getLastProblems().containsError()) {
			Logger.getInstance().logInfo(fh.getLastProblems().getErrors().get(0).getMessage(), 2, true);
			return null;
		} else {
			return fh.getObject();
		}
	}

	/**
	 * Reads a feature model with the given name from the models path. The models
	 * path can be specified with {@link #setPathToModels(Path)}.
	 * 
	 * @param name Name of the feature model to read.
	 * @return A feature model from the file, otherwise null if not feature model
	 *         could be found or read.
	 */
	public final IFeatureModel read(final String name) {
		IFeatureModel fm = null;

		fm = readFromFolder(pathToModels, name);
		if (fm != null) {
			return fm;
		}

		fm = readFromFile(pathToModels, name);
		if (fm != null) {
			return fm;
		}

		fm = readFromZip(pathToModels, name);

		return fm;
	}

	/**
	 * Loads a feature model from a given path pointing to a specific file.
	 * 
	 * @param rootPath Path to a folder that contains the model.
	 * @param name     Filename of the model.
	 * @return Model as {@link IFeatureModel}, otherwise null
	 */
	public IFeatureModel readFromFile(final Path rootPath, final String name) {
		final Filter<Path> fileFilter = file -> Files.isReadable(file) && Files.isRegularFile(file)
				&& file.getFileName().toString().matches("^" + name + "\\.\\w+$");
		try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, fileFilter)) {
			final Iterator<Path> iterator = files.iterator();
			while (iterator.hasNext()) {
				Path next = iterator.next();
				Logger.getInstance().logInfo("Trying to load from file " + next, 1, true);
				IFeatureModel loadedFm = loadFile(next);
				if (loadedFm != null) {
					return loadedFm;
				}
			}
			return null;
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
		return null;
	}

	/**
	 * Loads a feature model based on a given file name from a given folder.
	 * 
	 * @param rootPath Path to a folder that contains the model.
	 * @param name     Filename of the model.
	 * @return Model as {@link IFeatureModel}, otherwise null
	 */
	public IFeatureModel readFromFolder(final Path rootPath, final String name) {
		Path modelFolder = rootPath.resolve(name);
		Logger.getInstance().logInfo("Trying to load from folder " + modelFolder, 1, true);
		if (Files.exists(modelFolder) && Files.isDirectory(modelFolder)) {
			final Path path = modelFolder.resolve(modelFileName);
			if (Files.exists(path)) {
				return loadFile(path);
			} else {
				return readFromFile(modelFolder, "model");
			}
		} else {
			return null;
		}
	}

	/**
	 * Loads a feature model from a given path pointing to a zip file containing the
	 * model.
	 * 
	 * @param rootPath Path to a zip file that contains the model.
	 * @param name     Filename of the model.
	 * @return Model as {@link IFeatureModel}, otherwise null
	 */
	protected IFeatureModel readFromZip(final Path rootPath, final String name) {
		final Filter<Path> fileFilter = file -> Files.isReadable(file) && Files.isRegularFile(file)
				&& file.getFileName().toString().matches(".*[.]zip\\Z");
		try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, fileFilter)) {
			for (Path path : files) {
				Logger.getInstance().logInfo("Trying to load from zip file " + path, 1, true);
				final URI uri = URI.create("jar:" + path.toUri().toString());
				try (final FileSystem zipFs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
					for (Path root : zipFs.getRootDirectories()) {
						IFeatureModel fm = readFromFolder(root, name);
						if (fm != null) {
							return fm;
						}
						fm = readFromFile(root, name);
						if (fm != null) {
							return fm;
						}
					}
				} catch (IOException e) {
					Logger.getInstance().logError(e);
				}
			}
		} catch (IOException e) {
			Logger.getInstance().logError(e);
		}
		return null;
	}

	/**
	 * Sets the path to load models from.
	 * 
	 * @param pathToModels Path to where the models are stored.
	 */
	public void setPathToModels(Path pathToModels) {
		this.pathToModels = pathToModels;
	}
}
