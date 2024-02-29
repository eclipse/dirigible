/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.builders.user;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserBuilder extends AbstractCreateSqlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserBuilder.class);

    private final String userId;
    private final String password;

    public CreateUserBuilder(ISqlDialect dialect, String userId, String password) {
        super(dialect);
        this.userId = userId;
        this.password = password;
    }

    @Override
    public String generate() {
        String generated = generateCreateUserStatement(userId, password);
        logger.trace("generated: " + generated);

        return generated;
    }

    protected String generateCreateUserStatement(String user, String pass) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE USER ")
           .append(getEscapeSymbol())
           .append(user)
           .append(getEscapeSymbol())
           .append(SPACE)
           .append(" PASSWORD ")
           .append(getPasswordEscapeSymbol())
           .append(pass)
           .append(getPasswordEscapeSymbol());
        return sql.toString();
    }

    protected char getPasswordEscapeSymbol() {
        return '\'';
    }

}
