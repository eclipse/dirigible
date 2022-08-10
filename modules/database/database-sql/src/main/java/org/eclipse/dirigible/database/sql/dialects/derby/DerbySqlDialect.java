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
package org.eclipse.dirigible.database.sql.dialects.derby;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The Derby SQL Dialect.
 */
public class DerbySqlDialect extends
        DefaultSqlDialect<DerbySelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, DerbyCreateBranchingBuilder, DerbyAlterBranchingBuilder, DerbyDropBranchingBuilder, DerbyNextValueSequenceBuilder, DerbyLastValueIdentityBuilder> {

    /** The Constant IDENTITY_ARGUMENT. */
    private static final String IDENTITY_ARGUMENT = "GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";

    /**
     * Nextval.
     *
     * @param sequence the sequence
     * @return the derby next value sequence builder
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
     */
    @Override
    public DerbyNextValueSequenceBuilder nextval(String sequence) {
        return new DerbyNextValueSequenceBuilder(this, sequence);
    }

    /**
     * Creates the.
     *
     * @return the derby create branching builder
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
     */
    @Override
    public DerbyCreateBranchingBuilder create() {
        return new DerbyCreateBranchingBuilder(this);
    }

    /**
     * Drop.
     *
     * @return the derby drop branching builder
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
     */
    @Override
    public DerbyDropBranchingBuilder drop() {
        return new DerbyDropBranchingBuilder(this);
    }

    /**
     * Alter.
     *
     * @return the derby alter branching builder
     */
    @Override
    public DerbyAlterBranchingBuilder alter() {
        return new DerbyAlterBranchingBuilder(this);
    }

    /**
     * Select.
     *
     * @return the derby select builder
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#select()
     */
    @Override
    public DerbySelectBuilder select() {
        return new DerbySelectBuilder(this);
    }

    /**
     * Gets the identity argument.
     *
     * @return the identity argument
     */
    @Override
    public String getIdentityArgument() {
        return IDENTITY_ARGUMENT;
    }

    /**
     * Lastval.
     *
     * @param args the args
     * @return the derby last value identity builder
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
     */
    @Override
    public DerbyLastValueIdentityBuilder lastval(String... args) {
        return new DerbyLastValueIdentityBuilder(this, args);
    }

    /**
     * Gets the data type name.
     *
     * @param dataType the data type
     * @return the data type name
     */
    @Override
    public String getDataTypeName(DataType dataType) {
        switch (dataType) {
            case BIT:
                return "BOOLEAN";
            case TINYINT:
                return "SMALLINT";
            default:
                return super.getDataTypeName(dataType);
        }
    }

}
