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
package org.eclipse.dirigible.database.sql.builders;

import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.ISqlBuilder;
import org.eclipse.dirigible.database.sql.ISqlDialect;

/**
 * The Abstract SQL Builder.
 */
public abstract class AbstractSqlBuilder implements ISqlBuilder {

  /** The dialect. */
  private ISqlDialect dialect;


  /**
   * Instantiates a new abstract sql builder.
   *
   * @param dialect the dialect
   */
  protected AbstractSqlBuilder(ISqlDialect dialect) {
    this.dialect = dialect;
  }

  /**
   * Gets the dialect.
   *
   * @return the dialect
   */
  protected ISqlDialect getDialect() {
    return dialect;
  }

  /**
   * Usually returns the default generated snippet.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return build();
  }

  /**
   * Returns the default generated snippet.
   *
   * @return the string
   */
  @Override
  public String build() {
    return generate();
  }

  /**
   * Whether the names of tables, columns, indices are case sensitive.
   *
   * @return true if set
   */
  protected boolean isCaseSensitive() {
    return Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
  }

  /**
   * Encapsulate the name within quotes.
   *
   * @param name the name
   * @return the encapsulated name
   */
  protected String encapsulate(String name) {
    return encapsulate(name, false);
  }

  /**
   * Encapsulate the name within quotes.
   *
   * @param name the name
   * @param isDataStructureName to check if encapsulating a data structure name
   * @return the encapsulated name
   */
  protected String encapsulate(String name, boolean isDataStructureName) {
    if (name == null)
      return null;
    String escapeSymbol = getEscapeSymbol();
    if ("*".equals(name.trim())) {
      return name;
    }
    if (!name.startsWith(escapeSymbol)) {
      if (isDataStructureName || isColumn(name.trim())) {
        name = escapeSymbol + name + escapeSymbol;
      } else {
        name = encapsulateMany(name);
      }
    }
    return name;
  }

  /** The column pattern. */
  private Pattern columnPattern = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9_#$]+$");

  /**
   * Gets the escape symbol.
   *
   * @return the escape symbol
   */
  public String getEscapeSymbol() {
    return getDialect().getEscapeSymbol();
  }

  /**
   * Check whether the name is a column (one word) or it is complex expression containing functions,
   * etc. (count(*))
   *
   * @param name the name of the eventual column
   * @return true if it is one word
   */
  protected boolean isColumn(String name) {
    if (name == null) {
      return false;
    }
    return columnPattern.matcher(name)
                        .matches();
  }

  /**
   * Encapsulate all the non-function and non-numeric words.
   *
   * @param line the input string
   * @return the transformed string
   */
  protected String encapsulateMany(String line) {
    String lineWithoughContentBetweenSingleQuotes = String.join("", line.split(contentBetweenSingleQuotes.toString()));
    String regex = "([^a-zA-Z0-9_#$::']+)'*\\1*";
    String[] words = lineWithoughContentBetweenSingleQuotes.split(regex);
    Set<Set> functionsNames = getDialect().getFunctionsNames();
    for (String word : words) {
      if (isNumeric(word) || isValue(word)) {
        continue;
      }
      if (!"".equals(word.trim()) && !functionsNames.contains(word.toLowerCase())) {
        line = line.replace(word, "\"" + word + "\"");
      }
    }
    return line;
  }

  /**
   * The Regex find the content between single quotes.
   */
  private Pattern contentBetweenSingleQuotes = Pattern.compile("'([^']*?)'");

  /** The numeric pattern. */
  private Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

  /**
   * Check whether the string is a number.
   *
   * @param s the input
   * @return true if it is a number
   */
  protected boolean isNumeric(String s) {
    if (s == null) {
      return false;
    }
    return numericPattern.matcher(s)
                         .matches();
  }

  /**
   * Checks if is value.
   *
   * @param s the s
   * @return true, if is value
   */
  protected boolean isValue(String s) {
    if (s == null) {
      return false;
    }
    return s.startsWith("'") || s.endsWith("'");
  }
}
