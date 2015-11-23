package org.eclipse.dirigible.repository.ext.template;

import java.util.Map;

/**
 * The facade of the templating engine exposed thru Injected API
 */
public interface ITemplatingService {

	/**
	 * Produce a generated content based on a template and input parameters
	 *
	 * @param in
	 *            the template String
	 * @param parameters
	 *            input parameters
	 * @param tag
	 *            logging tag
	 * @return the result as String
	 * @throws Exception
	 */
	public String generate(String in, Map<String, Object> parameters, String tag) throws Exception;

	/**
	 * Utility method of creating the parameters object for use in generate() method
	 *
	 * @return parameters object
	 */
	public Map<String, Object> createParameters();
}
