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
package org.eclipse.dirigible.database.sql.dialects.mysql;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.view.CreateViewBuilder;

public class MySQLCreateViewBuilder extends CreateViewBuilder {

    private String values = null;


    public MySQLCreateViewBuilder(ISqlDialect dialect, String view) {
        super(dialect, view);
    }


    @Override
    public MySQLCreateViewBuilder asSelect(String select) {

        if (this.values != null) {
            throw new IllegalStateException("Create VIEW can use either AS SELECT or AS VALUES, but not both.");
        }
        setSelect(this.getSelectProperEscaping(this.getSelectProperEscaping(select)));
        return this;
    }

    private String getSelectProperEscaping(String select) {
        return select.replaceAll("\"", "`");
    }


}