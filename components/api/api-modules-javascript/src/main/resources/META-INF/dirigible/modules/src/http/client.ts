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
/**
 * HTTP API Client
 *
 */

const HttpClientFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpClientFacade");


interface Option {
	params: Array<{name: string, value: string}>;
}


export class HttpClient {
    public get(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.get(url, opts);
        return JSON.parse(result);
    };
    
    public post(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.post(url, opts);
        return JSON.parse(result);
    };
    
    public put(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.put(url, opts);
        return JSON.parse(result);
    };
    
    public patch(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.patch(url, opts);
        return JSON.parse(result);
    };
    
    public delete(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.delete(url, opts);
        return JSON.parse(result);
    };
    
    public head(_url: string, options: Option): Object {
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.head(url, opts);
        return JSON.parse(result);
    };
    
    public trace(_url: string, options: Option): Object { 
        let url = buildUrl(_url, options);
        let opts = '{}';
        if (options) {
            opts = JSON.stringify(options);
        }
        const result: string = HttpClientFacade.trace(url, opts);
        return JSON.parse(result);
    };
}



function buildUrl(url: string, options: Option): string {
    if (options === undefined || options === null || options.params === undefined || options.params === null || options.params.length === 0) {
        return url;
    }
    for (let i = 0; i < options.params.length; i++) {
        if (i === 0) {
            url += '?' + options.params[i].name + '=' + options.params[i].value;
        } else {
            url += '&' + options.params[i].name + '=' + options.params[i].value;
        }
    }
    return url;
}

export const httpClient = new HttpClient();
export const get = httpClient.get;
export const post = httpClient.post;
export const put = httpClient.put;
export const patch = httpClient.patch;
export const head = httpClient.head;
export const trace = httpClient.trace;
export const del = httpClient.delete;