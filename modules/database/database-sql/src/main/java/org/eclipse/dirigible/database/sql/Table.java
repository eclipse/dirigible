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
package org.eclipse.dirigible.database.sql;

import java.util.Collection;
import java.util.Objects;

/**
 * Table object containing SQL statements needed to create it.
 */
public class Table {

  private final String createTableStatement;
  private final Collection<String> createIndicesStatements;

  public Table(String createTableStatement, Collection<String> createIndicesStatements) {
    this.createTableStatement = createTableStatement;
    this.createIndicesStatements = createIndicesStatements;
  }

  public String getCreateTableStatement() {
    return createTableStatement;
  }

  public Collection<String> getCreateIndicesStatements() {
    return createIndicesStatements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Table table = (Table) o;
    return createTableStatement.equals(table.createTableStatement)
        && createIndicesStatements.equals(
        table.createIndicesStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(createTableStatement, createIndicesStatements);
  }
}
