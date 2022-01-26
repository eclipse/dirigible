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
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Sequence Builder.
 */
public class CreateSequenceBuilder extends AbstractCreateSqlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CreateSequenceBuilder.class);

    private String sequence;

    private Integer start;

    private Integer increment;

    private Integer maxvalue;

    private Boolean nomaxvalue;

    private Integer minvalue;

    private Boolean nominvalue;

    private Boolean cycles;

    private String resetBy;

    private Boolean publicc;

    public CreateSequenceBuilder(ISqlDialect dialect, String sequence) {
        super(dialect);
        this.sequence = sequence;
    }

    /**
     * Gets the sequence.
     *
     * @return the sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Gets the increment.
     *
     * @return the increment
     */
    public Integer getIncrement() {
        return increment;
    }

    public CreateSequenceBuilder start(Integer start) {
        logger.trace("start: " + start);
        this.start = start;
        return this;
    }

    /**
     * Increment.
     *
     * @param increment the increment
     * @return the creates the sequence builder
     */
    public CreateSequenceBuilder increment(int increment) {
        logger.trace("increment: " + increment);
        this.increment = increment;
        return this;
    }

    public CreateSequenceBuilder maxvalue(Integer maxvalue) {
        logger.trace("maxvalue: " + maxvalue);
        this.maxvalue = maxvalue;
        return this;
    }

    public CreateSequenceBuilder nomaxvalue(Boolean nomaxvalue) {
        logger.trace("nomaxvalue: " + nomaxvalue);
        this.nomaxvalue = nomaxvalue;
        return this;
    }

    public CreateSequenceBuilder minvalue(Integer minvalue) {
        logger.trace("minvalue: " + minvalue);
        this.minvalue = minvalue;
        return this;
    }

    public CreateSequenceBuilder nominvalue(Boolean nominvalue) {
        logger.trace("nominvalue: " + nominvalue);
        this.nominvalue = nominvalue;
        return this;
    }

    public CreateSequenceBuilder cycles(Boolean cycles) {
        logger.trace("cycles: " + cycles);
        this.cycles = cycles;
        return this;
    }

    public CreateSequenceBuilder resetBy(String resetBy) {
        logger.trace("resetBy: " + resetBy);
        this.resetBy = resetBy;
        return this;
    }

    public CreateSequenceBuilder publicc(Boolean publicc) {
        logger.trace("public: " + publicc);
        this.publicc = publicc;
        return this;
    }

    @Override
    public String generate() {
        StringBuilder sql = new StringBuilder();

        // CREATE
        generateCreate(sql);

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

    /**
     * Generate sequence.
     *
     * @param sql the sql
     */
    protected void generateSequence(StringBuilder sql) {
        String sequenceName = (isCaseSensitive()) ? encapsulate(this.sequence) : this.sequence;
        sql.append(SPACE).append(KEYWORD_SEQUENCE).append(SPACE).append(sequenceName);
    }


    protected void generateStart(StringBuilder sql) {
        if (this.start != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_START_WITH, String.valueOf(this.start));
        }

    }

    protected void generateIncrement(StringBuilder sql) {
        if (this.increment != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_INCREMENT_BY, String.valueOf(this.increment));
        }
    }

    protected void generateMaxvalue(StringBuilder sql) {
        if (this.maxvalue != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_MAXVALUE, String.valueOf(this.maxvalue));
        }

    }

    protected void generateNomaxvalue(StringBuilder sql) {
        if ((this.nomaxvalue != null && this.nomaxvalue)) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_NO_MAXVALUE, KEYWORD_SEQUENCE_EMPTY_STRING_FOR_BOOLEAN_TYPES);
        }

    }

    protected void generateMinvalue(StringBuilder sql) {
        if (this.minvalue != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_MINVALUE, String.valueOf(this.minvalue));
        }
    }

    protected void generateNominvalue(StringBuilder sql) {
        if ((this.nominvalue != null && this.nominvalue)) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_NO_MINVALUE, KEYWORD_SEQUENCE_EMPTY_STRING_FOR_BOOLEAN_TYPES);
        }
    }

    protected void generateCycle(StringBuilder sql) {
        if (this.cycles != null && this.cycles) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_CYCLE, KEYWORD_SEQUENCE_EMPTY_STRING_FOR_BOOLEAN_TYPES);
        }

    }

    protected void generateResetBy(StringBuilder sql) {
        if (this.resetBy != null) {
            generateSequenceParameter(sql, KEYWORD_SEQUENCE_RESET_BY, String.valueOf(this.resetBy));
        }

    }

    protected void generateSequenceParameter(StringBuilder sql, String parameterName, String parameterValue) {
        sql.append(SPACE)
                .append(parameterName)
                .append(SPACE)
                .append(parameterValue);
    }

}
