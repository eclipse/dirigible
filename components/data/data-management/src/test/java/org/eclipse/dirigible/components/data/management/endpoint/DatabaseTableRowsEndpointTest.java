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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.repository.DataSourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * The Database perspective's row editing, answered by Java at the script URL the result view calls.
 */
@WithMockUser(roles = {"ADMINISTRATOR"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
class DatabaseTableRowsEndpointTest {

    private static final String DATA_SOURCE = "RowsTestDB";

    private static final String SCHEMA = "PUBLIC";

    /** Mixed case, so an unquoted identifier would not find it. */
    private static final String TABLE = "Rows_It";

    private static final String URL = "/services/js/view-databases/js/databaseTable.js/{datasource}/{schema}/{table}";

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataSourcesManager dataSourcesManager;

    @Autowired
    private MockMvc mockMvc;

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeEach
    void createTable() throws SQLException {
        if (dataSourceRepository.findByName(DATA_SOURCE)
                                .isEmpty()) {
            dataSourceRepository.save(new DataSource("/test/" + DATA_SOURCE + ".datasource", DATA_SOURCE, "", "org.h2.Driver",
                    "jdbc:h2:mem:rows-test;DB_CLOSE_DELAY=-1", "sa", ""));
        }
        execute("CREATE TABLE \"" + TABLE + "\" (\"Id\" INTEGER NOT NULL PRIMARY KEY, \"Name\" VARCHAR(64), \"Note\" VARCHAR(64))",
                "INSERT INTO \"" + TABLE + "\" VALUES (1, 'John', 'seed')", "CREATE TABLE \"VICTIM\" (\"ID\" INTEGER)");
    }

    @AfterEach
    void dropTable() throws SQLException {
        execute("DROP TABLE IF EXISTS \"" + TABLE + "\"", "DROP TABLE IF EXISTS \"VICTIM\"");
    }

    @Test
    void lists_the_rows_of_a_table() throws Exception {
        mockMvc.perform(get(URL, DATA_SOURCE, SCHEMA, encode(TABLE)))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(content().json("[{\"Id\":1,\"Name\":\"John\",\"Note\":\"seed\"}]", JsonCompareMode.STRICT));
    }

    @Test
    void inserts_the_row_the_create_dialog_sends() throws Exception {
        mockMvc.perform(write(post(URL, DATA_SOURCE, SCHEMA, encode(TABLE)),
                "{\"schemaName\":\"PUBLIC\",\"tableName\":\"Rows_It\",\"data\":{\"Id\":2,\"Name\":\"Mary\",\"Note\":null}}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));

        assertThat(rows()).containsExactly("1|John|seed", "2|Mary|null");
    }

    @Test
    void updates_a_row_by_its_primary_key_and_writes_null() throws Exception {
        mockMvc.perform(write(put(URL, DATA_SOURCE, SCHEMA, encode(TABLE)),
                "{\"data\":{\"Id\":1,\"Name\":null,\"Note\":\"kept\"},\"primaryKey\":[\"Id\"]}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));

        assertThat(rows()).containsExactly("1|null|kept");
    }

    @Test
    void deletes_a_row_by_its_primary_key() throws Exception {
        mockMvc.perform(write(delete(URL, DATA_SOURCE, SCHEMA, encode(TABLE)),
                "{\"data\":{\"Id\":1,\"Name\":\"John\",\"Note\":\"seed\"},\"primaryKey\":[\"Id\"]}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));

        assertThat(rows()).isEmpty();
    }

    @Test
    void a_write_without_its_row_or_key_is_refused() throws Exception {
        mockMvc.perform(write(put(URL, DATA_SOURCE, SCHEMA, encode(TABLE)), "{\"data\":{\"Id\":1,\"Name\":\"x\"}}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Required 'schemaName', 'tableName', 'data' and 'primaryKey'"));
        mockMvc.perform(write(delete(URL, DATA_SOURCE, SCHEMA, encode(TABLE)), "{\"primaryKey\":[\"Id\"]}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Required 'data', and 'primaryKey'"));
        mockMvc.perform(write(post(URL, DATA_SOURCE, SCHEMA, encode(TABLE)), "not json"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Required 'data'"));

        assertThat(rows()).containsExactly("1|John|seed");
    }

    @Test
    void a_statement_the_database_refuses_is_answered_with_its_details() throws Exception {
        mockMvc.perform(write(post(URL, DATA_SOURCE, SCHEMA, encode(TABLE)), "{\"data\":{\"Id\":1,\"Name\":\"duplicate\"}}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Failed to create row"))
               .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void identifiers_are_quoted_so_a_name_cannot_inject_a_statement() throws Exception {
        String table = TABLE + "\"; DROP TABLE \"VICTIM";
        mockMvc.perform(get(URL, DATA_SOURCE, SCHEMA, encode(table)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Failed to fetch rows"));
        mockMvc.perform(write(put(URL, DATA_SOURCE, SCHEMA, encode(TABLE)),
                "{\"data\":{\"Id\":1,\"Name\\\" = 'x'; DROP TABLE \\\"VICTIM\":\"x\"},\"primaryKey\":[\"Id\"]}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Failed to update row"));

        assertThat(rows()).containsExactly("1|John|seed");
        try (Connection connection = connection();
                ResultSet tables = connection.getMetaData()
                                             .getTables(null, SCHEMA, "VICTIM", null)) {
            assertThat(tables.next()).as("the table a name tried to drop is still there")
                                     .isTrue();
        }
    }

    @Test
    void a_name_the_database_does_not_report_is_refused_before_any_statement() throws Exception {
        mockMvc.perform(get(URL, DATA_SOURCE, SCHEMA, encode("Missing")))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.details").value("The table [PUBLIC.Missing] does not exist"));
        mockMvc.perform(get(URL, DATA_SOURCE, SCHEMA, encode("rows_it")))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.details").value("The table [PUBLIC.rows_it] does not exist"));
        mockMvc.perform(write(post(URL, DATA_SOURCE, SCHEMA, encode(TABLE)), "{\"data\":{\"Id\":2,\"Missing\":\"x\"}}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.details").value("The table [PUBLIC.Rows_It] has no column [Missing]"));

        assertThat(rows()).containsExactly("1|John|seed");
    }

    @Test
    @WithMockUser(roles = {"EVERYONE"})
    void a_user_without_a_platform_role_is_refused() throws Exception {
        mockMvc.perform(get(URL, DATA_SOURCE, SCHEMA, encode(TABLE)))
               .andExpect(status().isForbidden());
    }

    private static MockHttpServletRequestBuilder write(MockHttpServletRequestBuilder request, String body) {
        // The platform's chains disable CSRF tokens; this module's test context keeps Spring's default.
        return request.with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body);
    }

    private static String encode(String table) {
        return Base64.getEncoder()
                     .encodeToString(table.getBytes(StandardCharsets.ISO_8859_1));
    }

    private List<String> rows() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Connection connection = connection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM \"" + TABLE + "\" ORDER BY \"Id\"")) {
            while (resultSet.next()) {
                rows.add(resultSet.getInt("Id") + "|" + resultSet.getString("Name") + "|" + resultSet.getString("Note"));
            }
        }
        return rows;
    }

    private void execute(String... statements) throws SQLException {
        try (Connection connection = connection(); Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.executeUpdate(sql);
            }
        }
    }

    private Connection connection() throws SQLException {
        return dataSourcesManager.getDataSource(DATA_SOURCE)
                                 .getConnection();
    }
}
