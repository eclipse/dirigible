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

const KafkaFacade = Java.type("org.eclipse.dirigible.components.api.kafka.KafkaFacade");

export function topic(destination, configuration) {
    return new Topic(destination, configuration);
};

class Topic {

    constructor(private destination, private configuration) { }

    startListening(handler, timeout) {
        KafkaFacade.startListening(this.destination, handler, timeout, this.configuration);
    };

    stopListening(handler, timeout) {
        KafkaFacade.stopListening(this.destination, this.configuration);
    };
};