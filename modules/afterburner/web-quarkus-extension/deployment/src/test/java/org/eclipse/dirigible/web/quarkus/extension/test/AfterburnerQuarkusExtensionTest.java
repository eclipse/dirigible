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
package org.eclipse.dirigible.web.quarkus.extension.test;

import io.smallrye.mutiny.Uni;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

import java.lang.reflect.InvocationTargetException;

public class AfterburnerQuarkusExtensionTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withEmptyApplication();

    @Test
    public void writeYourOwnUnitTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var uni = Uni.createFrom().item("pesho");
        uni.getClass().getMethod("then", Value.class, Value.class).invoke(uni, null, null);
    }
}
