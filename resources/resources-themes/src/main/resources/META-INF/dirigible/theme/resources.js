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
var rs = require('http/v4/rs');
var escape = require('utils/v4/escape');
var streams = require('io/v4/streams');
var repositoryManager = require('platform/v4/repository');
var configurations = require('core/v4/configurations');
var themesManager = require('theme/extensions/themes');
var cacheUtils = require('theme/utils/cache');

var THEME_CACHE = cacheUtils.getCache();

var PATH_REGISTRY_PUBLIC = '/registry/public';
var DIRIGIBLE_THEME_DEFAULT = 'DIRIGIBLE_THEME_DEFAULT';
var DEFAULT_THEME = 'default';
var NAME_PARAM = 'name';
var THEME_COOKIE = 'dirigible-theme';
var DEFAULT_THEME_MODULE_NAME = 'theme-';

rs.service()
	.resource('')
		.get(function(ctx, request, response) {
			var theme = getCurrentTheme(request, response);
			response.print(theme);
			response.setContentType('text/plain');
		})
	.resource('themes')
		.get(function(ctx, request, response) {
			var themes = themesManager.getThemes();
			response.setContentType('application/json');
			response.print(JSON.stringify(themes));
		})
	.resource('{path}')
		.get(function(ctx, request, response) {
			var path = ctx.pathParameters.path;

			if (isCached(request, path)) {
				response.setContentType('text/css');
				response.setHeader('ETag', getTag(request));
				response.setStatus(response.NOT_MODIFIED);
			} else {
				var content = getContent(request, response, path);

				if (content !== null && content !== '') {
					var tag = cacheResource(path);

					response.setContentType('text/css');
					response.setStatus(response.OK);
					response.setHeader('ETag', tag);
					response.setHeader('Cache-Control', 'public, must-revalidate, max-age=0');
					response.write(content);
				} else {
					response.setStatus(response.NOT_FOUND);
					response.println('');
				}
			}
		})
.execute();

function getContent(request, response, path) {
	var content = null;
	var cookieValue = getCurrentTheme(request, response);
	var themes = themesManager.getThemes();

	var themeModule = null;
	for (var i = 0; i < themes.length; i ++) {
		if (themes[i].id === cookieValue) {
			themeModule = themes[i].module;
			break;
		}
	}
	if (themes.length === 0) {
		themeModule = DEFAULT_THEME_MODULE_NAME + cookieValue;
	}

	var resource = repositoryManager.getResource(PATH_REGISTRY_PUBLIC + '/' + themeModule + '/' + path);
	if (resource.exists()) {
		var resourceContent = resource.getContent();
		var repositoryInputStream = streams.createByteArrayInputStream(resourceContent);
		content = repositoryInputStream.readBytes();
	} else {
		var inputStream = streams.getResourceAsByteArrayInputStream('/' + themeModule + '/' + path);
		content = inputStream.readBytes();
	}
	return content;
}

function getCurrentTheme(request, response) {
	var env = configurations.get(DIRIGIBLE_THEME_DEFAULT);
	var cookieValue = env ? env : DEFAULT_THEME;
	var themeName = request.getParameter(NAME_PARAM);
	themeName = escape.escapeHtml4(themeName);
	themeName = escape.escapeJavascript(themeName);

	if (themeName !== null && themeName !==  '') {
		setThemeCookie(response, themeName);
		THEME_CACHE.clear();
		cookieValue = themeName;
	} else {
		var themeCookie = getThemeCookie(request);
		cookieValue = themeCookie !== null ? themeCookie : cookieValue;
	}

	cookieValue = escape.escapeHtml4(cookieValue);
	cookieValue = escape.escapeJavascript(cookieValue);
	var themes = themesManager.getThemes();
	var themeFound = false;
	themes.forEach(function(e) {
		if (e.id === cookieValue) {
			themeFound = true;
		}
	})
	cookieValue = themeFound ? cookieValue : DEFAULT_THEME;
	return cookieValue;
}

function getThemeCookie(request) {
	var cookies = request.getCookies();
	for (var i = 0; cookies !== null && i < cookies.length; i ++) {
		if (cookies[i].name === THEME_COOKIE) {
			return cookies[i].value;
		}
	}
	return null;
}

function setThemeCookie(response, theme) {
	response.addCookie({
		'name': THEME_COOKIE,
		'value': theme,
		'path': '/',
		'maxAge': 30 * 24 * 60 * 60
	});
}

function cacheResource(path) {
	var tag = THEME_CACHE.generateTag();
	THEME_CACHE.setTag(path, tag);
	return tag;
}

function isCached(request, path) {
	var tag = getTag(request);
	var cachedTag = THEME_CACHE.getTag(path);
	return tag === null || cachedTag === null ? false : tag === cachedTag;
}

function getTag(request) {
	return request.getHeader('If-None-Match');
}
