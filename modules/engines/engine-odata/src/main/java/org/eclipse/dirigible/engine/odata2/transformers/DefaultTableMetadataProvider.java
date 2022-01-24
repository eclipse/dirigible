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
package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.api.ITableMetadataProvider;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;

import java.sql.SQLException;

public class DefaultTableMetadataProvider implements ITableMetadataProvider {

    private DBMetadataUtil dbMetadataUtil = new DBMetadataUtil();

    public PersistenceTableModel getPersistenceTableModel(ODataEntityDefinition oDataEntityDefinition) throws SQLException {
        return dbMetadataUtil.getTableMetadata(oDataEntityDefinition.getTable(), dbMetadataUtil.getOdataArtifactTypeSchema(oDataEntityDefinition.getTable()));
    }
}
