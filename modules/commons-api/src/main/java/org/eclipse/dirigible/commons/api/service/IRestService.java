package org.eclipse.dirigible.commons.api.service;

/**
 * The interface for all the front facing REST (sync) services
 *
 */
public interface IRestService {

	public Class<? extends IRestService> getType();
}
