/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.service.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.engine.template.velocity.VelocityGenerationEngine;
import org.eclipse.dirigible.components.ide.template.domain.GenerationTemplateMetadataSource;

/**
 * Renders a glue descriptor entry into the Java a Generate writes for it: the entry is bound by
 * {@link GlueGenerator} exactly as in the generation pipeline, then rendered by the real Velocity
 * engine from the template the events-java module ships. It lives in the generator's package
 * because both are package-private.
 */
public final class GlueTemplateRendering {

    private static final String INBOUND_MESSAGE = "/template-application-events-java/events/InboundMessage.java.template";

    private static final String WEBHOOK = "/template-application-events-java/events/Webhook.java.template";

    private static final String INBOUND_FILE = "/template-application-events-java/events/InboundFile.java.template";

    private GlueTemplateRendering() {}

    /**
     * Render the queue/topic consumer of one inbound entry.
     *
     * @param entry the {@code inboundMessages} glue entry
     * @param genFolder the generation folder, the {@code javaGenFolderName} of the package
     * @return the generated Java source
     * @throws IOException when the template cannot be read or rendered
     */
    public static String renderInboundMessage(Map<String, Object> entry, String genFolder) throws IOException {
        return render("inboundMessages", INBOUND_MESSAGE, entry, genFolder);
    }

    /**
     * Render the webhook controller of one inbound entry.
     *
     * @param entry the {@code inbound} glue entry
     * @param genFolder the generation folder, the {@code javaGenFolderName} of the package
     * @return the generated Java source
     * @throws IOException when the template cannot be read or rendered
     */
    public static String renderWebhook(Map<String, Object> entry, String genFolder) throws IOException {
        return render("inbound", WEBHOOK, entry, genFolder);
    }

    /**
     * Render the drop-folder job of one inbound entry.
     *
     * @param entry the {@code inboundFiles} glue entry
     * @param genFolder the generation folder, the {@code javaGenFolderName} of the package
     * @return the generated Java source
     * @throws IOException when the template cannot be read or rendered
     */
    public static String renderInboundFile(Map<String, Object> entry, String genFolder) throws IOException {
        return render("inboundFiles", INBOUND_FILE, entry, genFolder);
    }

    private static String render(String collection, String location, Map<String, Object> entry, String genFolder) throws IOException {
        GenerationTemplateMetadataSource source = new GenerationTemplateMetadataSource();
        source.setLocation(location);
        source.setRename("gen/events/" + genFolder + "/Handler.java");
        List<GeneratedFile> files = new GlueGenerator(new VelocityRenderer()).generate(collection, source, read(location),
                Map.of(collection, List.of(entry)), new LinkedHashMap<>(Map.of("javaGenFolderName", genFolder)));
        return files.get(0)
                    .content();
    }

    private static String read(String location) throws IOException {
        try (InputStream in = GlueTemplateRendering.class.getResourceAsStream("/META-INF/dirigible" + location)) {
            if (in == null) {
                throw new IOException("Template [" + location + "] is not on the test classpath");
            }
            return new String(in.readAllBytes(), UTF_8);
        }
    }

    /** Renders a body with Velocity directly, without the platform's engine registry. */
    private static final class VelocityRenderer extends ModelTemplateRenderer {

        private final VelocityGenerationEngine velocity = new VelocityGenerationEngine();

        VelocityRenderer() {
            super(null, null);
        }

        @Override
        String render(GenerationTemplateMetadataSource source, String content, Map<String, Object> parameters) throws IOException {
            return new String(velocity.generate(parameters, source.getLocation(), content.getBytes(UTF_8)), UTF_8);
        }

        @Override
        String renderPath(String location, String rename, Map<String, Object> parameters) {
            return rename;
        }
    }
}
