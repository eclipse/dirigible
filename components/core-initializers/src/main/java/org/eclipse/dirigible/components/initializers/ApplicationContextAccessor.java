package org.eclipse.dirigible.components.initializers;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * The Class ApplicationContextAccessor.
 */
@Configuration
public class ApplicationContextAccessor implements ApplicationContextAware {

	/** The application context. */
	private static ApplicationContext applicationContext;

	/**
	 * Sets the application context.
	 *
	 * @param context the new application context
	 * @throws BeansException the beans exception
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	/**
	 * Gets the application context.
	 *
	 * @return the application context
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
