/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.quartz.utils.ConnectionProvider;

public class DatasourceConnectionProvider implements ConnectionProvider {

	private DatasourceProvider datasourceProvider;

	@Override
	public Connection getConnection() throws SQLException {
		return this.datasourceProvider.getDatasource().getConnection();
	}

	@Override
	public void initialize() throws SQLException {
		if (this.datasourceProvider == null) {
			this.datasourceProvider = StaticInjector.getInjector().getInstance(DatasourceProvider.class);
		}
	}

	@Override
	public void shutdown() throws SQLException {
		//
	}

}
