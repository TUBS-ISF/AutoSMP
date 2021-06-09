package de.tubs.cs.isf.AutoSMP.config.properties;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathProperty extends AProperty<Path>{

	public PathProperty(String key) {
		super(key);
	}
	
	public PathProperty(String key, String path) {
		super(key, Paths.get(path));
	}
	

	@Override
	protected Path cast(String valueString) throws Exception {
		return Paths.get(valueString);
	}
}
