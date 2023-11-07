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
package org.eclipse.dirigible.database.sql.builders.sequence;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractDropSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Drop Sequence Builder.
 */
public class DropSequenceBuilder extends AbstractDropSqlBuilder {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(DropSequenceBuilder.class);

  /** The sequence. */
  private String sequence = null;

  /**
   * Instantiates a new drop sequence builder.
   *
   * @param dialect the dialect
   * @param sequence the sequence
   */
  public DropSequenceBuilder(ISqlDialect dialect, String sequence) {
    super(dialect);
    this.sequence = sequence;
  }

  /**
   * Generate.
   *
   * @return the string
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
   */
  @Override
  public String generate() {

    StringBuilder sql = new StringBuilder();

    // DROP
    generateDrop(sql);

    // SEQUENCE
    generateSequence(sql);

    String generated = sql.toString();

    if (logger.isTraceEnabled()) {
      logger.trace("generated: " + generated);
    }

    return generated;
  }

  /**
   * Generate sequence.
   *
   * @param sql the sql
   */
  protected void generateSequence(StringBuilder sql) {
    String sequenceName = (isCaseSensitive()) ? encapsulate(this.getSequence(), true) : this.getSequence();
    sql.append(SPACE)
       .append(KEYWORD_SEQUENCE)
       .append(SPACE)
       .append(sequenceName);
  }

  /**
   * Gets the sequence.
   *
   * @return the sequence
   */
  public String getSequence() {
    return sequence;
  }

}
