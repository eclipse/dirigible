/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.publisher.api.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.publisher.api.IPublisherHandler;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsPublisherHandler implements IPublisherHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsPublisherHandler.class);
	
	private static final String EXTENSION_POINT_IDE_WORKSPACE_BEFORE_PUBLISH = "ide-workspace-before-publish";
	
	private static final String EXTENSION_POINT_IDE_WORKSPACE_AFTER_PUBLISH = "ide-workspace-after-publish";
	
	private static final String EXTENSION_POINT_IDE_WORKSPACE_BEFORE_UNPUBLISH = "ide-workspace-before-unpublish";
	
	private static final String EXTENSION_POINT_IDE_WORKSPACE_AFTER_UNPUBLISH = "ide-workspace-after-unpublish";
	
	private static final String EXTENSION_PARAMETER_PATH = "path";

	@Override
	public void beforePublish(String location) throws SchedulerException {
		triggerExtensions(location, EXTENSION_POINT_IDE_WORKSPACE_BEFORE_PUBLISH, "Before Publish");
	}

	@Override
	public void afterPublish(String workspaceLocation, String registryLocation) throws SchedulerException {
		triggerExtensions(workspaceLocation, EXTENSION_POINT_IDE_WORKSPACE_AFTER_PUBLISH, "After Publish");
	}

	@Override
	public void beforeUnpublish(String location) throws SchedulerException {
		triggerExtensions(location, EXTENSION_POINT_IDE_WORKSPACE_BEFORE_UNPUBLISH, "Before Unpublish");
	}

	@Override
	public void afterUnpublish(String location) throws SchedulerException {
		triggerExtensions(location, EXTENSION_POINT_IDE_WORKSPACE_AFTER_UNPUBLISH, "After Unpublish");
	}
	
	private void triggerExtensions(String location, String extensionPoint, String state) {
		try {
            Map<Object, Object> context = new HashMap<Object, Object>();
            context.put(EXTENSION_PARAMETER_PATH, location);
            String[] modules = ExtensionsServiceFacade.getExtensions(extensionPoint);
            for (String module : modules) {
                try {
                    logger.trace("Workspace {} Extension: {} triggered...", state, module);
                    ScriptEngineExecutorsManager.executeServiceModule("javascript", module, context);
                    logger.trace("Workspace {} Extension: {} finshed.", state, module);
                } catch (Exception | Error e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (ExtensionsException e) {
            logger.error(e.getMessage(), e);
        }
	}

}
