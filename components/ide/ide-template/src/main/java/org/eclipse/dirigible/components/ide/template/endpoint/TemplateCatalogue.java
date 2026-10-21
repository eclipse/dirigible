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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.service.ExtensionService;
import org.eclipse.dirigible.components.ide.template.service.TemplateDescriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reads the templates registered on template extension points - the catalogue the IDE offers in its
 * "New from template" dialogs, its Welcome view and its editors' Regenerate.
 */
@Component
class TemplateCatalogue {

    /** The extension point the project and model templates register on. */
    static final String TEMPLATES = "platform-templates";

    /** The extension point the file templates of the "New" menu register on. */
    static final String MENU_TEMPLATES = "platform-templates-menu";

    /**
     * The order the IDE's dialogs list templates in: those declaring an {@code order} first, by it,
     * then the labelled ones by label ignoring case, then the rest by name. A template without a label
     * sorts after the labelled ones.
     */
    static final Comparator<Map<String, Object>> DIALOG_ORDER = TemplateCatalogue::compareForDialogs;

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateCatalogue.class);

    private static final String ID = "id";

    private static final String ORDER = "order";

    private static final String LABEL = "label";

    private static final String NAME = "name";

    private final ExtensionService extensionService;

    private final TemplateDescriptors templateDescriptors;

    /**
     * Instantiates a new template catalogue.
     *
     * @param extensionService the extension service
     * @param templateDescriptors the template descriptors
     */
    TemplateCatalogue(ExtensionService extensionService, TemplateDescriptors templateDescriptors) {
        this.extensionService = extensionService;
        this.templateDescriptors = templateDescriptors;
    }

    /**
     * Reads the templates registered on the extension point that the current user may see, in the order
     * the extension service returns them, each with its {@code id}: the module its extension names. A
     * template whose descriptor cannot be read is skipped with a warning naming it.
     *
     * @param extensionPoint the extension point
     * @return the templates
     */
    List<Map<String, Object>> read(String extensionPoint) {
        List<Map<String, Object>> templates = new ArrayList<>();
        try (TemplateDescriptors.Reading reading = templateDescriptors.open()) {
            for (Extension extension : extensionService.findByExtensionPoint(extensionPoint)) {
                String module = extension.getModule();
                try {
                    Map<String, Object> template = new LinkedHashMap<>(reading.read(module, null));
                    template.put(ID, module);
                    templates.add(template);
                } catch (RuntimeException e) {
                    LOGGER.warn("Template [{}] of extension point [{}] cannot be read and is not listed.", module, extensionPoint, e);
                }
            }
        }
        return templates;
    }

    private static int compareForDialogs(Map<String, Object> left, Map<String, Object> right) {
        Double leftOrder = order(left);
        Double rightOrder = order(right);
        if (leftOrder != null || rightOrder != null) {
            return leftOrder == null ? 1 : rightOrder == null ? -1 : Double.compare(leftOrder, rightOrder);
        }
        String leftLabel = label(left);
        String rightLabel = label(right);
        if (leftLabel != null || rightLabel != null) {
            return leftLabel == null ? 1 : rightLabel == null ? -1 : leftLabel.compareTo(rightLabel);
        }
        return name(left).compareTo(name(right));
    }

    private static Double order(Map<String, Object> template) {
        return template.get(ORDER) instanceof Number order ? order.doubleValue() : null;
    }

    private static String label(Map<String, Object> template) {
        return template.get(LABEL) instanceof String label && !label.isEmpty() ? label.toLowerCase(Locale.ROOT) : null;
    }

    private static String name(Map<String, Object> template) {
        return template.get(NAME) instanceof String name ? name : "";
    }
}
