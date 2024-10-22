/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.components;

import org.apache.camel.*;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invoke JavaScript code.
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = DirigibleJavaScriptEndpoint.SCHEME, title = "Dirigible JavaScript",
        syntax = DirigibleJavaScriptEndpoint.SCHEME, producerOnly = true, remote = false, category = {Category.CORE, Category.SCRIPT})
public class DirigibleJavaScriptEndpoint extends DefaultEndpoint {

    /**
     * If changed, file [META-INF/services/org/apache/camel/component/dirigible-java-script] must be renamed as well
     */
    static final String SCHEME = "dirigible-java-script";
    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleJavaScriptEndpoint.class);
    @UriParam(label = "common", description = "Sets the path of the JavaScript file.")
    @Metadata(required = true)
    private String javaScriptPath;

    public DirigibleJavaScriptEndpoint() {
        LOGGER.info("Creating [{}]", this);
        setExchangePattern(ExchangePattern.InOut);
    }

    public DirigibleJavaScriptEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        LOGGER.info("Creating [{}]", this);
        setExchangePattern(ExchangePattern.InOut);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new DirigibleJavaScriptProducer(this, javaScriptPath);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("You cannot consume from a bean endpoint");
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();
    }

    @Override
    protected String createEndpointUri() {
        // return DirigibleJavaScriptEndpoint.SCHEME + ":" +
        // UnsafeUriCharactersEncoder.encode(getJavaScriptPath());
        // return DirigibleJavaScriptEndpoint.SCHEME + "?javaScriptPath=" +
        // UnsafeUriCharactersEncoder.encode(getJavaScriptPath());

        return "dirigible-java-script?javaScriptPath=camel-custom-component/handler.ts";
    }

    public String getJavaScriptPath() {
        return javaScriptPath;
    }

    public void setJavaScriptPath(String javaScriptPath) {
        this.javaScriptPath = javaScriptPath;
    }

}
