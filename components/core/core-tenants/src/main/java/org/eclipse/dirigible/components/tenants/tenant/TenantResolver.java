/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.tenant;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * The Class TenantResolver.
 */
public class TenantResolver implements HandlerMethodArgumentResolver {

    /**
     * Supports parameter.
     *
     * @param parameter the parameter
     * @return true, if successful
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(Tenant.class) != null && parameter.getParameterType() == String.class)
                || (parameter.getParameterAnnotation(TenantId.class) != null && parameter.getParameterType()
                                                                                         .getTypeName()
                                                                                         .equals("long"));
    }

    /**
     * Resolve argument.
     *
     * @param parameter the parameter
     * @param mavContainer the mav container
     * @param webRequest the web request
     * @param binderFactory the binder factory
     * @return the object
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(Tenant.class) != null) {
            return TenantContext.getCurrentTenant();
        }
        return TenantContext.getCurrentTenantId();
    }
}
