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
package org.eclipse.dirigible.api.v3.job;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;

public class JobFacade {
	
	private static ISchedulerCoreService schedulerCoreService = new SchedulerCoreService();
	
	public static String getJobs() throws SchedulerException {
		return GsonHelper.GSON.toJson(schedulerCoreService.getJobs());
	}
	
	public static String getJob(String name) throws SchedulerException {
		return GsonHelper.GSON.toJson(schedulerCoreService.getJob(name));
	}
	
}
