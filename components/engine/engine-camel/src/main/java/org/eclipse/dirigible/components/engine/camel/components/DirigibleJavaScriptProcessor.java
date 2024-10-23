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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.engine.camel.invoke.Invoker;

class DirigibleJavaScriptProcessor implements Processor {

    private final String javaScriptPath;

    DirigibleJavaScriptProcessor(String javaScriptPath) {
        this.javaScriptPath = javaScriptPath;
    }

    @Override
    public void process(Exchange exchange) {
        Invoker invoker = BeanProvider.getBean(Invoker.class);

        String exchangeResourcePath = (String) exchange.getProperty(Invoker.RESOURCE_PATH_PROPERTY_NAME);
        try {
            exchange.setProperty(Invoker.RESOURCE_PATH_PROPERTY_NAME, javaScriptPath);

            Message message = exchange.getMessage();
            invoker.invoke(message);
        } finally {
            exchange.setProperty(Invoker.RESOURCE_PATH_PROPERTY_NAME, exchangeResourcePath);
        }
    }
}
