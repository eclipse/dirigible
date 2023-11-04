/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const SpringBeanProvider = Java.type("org.eclipse.dirigible.components.spring.SpringBeanProvider");
const camelMessage = __context.camelMessage;
const Invoker = Java.type('org.eclipse.dirigible.components.engine.camel.invoke.Invoker');
const invoker = SpringBeanProvider.getBean(Invoker.class);

exports.invokeRoute = function (routeId, payload, headers) {
    return invoker.invokeRoute(routeId, payload, headers);
};

exports.getInvokingRouteMessage = function () {
    return camelMessage;
};
