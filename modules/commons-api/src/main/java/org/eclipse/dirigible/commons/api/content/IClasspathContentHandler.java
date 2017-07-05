package org.eclipse.dirigible.commons.api.content;

import java.util.Set;

public interface IClasspathContentHandler {
	
	public void accept(String path);
	
	public Set<String> getPaths();

}
