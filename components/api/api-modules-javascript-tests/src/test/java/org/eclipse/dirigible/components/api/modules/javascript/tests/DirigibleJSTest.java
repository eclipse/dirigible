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
package org.eclipse.dirigible.components.api.modules.javascript.tests;

import org.eclipse.dirigible.components.ide.workspace.domain.ProjectStatusProvider;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"org.eclipse.dirigible.*"})
public class DirigibleJSTest {

    private DirigibleJavascriptCodeRunner codeRunner;

    @MockBean
    private ProjectStatusProvider projectStatusProvider;


    @SpringBootApplication
    static class TestConfiguration {

    }

    @BeforeEach
    public void createCodeRunner() {
        codeRunner = new DirigibleJavascriptCodeRunner();
    }

    @TestFactory
    public List<DynamicContainer> testAll() {
        return new DirigibleJSTestFactory(codeRunner, "modules-tests").createTestContainers();
    }

    @AfterEach
    public void closeCodeRunner() {
        if (codeRunner != null) {
            codeRunner.close();
        }
    }

}
