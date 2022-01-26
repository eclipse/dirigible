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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HanaDropSequenceBuilder extends DropSequenceBuilder {
    private static final Logger logger = LoggerFactory.getLogger(HanaDropSequenceBuilder.class);

    /**
     * Instantiates a new drop sequence builder.
     *
     * @param dialect  the dialect
     * @param sequence
     */
    public HanaDropSequenceBuilder(ISqlDialect dialect, String sequence) {
        super(dialect, sequence);
    }

    @Override
    public String generate() {

        StringBuilder sql = new StringBuilder();

        // DROP
        generateDrop(sql);

        // SEQUENCE
        generateSequence(sql);

        //RESTRICT
        generateRestrict(sql);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }

    protected void generateRestrict(StringBuilder sql) {
        String sequenceName = (isCaseSensitive()) ? encapsulate(this.getSequence()) : this.getSequence();
        sql.append(SPACE).append(KEYWORD_DATABASE_DROP_RESTRICT);
    }
}