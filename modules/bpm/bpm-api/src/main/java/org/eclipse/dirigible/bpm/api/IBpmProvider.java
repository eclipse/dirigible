package org.eclipse.dirigible.bpm.api;

/**
 * The Business Process Management interface implemented by all the BPM providers
 *
 */
public interface IBpmProvider {

	public static final String DIRIGIBLE_BPM_PROVIDER = "DIRIGIBLE_BPM_PROVIDER"; //$NON-NLS-1$

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();

	/**
	 * Getter for the underlying process engine object
	 * 
	 * @return the process engine
	 */
	public Object getProcessEngine();

}
