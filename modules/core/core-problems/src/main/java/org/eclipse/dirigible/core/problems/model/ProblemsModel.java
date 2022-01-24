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

@Table(name = "DIRIGIBLE_PROBLEMS", uniqueConstraints=
        @UniqueConstraint(columnNames={"PROBLEM_LOCATION", "PROBLEM_TYPE", "PROBLEM_LINE", "PROBLEM_COLUMN"}))
public class ProblemsModel {

    @Id
    @GeneratedValue
    @Column(name = "PROBLEM_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @Column(name = "PROBLEM_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 512)
    private String location;

    @Column(name = "PROBLEM_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String type;

    @Column(name = "PROBLEM_LINE", columnDefinition = "VARCHAR", nullable = false, length = 11)
    private String line;

    @Column(name = "PROBLEM_COLUMN", columnDefinition = "VARCHAR", nullable = false, length = 11)
    private String column;

    @Column(name = "PROBLEM_CAUSE", columnDefinition = "VARCHAR", nullable = false, length = 1024)
    private String cause;

    @Column(name = "PROBLEM_EXPECTED", columnDefinition = "VARCHAR", nullable = false, length = 512)
    private String expected;

    @Column(name = "PROBLEM_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Timestamp createdAt;

    @Column(name = "PROBLEM_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String createdBy;

    @Column(name = "PROBLEM_CATEGORY", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String category;

    @Column(name = "PROBLEM_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String module;

    @Column(name = "PROBLEM_SOURCE", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String source;

    @Column(name = "PROBLEM_PROGRAM", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String program;

    @Column(name = "PROBLEM_STATUS", columnDefinition = "VARCHAR", nullable = false, length = 8)
    private String status;

    public ProblemsModel() {
    }

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

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getStatus() {
        return status;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(location, type, line, column, cause, expected, category, module, source, program, status);
    }
}
