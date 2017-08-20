package org.eclipse.dirigible.commons.api.context;

import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scripting context facade is the centralized place where the different scripting facade providers
 * can register the request (sync) scoped objects
 */
public class ThreadContextFacade {

	private static final Logger logger = LoggerFactory.getLogger(ThreadContextFacade.class);

	private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<Map<String, Object>>();

	private static final ThreadLocal<Map<String, Object>> PROXIES = new ThreadLocal<Map<String, Object>>();

	/**
	 * Initializes the context. This has to be called at the very first (as possible) place at the service entry point
	 *
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void setUp() throws ContextException {
		CONTEXT.set(new WeakHashMap<String, Object>());
		PROXIES.set(new WeakHashMap<String, Object>());
		logger.debug("Scripting context {} has been set up", Thread.currentThread().hashCode());
	}

	/**
	 * IMPORTANT! This have to be added at the finally block to clean up objects after the execution of the service
	 *
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void tearDown() throws ContextException {
		CONTEXT.get().clear();
		CONTEXT.remove();
		PROXIES.get().clear();
		PROXIES.remove();
		logger.debug("Scripting context {} has been torn up", Thread.currentThread().hashCode());
	}

	/**
	 * Get a context scripting object
	 *
	 * @param key
	 *            the key
	 * @return the value by this key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final Object get(String key) throws ContextException {
		checkContext();
		return CONTEXT.get().get(key);
	}

	/**
	 * Set a context scripting object
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void set(String key, Object value) throws ContextException {
		checkContext();
		CONTEXT.get().put(key, value);
		logger.debug("Context object has been added to {} with key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Remove a context scripting object
	 *
	 * @param key
	 *            the key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void remove(String key) throws ContextException {
		checkContext();
		CONTEXT.get().remove(key);
		logger.debug("Context object has been removed - key {}", Thread.currentThread().hashCode(), key);
	}

	private static void checkContext() throws ContextException {
		if (CONTEXT.get() == null) {
			throw new ContextException("Context has not been initialized");
		}
	}

	/**
	 * Check whether the facade is valid
	 *
	 * @return yes, if it is valid
	 */
	public static boolean isValid() {
		return (CONTEXT.get() != null);
	}

	/**
	 * Get a proxy scripting object
	 *
	 * @param key
	 *            the key
	 * @return the value by this key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final Object getProxy(String key) throws ContextException {
		checkContext();
		return PROXIES.get().get(key);
	}

	/**
	 * Set a proxy scripting object
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void setProxy(String key, Object value) throws ContextException {
		checkContext();
		PROXIES.get().put(key, value);
		logger.debug("Proxy object has been added to {} with key {}", Thread.currentThread().hashCode(), key);
	}

	/**
	 * Remove a proxy scripting object
	 *
	 * @param key
	 *            the key
	 * @throws ContextException
	 *             in case of an error
	 */
	public static final void removeProxy(String key) throws ContextException {
		checkContext();
		PROXIES.get().remove(key);
		logger.debug("Proxy object has been removes - key {}", Thread.currentThread().hashCode(), key);
	}
}
