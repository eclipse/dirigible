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

import { rs, response, request } from "."

const router = rs.service();
let instance = null;

export class Decorators {
    public static Controller(ctr: {new()}, context: ClassDecoratorContext): void {
        instance = new ctr();
        router.execute();
    }

    public static Get = this.createRequestDecorator("get")
    public static Post = this.createRequestDecorator("post")
    public static Put = this.createRequestDecorator("put")
    public static Patch = this.createRequestDecorator("patch")
    public static Delete = this.createRequestDecorator("delete")
    public static Head = this.createRequestDecorator("head")
    public static Options = this.createRequestDecorator("options")

    private static createRequestDecorator(httpMethod:string): (path: string)=>(target, propertyKey, descriptor: PropertyDescriptor) => void {
        return function (path: string): (target, propertyKey, descriptor: PropertyDescriptor) => void {
            return function (target, _propertyKey, descriptor: PropertyDescriptor) {
                const handler = descriptor ? descriptor.value : target;
                router[httpMethod](
                    path,
                    (ctx, req: typeof request, res: typeof response) => {
                        Decorators.handleRequest(req, res, ctx, handler);
                    }
                );
            };
        }
    }

    private static handleRequest(req: typeof request, res: typeof response, ctx, handler): void {//TODO: what are the types of ctx and handler?
        const body = req.json();
        const maybeResponseBody = handler.apply(instance || {}, [body, ctx, req, res]);
        if (maybeResponseBody) {
            res.json(maybeResponseBody);
        }
    }
}