package org.eclipse.dirigible.runtime.js.nashorn.commonjs;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ISourceProvider {

	public String loadSource(String moduleId) throws IOException, URISyntaxException;

}
