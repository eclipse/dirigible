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
package org.eclipse.dirigible.engine.odata2.api;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;

import java.sql.SQLException;

/**
 * The Interface ITableMetadataProvider.
 */
public interface ITableMetadataProvider {
    
    /**
     * Gets the persistence table model.
     *
     * @param oDataEntityDefinition the o data entity definition
     * @return the persistence table model
     * @throws SQLException the SQL exception
     */
    PersistenceTableModel getPersistenceTableModel(ODataEntityDefinition oDataEntityDefinition) throws SQLException;
}
