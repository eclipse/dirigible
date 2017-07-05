package org.eclipse.dirigible.commons.api.content;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

public abstract class AbstractClasspathContentHandler implements IClasspathContentHandler {

	private final Set<String> resources = Collections.synchronizedSet(new HashSet<String>());
	
	@Override
	public void accept(String path) {
		if (isValid(path)) {
			resources.add(path);
			getLogger().info("Added: " + path);
		}
	}

	@Override
	public Set<String> getPaths() {
		return resources;
	}
	
	protected abstract boolean isValid(String path);
	
	protected abstract Logger getLogger();
	
}
