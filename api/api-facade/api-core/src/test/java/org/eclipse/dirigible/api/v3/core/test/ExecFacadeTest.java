/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.core.test;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.eclipse.dirigible.api.v3.core.ExecFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Test;

/**
 * The Class ExecFacadeTest.
 */
public class ExecFacadeTest extends AbstractGuiceTest {

    @Test
    public void execTest() {
        String commandLine = null;
        String expectedUnsetValue = null;
        if (Configuration.isOSUNIX() || Configuration.isOSMac()) {
            commandLine = "/bin/sh -c 'echo \"$avar\"'";
            expectedUnsetValue = System.lineSeparator();
        } else if (Configuration.isOSWindows()) {
            commandLine = "cmd.exe /c echo %avar%";
            expectedUnsetValue = "%avar%" + System.lineSeparator();;
        }
        String resultSet = ExecFacade.exec(commandLine, Map.of("avar", "avalue"), null);
        assertEquals(resultSet, "avalue" + System.lineSeparator());
        String resultUnset = ExecFacade.exec(commandLine, null, Collections.singletonList("avar"));
        assertEquals(resultUnset, expectedUnsetValue);
    }

}
