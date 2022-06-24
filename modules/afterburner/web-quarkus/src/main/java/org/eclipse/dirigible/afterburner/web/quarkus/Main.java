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
package org.eclipse.dirigible.afterburner.web.quarkus;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.vertx.mutiny.ext.web.Router;
import org.eclipse.dirigible.afterburner.web.quarkus.app.ApplicationBootstrap;

import javax.inject.Inject;
import java.nio.file.Path;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    Router router;

    public static void main(String... args) {
        Quarkus.run(Main.class, args);
    }

    @Override
    public int run(String... args) {
        var cwd = Path.of(args[0]);
        var appEntry = Path.of(args[1]);
        ApplicationBootstrap.forCurrentThread(cwd, appEntry).registerJSRoutes(router);
        Quarkus.waitForExit();
        return 0;
    }
}
