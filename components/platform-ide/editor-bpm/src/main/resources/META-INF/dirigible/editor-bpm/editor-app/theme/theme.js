/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const themingApi = new ThemingApi();

let headElement = document.getElementsByTagName('head')[0];
let themeId = 'vs-light';

function setTheme(theme) {
    if (!theme) theme = themingApi.getSavedTheme();
    themeId = theme.id;
    let themeLinks = headElement.querySelectorAll('link[data-type=\'theme\']');
    for (let i = 0; i < themeLinks.length; i++) {
        headElement.removeChild(themeLinks[i]);
    }
    for (let i = 0; i < theme.links.length; i++) {
        const link = document.createElement('link');
        link.type = 'text/css';
        link.href = theme.links[i];
        link.rel = 'stylesheet';
        link.setAttribute('data-type', 'theme');
        headElement.appendChild(link);
    }
}

setTheme();

themingApi.onThemeChange((theme) => {
    setTheme(theme);
});