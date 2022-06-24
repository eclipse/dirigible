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
package org.eclipse.dirigible.web.quarkus.extension;

import io.smallrye.mutiny.Uni;
//import io.vertx.mutiny.core.Context;
//import io.vertx.mutiny.core.Vertx;
import org.graalvm.polyglot.Value;

public class UniThenUtil {
    public static void then(Uni<?> self, Value onFulfilled, Value onRejected) {
//        Context context = Vertx.currentContext();
        self.subscribe().with(res -> {
            if (onFulfilled != null) {
//                context.runOnContext(onFulfilled::executeVoid);
                onFulfilled.executeVoid();
            }
        }, ex -> {
            if (onRejected != null) {
//                context.runOnContext(onRejected::executeVoid);
                onRejected.executeVoid(ex);
            }
        });
    }
}
