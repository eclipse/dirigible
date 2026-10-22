/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package deprecationjava;

import java.util.Map;
import org.eclipse.dirigible.sdk.http.Controller;
import org.eclipse.dirigible.sdk.http.Get;

/**
 * The Java side of a project without server-side JavaScript or TypeScript.
 */
@Controller
public class HelloController {

    @Get("/")
    public Map<String, Object> hello() {
        return Map.of("message", "Hello from Java");
    }
}
