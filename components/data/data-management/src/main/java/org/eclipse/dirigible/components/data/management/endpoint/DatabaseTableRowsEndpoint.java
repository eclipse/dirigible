/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.management.endpoint;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.components.data.management.service.DatabaseTableRowsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.annotation.security.RolesAllowed;

/**
 * The row editing of the Database perspective's result view: list a table's rows, and insert,
 * update or delete one - answered without running a script.
 *
 * <p>
 * It is served at the script URL that used to answer it, so the view keeps calling what it called.
 * A literal mapping wins over the JavaScript endpoint's pattern, and the script itself is gone. The
 * table name in the path is Base64-encoded, as the view encodes it. A write takes the row's column
 * values under {@code data} and, to update or delete, the primary key columns under
 * {@code primaryKey}; it answers {@code {"success": true}}. A request that fails is answered 400
 * with an {@code error} and, when the database refused it, its {@code details}.
 */
@RestController
@RequestMapping("/services/js/view-databases/js/databaseTable.js/{datasource}/{schema}/{table}")
@RolesAllowed({"ADMINISTRATOR", "DEVELOPER", "OPERATOR"})
class DatabaseTableRowsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTableRowsEndpoint.class);

    private static final String DATA = "data";

    private static final String PRIMARY_KEY = "primaryKey";

    private static final Map<String, Object> SUCCESS = Map.of("success", true);

    private final DatabaseTableRowsService rowsService;

    /**
     * Instantiates a new database table rows endpoint.
     *
     * @param rowsService the database table rows service
     */
    DatabaseTableRowsEndpoint(DatabaseTableRowsService rowsService) {
        this.rowsService = rowsService;
    }

    @GetMapping
    ResponseEntity<Object> findAll(@PathVariable("datasource") String datasource, @PathVariable("schema") String schema,
            @PathVariable("table") String table) {
        try {
            String rows = rowsService.findAll(datasource, schema, decode(table));
            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(rows);
        } catch (Exception e) {
            return failed("Failed to fetch rows", datasource, schema, table, e);
        }
    }

    @PostMapping
    ResponseEntity<Object> insert(@PathVariable("datasource") String datasource, @PathVariable("schema") String schema,
            @PathVariable("table") String table, @RequestBody(required = false) String body) {
        Optional<JsonObject> row = parse(body).flatMap(DatabaseTableRowsEndpoint::row);
        if (row.isEmpty()) {
            return required("Required 'data'");
        }
        try {
            rowsService.insert(datasource, schema, decode(table), row.get());
            return ResponseEntity.ok(SUCCESS);
        } catch (Exception e) {
            return failed("Failed to create row", datasource, schema, table, e);
        }
    }

    @PutMapping
    ResponseEntity<Object> update(@PathVariable("datasource") String datasource, @PathVariable("schema") String schema,
            @PathVariable("table") String table, @RequestBody(required = false) String body) {
        Optional<JsonObject> request = parse(body);
        Optional<JsonObject> row = request.flatMap(DatabaseTableRowsEndpoint::row);
        Optional<Set<String>> primaryKey = request.flatMap(DatabaseTableRowsEndpoint::primaryKey);
        if (row.isEmpty() || primaryKey.isEmpty()) {
            return required("Required 'schemaName', 'tableName', 'data' and 'primaryKey'");
        }
        try {
            rowsService.update(datasource, schema, decode(table), row.get(), primaryKey.get());
            return ResponseEntity.ok(SUCCESS);
        } catch (Exception e) {
            return failed("Failed to update row", datasource, schema, table, e);
        }
    }

    @DeleteMapping
    ResponseEntity<Object> delete(@PathVariable("datasource") String datasource, @PathVariable("schema") String schema,
            @PathVariable("table") String table, @RequestBody(required = false) String body) {
        Optional<JsonObject> request = parse(body);
        Optional<JsonObject> row = request.flatMap(DatabaseTableRowsEndpoint::row);
        Optional<Set<String>> primaryKey = request.flatMap(DatabaseTableRowsEndpoint::primaryKey);
        if (row.isEmpty() || primaryKey.isEmpty()) {
            return required("Required 'data', and 'primaryKey'");
        }
        try {
            rowsService.delete(datasource, schema, decode(table), row.get(), primaryKey.get());
            return ResponseEntity.ok(SUCCESS);
        } catch (Exception e) {
            return failed("Failed to delete row", datasource, schema, table, e);
        }
    }

    /**
     * Decodes the table name. The view encodes it with the browser's {@code btoa}, whose input is one
     * byte per character, so the bytes are read back as ISO-8859-1.
     */
    private static String decode(String table) {
        return new String(Base64.getDecoder()
                                .decode(table),
                StandardCharsets.ISO_8859_1);
    }

    private static Optional<JsonObject> parse(String body) {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonElement request = JsonParser.parseString(body);
            return request.isJsonObject() ? Optional.of(request.getAsJsonObject()) : Optional.empty();
        } catch (JsonParseException e) {
            LOGGER.debug("The request body is not JSON", e);
            return Optional.empty();
        }
    }

    private static Optional<JsonObject> row(JsonObject request) {
        JsonElement row = request.get(DATA);
        return row != null && row.isJsonObject() ? Optional.of(row.getAsJsonObject()) : Optional.empty();
    }

    private static Optional<Set<String>> primaryKey(JsonObject request) {
        JsonElement primaryKey = request.get(PRIMARY_KEY);
        if (primaryKey == null || !primaryKey.isJsonArray()) {
            return Optional.empty();
        }
        Set<String> columns = new LinkedHashSet<>();
        for (JsonElement column : primaryKey.getAsJsonArray()) {
            if (!column.isJsonPrimitive() || !column.getAsJsonPrimitive()
                                                    .isString()) {
                return Optional.empty();
            }
            columns.add(column.getAsString());
        }
        return Optional.of(columns);
    }

    private static ResponseEntity<Object> required(String error) {
        return ResponseEntity.badRequest()
                             .body(Map.of("error", error));
    }

    private static ResponseEntity<Object> failed(String error, String datasource, String schema, String table, Exception cause) {
        LOGGER.warn("{} of table [{}] in schema [{}] of data source [{}]", error, LogSanitizer.sanitize(table),
                LogSanitizer.sanitize(schema), LogSanitizer.sanitize(datasource), cause);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("error", error, "details", Objects.toString(cause.getMessage(), cause.getClass()
                                                                                                               .getName())));
    }
}
