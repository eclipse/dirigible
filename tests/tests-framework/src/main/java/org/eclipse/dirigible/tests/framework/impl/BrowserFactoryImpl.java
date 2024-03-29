/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.tests.framework.impl;

import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.BrowserFactory;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
class BrowserFactoryImpl implements BrowserFactory {

    private final int localServerPort;

    BrowserFactoryImpl(@LocalServerPort int localServerPort) {
        this.localServerPort = localServerPort;
    }

    public Browser createByHost(String host) {
        return create(BrowserImpl.Protocol.HTTP, host, localServerPort);
    }

    private Browser create(BrowserImpl.Protocol protocol, String host, int port) {
        return new BrowserImpl(protocol, host, port);
    }

}
