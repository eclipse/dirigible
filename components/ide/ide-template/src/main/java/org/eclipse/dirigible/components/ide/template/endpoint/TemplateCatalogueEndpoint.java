/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The template catalogue the IDE's dialogs read - every template with its id, sources and
 * parameters - answered without running a script.
 *
 * <p>
 * It is served at the script URL that used to answer it,
 * {@code /services/js/service-template/api/templates.js}, so the IDE keeps calling what it called.
 * A literal mapping wins over the JavaScript endpoint's pattern, and the script itself is gone.
 */
@RestController
class TemplateCatalogueEndpoint {

    private static final String IDE_TEMPLATES = "/services/js/service-template/api/templates.js";

    private static final String EXTENSION = "extension";

    private final TemplateCatalogue catalogue;

    /**
     * Instantiates a new template catalogue endpoint.
     *
     * @param catalogue the template catalogue
     */
    TemplateCatalogueEndpoint(TemplateCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    @GetMapping(IDE_TEMPLATES)
    List<Map<String, Object>> templates() {
        return sorted(catalogue.read(TemplateCatalogue.TEMPLATES));
    }

    /**
     * The file extensions of the project and model templates, in catalogue order and repeated as often
     * as they are declared.
     */
    @GetMapping(IDE_TEMPLATES + "/extensions")
    List<Object> templateExtensions() {
        List<Object> extensions = new ArrayList<>();
        for (Map<String, Object> template : catalogue.read(TemplateCatalogue.TEMPLATES)) {
            if (template.get(EXTENSION) instanceof String extension && !extension.isEmpty()) {
                extensions.add(extension);
            }
        }
        return extensions;
    }

    @GetMapping(IDE_TEMPLATES + "/menu")
    List<Map<String, Object>> menuTemplates() {
        return sorted(catalogue.read(TemplateCatalogue.MENU_TEMPLATES));
    }

    private static List<Map<String, Object>> sorted(List<Map<String, Object>> templates) {
        templates.sort(TemplateCatalogue.DIALOG_ORDER);
        return templates;
    }
}
