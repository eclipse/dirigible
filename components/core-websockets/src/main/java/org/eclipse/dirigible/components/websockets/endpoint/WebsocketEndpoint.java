/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.websockets.endpoint;

import io.swagger.v3.oas.annotations.Parameter;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * The Class WebsocketEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "websockets")
public class WebsocketEndpoint extends BaseEndpoint{

    /** The websocket service. */
    @Autowired
    private WebsocketService websocketService;

    /**
     * Find all.
     *
     * @param size the size
     * @param page the page
     * @return the page
     */
    @GetMapping
    public Page<Websocket> findAll(
            @Parameter(description = "The size of the page to be returned") @RequestParam(required = false) Integer size,
            @Parameter(description = "Zero-based page index") @RequestParam(required = false) Integer page) {

        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
        }
        if (page == null) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Websocket> websockets = websocketService.findAll(pageable);
        return websockets;

    }
}
