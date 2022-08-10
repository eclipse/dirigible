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
package org.eclipse.dirigible.core.problems.model;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The Class ProblemsModel.
 */
@Table(name = "DIRIGIBLE_PROBLEMS", uniqueConstraints=
        @UniqueConstraint(columnNames={"PROBLEM_LOCATION", "PROBLEM_TYPE", "PROBLEM_LINE", "PROBLEM_COLUMN"}))
public class ProblemsModel {

    /** The id. */
    @Id
    @GeneratedValue
    @Column(name = "PROBLEM_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /** The location. */
    @Column(name = "PROBLEM_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 512)
    private String location;

    /** The type. */
    @Column(name = "PROBLEM_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String type;

    /** The line. */
    @Column(name = "PROBLEM_LINE", columnDefinition = "VARCHAR", nullable = false, length = 11)
    private String line;

    /** The column. */
    @Column(name = "PROBLEM_COLUMN", columnDefinition = "VARCHAR", nullable = false, length = 11)
    private String column;

    /** The cause. */
    @Column(name = "PROBLEM_CAUSE", columnDefinition = "VARCHAR", nullable = false, length = 1024)
    private String cause;

    /** The expected. */
    @Column(name = "PROBLEM_EXPECTED", columnDefinition = "VARCHAR", nullable = false, length = 512)
    private String expected;

    /** The created at. */
    @Column(name = "PROBLEM_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Timestamp createdAt;

    /** The created by. */
    @Column(name = "PROBLEM_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String createdBy;

    /** The category. */
    @Column(name = "PROBLEM_CATEGORY", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String category;

    /** The module. */
    @Column(name = "PROBLEM_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String module;

    /** The source. */
    @Column(name = "PROBLEM_SOURCE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String source;

    /** The program. */
    @Column(name = "PROBLEM_PROGRAM", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String program;

    /** The status. */
    @Column(name = "PROBLEM_STATUS", columnDefinition = "VARCHAR", nullable = false, length = 8)
    private String status;

    /**
     * Instantiates a new problems model.
     */
    public ProblemsModel() {
    }

    /**
     * Instantiates a new problems model.
     *
     * @param location the location
     * @param type the type
     * @param line the line
     * @param column the column
     * @param cause the cause
     * @param expected the expected
     * @param category the category
     * @param module the module
     * @param source the source
     * @param program the program
     */
    public ProblemsModel(String location,
                         String type,
                         String line,
                         String column,
                         String cause,
                         String expected,
                         String category,
                         String module,
                         String source,
                         String program) {
        this.location = location;
        this.type = type;
        this.line = line;
        this.column = column;
        this.cause = cause;
        this.expected = expected;
        this.category = category;
        this.module = module;
        this.source = source;
        this.program = program;
    }

    /**
     * Instantiates a new problems model.
     *
     * @param location the location
     * @param type the type
     * @param line the line
     * @param column the column
     * @param cause the cause
     * @param expected the expected
     * @param category the category
     * @param module the module
     * @param source the source
     * @param program the program
     * @param status the status
     */
    public ProblemsModel(String location,
                         String type,
                         String line,
                         String column,
                         String cause,
                         String expected,
                         String category,
                         String module,
                         String source,
                         String program,
                         String status) {
        this.location = location;
        this.type = type;
        this.line = line;
        this.column = column;
        this.cause = cause;
        this.expected = expected;
        this.category = category;
        this.module = module;
        this.source = source;
        this.program = program;
        this.status = status;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param location the new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the line.
     *
     * @return the line
     */
    public String getLine() {
        return line;
    }

    /**
     * Sets the line.
     *
     * @param line the new line
     */
    public void setLine(String line) {
        this.line = line;
    }

    /**
     * Gets the column.
     *
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * Sets the column.
     *
     * @param column the new column
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * Gets the cause.
     *
     * @return the cause
     */
    public String getCause() {
        return cause;
    }

    /**
     * Sets the cause.
     *
     * @param cause the new cause
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * Gets the expected.
     *
     * @return the expected
     */
    public String getExpected() {
        return expected;
    }

    /**
     * Sets the expected.
     *
     * @param expected the new expected
     */
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     * Gets the created at.
     *
     * @return the created at
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     *
     * @param createdAt the new created at
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy the new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the module.
     *
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * Sets the module.
     *
     * @param module the new module
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the program.
     *
     * @return the program
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the program.
     *
     * @param program the new program
     */
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * To json.
     *
     * @return the string
     */
    public String toJson() {
        return GsonHelper.GSON.toJson(this);
    }

    /**
     * Equals.
     *
     * @param o the o
     * @return true, if successful
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemsModel that = (ProblemsModel) o;
        return location.equals(that.location)
                && type.equals(that.type)
                && line.equals(that.line)
                && column.equals(that.column)
                && cause.equals(that.cause)
                && expected.equals(that.expected)
                && category.equals(that.category)
                && module.equals(that.module)
                && source.equals(that.source)
                && program.equals(that.program);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(location, type, line, column, cause, expected, category, module, source, program, status);
    }
}
