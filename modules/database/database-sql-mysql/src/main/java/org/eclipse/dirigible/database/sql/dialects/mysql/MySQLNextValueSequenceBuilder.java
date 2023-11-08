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
package org.eclipse.dirigible.database.sql.dialects.mysql;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;

/**
 * The MySQL Next Value Sequence Builder.
 */
public class MySQLNextValueSequenceBuilder extends NextValueSequenceBuilder {

    /**
     * Instantiates a new MySQL next value sequence builder.
     *
     * @param dialect the dialect
     * @param sequence the sequence
     */
    public MySQLNextValueSequenceBuilder(ISqlDialect dialect, String sequence) {
        super(dialect, sequence);
    }

    /**
     * Generate.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder#generate()
     */
    @Override
    public String generate() {
        throw new IllegalStateException("MySQL does not support Sequences");
    }
}
