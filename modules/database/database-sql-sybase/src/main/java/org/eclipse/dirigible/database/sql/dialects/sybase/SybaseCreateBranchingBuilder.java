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
package org.eclipse.dirigible.database.sql.dialects.sybase;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;

/**
 * The Sybase Create Branching Builder.
 */
public class SybaseCreateBranchingBuilder extends CreateBranchingBuilder {

    /**
     * Instantiates a new Sybase create branching builder.
     *
     * @param dialect the dialect
     */
    public SybaseCreateBranchingBuilder(ISqlDialect dialect) {
        super(dialect);
    }

    /**
     * Sequence.
     *
     * @param sequence the sequence
     * @return the creates the sequence builder
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#sequence(java.lang.String)
     */
    @Override
    public CreateSequenceBuilder sequence(String sequence) {
        return new SybaseCreateSequenceBuilder(this.getDialect(), sequence);
    }

}
