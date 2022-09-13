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
const response = require("http/v4/response");
const extensions = require("core/v4/extensions");

let tiles = {};

let tileExtensions = extensions.getExtensions("${projectName}-tile");
for (let i = 0; tileExtensions !== null && i < tileExtensions.length; i++) {
    let tileExtension = require(tileExtensions[i]);
    let tile = tileExtension.getTile();
    if (!tiles[tile.group]) {
        tiles[tile.group] = [];
    }
    tiles[tile.group].push({
        name: tile.name,
        location: tile.location,
        description: tile.description,
        order: tile.order
    });
}

for (let next in tiles) {
    tiles[next] = tiles[next].sort(function (a, b) {
        var result = a.order - b.order;
        return result;
    });
}

response.println(JSON.stringify(tiles));