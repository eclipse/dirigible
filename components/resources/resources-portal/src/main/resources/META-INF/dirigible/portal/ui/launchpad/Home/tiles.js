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
/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
import { response } from "@dirigible/http";
import { extensions } from "@dirigible/extensions";

let tiles = {};

let tileExtensions = await extensions.loadExtensionModules("portal-tile");
for (let i = 0; i < tileExtensions?.length; i++) {
    let tile = tileExtensions[i].getTile();

    if (!tile) {
        continue;
    }
    if (!tiles[tile.group]) {
        tiles[tile.group] = [];
    }
    tiles[tile.group].push({
        name: tile.name,
        location: tile.location,
        caption: tile.caption,
        tooltip: tile.tooltip,
        order: parseInt(tile.order),
        groupOrder: parseInt(tile.groupOrder)
    });
}

for (let next in tiles) {
    tiles[next] = tiles[next].sort((a, b) => a.order - b.order);
}

let sortedGroups = [];
for (let next in tiles) {
    sortedGroups.push({
        name: next,
        order: tiles[next][0] && tiles[next][0].groupOrder ? tiles[next][0].groupOrder : 100,
        tiles: tiles[next]
    });
}
sortedGroups = sortedGroups.sort((a, b) => a.order - b.order);

let sortedTiles = {};
for (let i = 0; i < sortedGroups.length; i++) {
    sortedTiles[sortedGroups[i].name] = sortedGroups[i].tiles;
}

response.println(JSON.stringify(sortedTiles));