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
package org.eclipse.dirigible.components.data.structures.synchronizer.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.structures.domain.View;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The View Create Processor.
 */
public class ViewCreateProcessor {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ViewCreateProcessor.class);

  /**
   * Execute the corresponding statement.
   *
   * @param connection the connection
   * @param viewModel the view model
   * @throws SQLException the SQL exception
   */
  public static void execute(Connection connection, View viewModel) throws SQLException {
    boolean caseSensitive = Boolean.parseBoolean(Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
    String viewName = viewModel.getName();
    if (caseSensitive) {
      viewName = "\"" + viewName + "\"";
    }
    if (logger.isInfoEnabled()) {
      logger.info("Processing Create View: " + viewName);
    }
    if (!SqlFactory.getNative(connection)
                   .existsTable(connection, viewName)) {
      String sql = SqlFactory.getNative(connection)
                             .create()
                             .view(viewName)
                             .asSelect(viewModel.getQuery())
                             .build();
      if (logger.isInfoEnabled()) {
        logger.info(sql);
      }
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        statement.executeUpdate();
      } catch (SQLException e) {
        if (logger.isErrorEnabled()) {
          logger.error(sql);
        }
        if (logger.isErrorEnabled()) {
          logger.error(e.getMessage(), e);
        }
      } finally {
        if (statement != null) {
          statement.close();
        }
      }
    }
  }

}
