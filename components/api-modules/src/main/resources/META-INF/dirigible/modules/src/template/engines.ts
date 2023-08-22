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
 * API v4 Template Engine
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */
const repository = dirigibleRequire("platform/repository");

const REGISTRY_PUBLIC = "/registry/public/";
const MUSTACHE_FILE_EXTENSION = ".mustache";

const TemplateEnginesFacade = Java.type("org.eclipse.dirigible.components.api.templates.TemplateEnginesFacade");

export function getDefaultEngine() {
    return getVelocityEngine();
};

export function getMustacheEngine() {
    const engine = TemplateEnginesFacade.getMustacheEngine();
    return new TemplateEngine(engine, "mustache");
};

export function getVelocityEngine() {
    const engine = TemplateEnginesFacade.getVelocityEngine();
    return new TemplateEngine(engine, "velocity");
};

export function getJavascriptEngine() {
    const engine = TemplateEnginesFacade.getJavascriptEngine();
    return new TemplateEngine(engine, "javascript");
};

export function generate(template, parameters) {
    return getDefaultEngine().generate(template, parameters);
};

export function generateFromFile(location, parameters) {
    const resource = repository.getResource(REGISTRY_PUBLIC + location);
    if (resource.exists()) {
        const isMustacheTemplate = location.endsWith(MUSTACHE_FILE_EXTENSION);
        const engine = isMustacheTemplate ? getMustacheEngine() : getDefaultEngine();
        const template = resource.getText();
        return engine.generate(template, parameters);
    }
    return null;
};

class TemplateEngine {

    private sm: any;
    private em: any;

    constructor(private engine, type) {
        this.sm = type === "mustache" ? "{{" : null;
        this.em = type === "mustache" ? "}}" : null;
    }


    generate(template, parameters) {
        return this.engine.generate(template, JSON.stringify(parameters), this.sm, this.em);
    };

    setSm(sm) {
        this.sm = sm;
    };

    setEm(em) {
        this.em = em;
    };
}
