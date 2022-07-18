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
package org.eclipse.dirigible.database.transfer.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.commons.api.topology.ITopologicallySortable;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTransferSortableTableWrapper implements ITopologicallySortable {
	
	private static final Logger logger = LoggerFactory.getLogger(DataTransferSortableTableWrapper.class);

	private PersistenceTableModel tableModel;
	
	Map<String, DataTransferSortableTableWrapper> wrappers;
	
	public DataTransferSortableTableWrapper(PersistenceTableModel tableModel, Map<String, DataTransferSortableTableWrapper> wrappers) {
		this.tableModel = tableModel;
		this.wrappers = wrappers;
		this.wrappers.put(getId(), this);
	}

	@Override
	public String getId() {
		return tableModel.getTableName();
	}

	@Override
	public List<ITopologicallySortable> getDependencies() {
		List<ITopologicallySortable> dependencies = new ArrayList<ITopologicallySortable>();
		for (PersistenceTableRelationModel dependency: this.tableModel.getRelations()) {
			String dependencyName = dependency.getToTableName();
			if (!wrappers.containsKey(dependencyName)) {
				logger.warn("Dependency is not present in this cycle: " + dependencyName);
			} else {
				dependencies.add(wrappers.get(dependencyName));
			}
		}
		return dependencies;
	}
	
	public PersistenceTableModel getTableModel() {
		return tableModel;
	}

}
