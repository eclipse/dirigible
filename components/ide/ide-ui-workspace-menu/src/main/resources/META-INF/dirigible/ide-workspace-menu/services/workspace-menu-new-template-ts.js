/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
export function getTemplate() {
    return {
        name: "typescript",
        label: "TypeScript Service",
        extension: "ts",
        data: `import { response } from "sdk/http";\n\nresponse.println("Hello World!");`,
        order: 2
    }
};
