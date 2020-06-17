/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.v3.platform;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.processor.PublisherProcessor;

public class LifecycleFacade {

	private static PublisherProcessor processor = StaticInjector.getInjector().getInstance(PublisherProcessor.class);

	public static boolean publish(String user, String workspace, String project) {
		boolean isSuccessfulPublishRequest = false;
		try {
			processor.requestPublishing(user, workspace, project);
			isSuccessfulPublishRequest = true;
		} catch (PublisherException e) {
			// Do nothing
		}
		return isSuccessfulPublishRequest;
	}
	
	public static boolean unpublish(String user, String workspace, String project) {
		boolean isSuccessfulUnpublishRequest = false;
		try {
			processor.requestUnpublishing(user, workspace, project);
			isSuccessfulUnpublishRequest = true;
		} catch (PublisherException e) {
			// Do nothing
		}
		return isSuccessfulUnpublishRequest;
	}

}
