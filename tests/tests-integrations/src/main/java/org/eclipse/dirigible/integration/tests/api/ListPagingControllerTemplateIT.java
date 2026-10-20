/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.engine.template.velocity.VelocityGenerationEngine;
import org.junit.jupiter.api.Test;

/**
 * The generated list endpoints honour the page their callers ask for (dirigible #7982). The shell's
 * {@code api.getAll} walks {@code $limit}/{@code $offset} pages until a short one, and the personal
 * and partner lists did not even declare the two parameters: every page came back with the whole
 * set, so a person with 1000+ records hung the page that listed them. The counts are counted, not
 * loaded. Rendered through the platform's {@link VelocityGenerationEngine}, like
 * {@code PersonalSurfaceCreateValidationTemplateIT}; {@code IntentEmissionCoverageIT} compiles the
 * same templates, and {@code GeneratedListPagingIT} runs the power controller's paging.
 */
class ListPagingControllerTemplateIT {

    private static final String REST_BASE = "/META-INF/dirigible/template-application-rest-java/api/";

    private final VelocityGenerationEngine velocityGenerationEngine = new VelocityGenerationEngine();

    @Test
    void theSelfServiceListsTakeAPageAndCountInTheDatabase() throws Exception {
        for (String[] surface : new String[][] {{"EntityMyController.java.template", "Employee"},
                {"EntityPartnerController.java.template", "Employee"}}) {
            String rendered = render(surface[0]);

            assertTrue(rendered.contains("getAll(@QueryParam(\"$limit\") Integer limit, @QueryParam(\"$offset\") Integer offset)"),
                    surface[0] + " must declare the page parameters: " + rendered);
            assertTrue(rendered.contains("repository.findAll(Criteria.create().eq(\"" + surface[1] + "\", me).orderByAsc(\"Id\"),"),
                    surface[0] + " must page in a stable order: " + rendered);
            assertTrue(rendered.contains("limit != null ? limit.intValue() : -1, offset != null ? offset.intValue() : 0"),
                    surface[0] + " must pass the page to the repository, all rows without one: " + rendered);
            assertTrue(rendered.contains("repository.count(Criteria.create().eq(\"" + surface[1] + "\", me))"),
                    surface[0] + " must count in the database: " + rendered);
            assertFalse(rendered.contains(").size());"), surface[0] + " must not load every record to count it: " + rendered);
        }
    }

    @Test
    void thePowerSearchTakesThePageFromItsBodyAndCountsInTheDatabase() throws Exception {
        String rendered = render("EntityController.java.template");

        assertTrue(rendered.contains("pageParameter(filter, \"$limit\", -1), pageParameter(filter, \"$offset\", 0)"),
                "the search must honour $limit/$offset of its body: " + rendered);
        assertTrue(rendered.contains("repository.countQuery(\"select count(e) from vacations_VacationRequestEntity e\" + where, params)"),
                "the filtered count must be a count query: " + rendered);
        assertFalse(rendered.contains("runFilter(filter).size()"), "the filtered count must not load every match: " + rendered);
        assertTrue(rendered.contains(" order by e.Id\");"), "without a declared order the search pages by primary key: " + rendered);
    }

    private String render(String templateName) throws Exception {
        String location = REST_BASE + templateName;
        String template;
        try (InputStream in = getClass().getResourceAsStream(location)) {
            assertNotNull(in, "template resource not found on classpath: " + location);
            template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new String(velocityGenerationEngine.generate(context(), location, template.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    private static Map<String, Object> context() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "VacationRequest");
        parameters.put("projectName", "vacations");
        parameters.put("perspectiveName", "VacationRequest");
        parameters.put("javaGenFolderName", "vacations");
        parameters.put("javaPerspectiveName", "vacationrequest");
        parameters.put("tablePrefix", "VACATIONS_");
        parameters.put("dataName", "VACATION_REQUEST");
        parameters.put("pkPropertyName", "Id");
        parameters.put("properties", List.of(primaryKey()));
        parameters.put("sensitiveProperties", List.of());
        parameters.put("rowChecks", List.of());
        parameters.put("personalProperty", "Employee");
        parameters.put("personalFkJavaClass", "Integer");
        parameters.put("personalIdentityProperty", "Email");
        parameters.put("personalIdentityLabel", "Name");
        parameters.put("personalIdentityRepositoryClass", "gen.vacations.data.vacationrequest.EmployeeRepository");
        parameters.put("partnerProperty", "Employee");
        parameters.put("partnerFkJavaClass", "Integer");
        parameters.put("partnerIdentityProperty", "Email");
        parameters.put("partnerIdentityLabel", "Name");
        parameters.put("partnerIdentityRepositoryClass", "gen.vacations.data.vacationrequest.EmployeeRepository");
        return parameters;
    }

    private static Map<String, Object> primaryKey() {
        Map<String, Object> property = new LinkedHashMap<>();
        property.put("name", "Id");
        property.put("dataName", "VACATION_REQUEST_ID");
        property.put("dataType", "INTEGER");
        property.put("dataTypeJavaClass", "Integer");
        property.put("dataPrimaryKey", Boolean.TRUE);
        property.put("dataNotNull", Boolean.TRUE);
        return property;
    }
}
