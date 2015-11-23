package org.eclipse.dirigible.repository.ext.template;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.velocity.VelocityGenerator;

/**
 * Templating Engine implementation of {@link ITemplatingService}
 */
public class TemplatingEngine implements ITemplatingService {

	private VelocityGenerator velocityGenerator;

	/**
	 * The public constructor
	 */
	public TemplatingEngine() {
		this.velocityGenerator = new VelocityGenerator();
	}

	@Override
	public String generate(String in, Map<String, Object> parameters, String tag) throws Exception {
		return this.velocityGenerator.generate(in, parameters, tag);
	}

	@Override
	public Map<String, Object> createParameters() {
		return new HashMap<String, Object>();
	}

}
