/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.imports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import org.eclipse.dirigible.sdk.db.Database;
import org.eclipse.dirigible.sdk.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Appends one row per import attempt to the entity's sibling <code>&lt;TABLE&gt;_IMPORTS</code>
 * table - every upload or job run, imported, refused as a duplicate or rejected, shaped
 * <code>Id, ImportName, FileName, Checksum, Records, Status, Message, ImportedAt, ImportedBy</code>.
 *
 * <p>
 * The append happens after the import's own unit of work, on its own connection, exactly as
 * {@code org.eclipse.dirigible.sdk.db.History} appends a change: a refused import has no unit to
 * join, and an imported one has already committed. A failure to append is therefore logged at ERROR
 * and does not undo the import - reporting failure for records that were saved would be the worse
 * lie.
 */
public final class ImportLedger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportLedger.class);

    private static final String LEDGER_TABLE_SUFFIX = "_IMPORTS";
    private static final int FILE_NAME_LENGTH = 255;
    private static final int MESSAGE_LENGTH = 2000;

    private ImportLedger() {}

    /**
     * Record an import attempt.
     *
     * @param table the BASE table name of the imported entity (the ledger is {@code <table>_IMPORTS})
     * @param result the attempt's outcome
     */
    public static void record(String table, ImportResult result) {
        String ledgerTable = table + LEDGER_TABLE_SUFFIX;
        String script = "INSERT INTO \"" + ledgerTable
                + "\" (\"ImportName\", \"FileName\", \"Checksum\", \"Records\", \"Status\", \"Message\", \"ImportedAt\", \"ImportedBy\")"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(script)) {
            statement.setString(1, result.getImportName());
            statement.setString(2, truncate(result.getFileName(), FILE_NAME_LENGTH));
            statement.setString(3, result.getChecksum());
            statement.setInt(4, result.getRecords());
            statement.setString(5, result.getStatus()
                                         .name());
            statement.setString(6, truncate(result.getMessage(), MESSAGE_LENGTH));
            statement.setTimestamp(7, Timestamp.from(Instant.now()));
            statement.setString(8, currentUser());
            statement.executeUpdate();
        } catch (Throwable e) {
            LOGGER.error("Cannot record the {} import of [{}] by [{}] in [{}]", result.getStatus(), result.getFileName(),
                    result.getImportName(), ledgerTable, e);
        }
    }

    private static String truncate(String value, int length) {
        return value == null || value.length() <= length ? value : value.substring(0, length);
    }

    private static String currentUser() {
        try {
            return User.getName();
        } catch (Throwable e) {
            LOGGER.warn("Cannot resolve the acting user for an import ledger entry", e);
            return null;
        }
    }
}
