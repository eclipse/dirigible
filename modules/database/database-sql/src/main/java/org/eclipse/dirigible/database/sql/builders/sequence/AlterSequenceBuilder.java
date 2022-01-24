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
package org.eclipse.dirigible.database.sql.builders.sequence;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlterSequenceBuilder extends CreateSequenceBuilder {
    private static final Logger logger = LoggerFactory.getLogger(AlterSequenceBuilder.class);

    public AlterSequenceBuilder(ISqlDialect dialect, String sequence) {
        super(dialect, sequence);
    }

    @Override
    public String generate() {
        StringBuilder sql = new StringBuilder();

        // ALTER
        generateAlter(sql);

        // SEQUENCE
        generateSequence(sql);

        // START_WITH
        generateStart(sql);

        // INCREMENT_BY
        generateIncrement(sql);

        //MAXVALUE
        generateMaxvalue(sql);

        //NO MAXVALUE
        generateNomaxvalue(sql);

        //MINVALUE
        generateMinvalue(sql);

        //NO MINVALUE
        generateNominvalue(sql);

        //CYCLE
        generateCycle(sql);

        //RESET BY
        generateResetBy(sql);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }

    @Override
    protected void generateStart(StringBuilder sql) {
        if (this.getStart() != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_RESTART_WITH, String.valueOf(this.getStart()));
        }

    }
}
