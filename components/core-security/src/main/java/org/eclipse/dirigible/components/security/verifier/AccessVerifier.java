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
package org.eclipse.dirigible.components.security.verifier;

import org.eclipse.dirigible.components.security.domain.Access;
import org.eclipse.dirigible.components.security.service.AccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that checks whether the location is secured via the *.access file
 */

@Component
public class AccessVerifier {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AccessVerifier.class);

    /**
     * The security access service.
     */
    @Autowired
    private AccessService securityAccessService;

    /**
     * Checks whether the URI is secured via the *.access file or not
     *
     * @param scope  the scope
     * @param path   the path
     * @param method the method
     * @return all the most specific security access entry matching the URI if any
     */
    public List<Access> getMatchingSecurityAccesses(String scope, String path, String method) {
        List<Access> securityAccesses = new ArrayList<>();
        Access currentSecurityAccess = null;
        List<Access> existingSecurityAccesses = securityAccessService.getAll();
        for (Access securityAccess : existingSecurityAccesses) {
            if (scope.equalsIgnoreCase(securityAccess.getScope()) && path.startsWith(securityAccess.getPath())
                    && (securityAccess.getMethod().equals("*") || method.equals(securityAccess.getMethod()))) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("URI [%s] with HTTP method [%s] is secured because of definition: %s", path, method,
                            securityAccess.getLocation()));
                }
                if ((currentSecurityAccess == null) || (securityAccess.getPath().length() > currentSecurityAccess.getPath().length())) {
                    currentSecurityAccess = securityAccess;
                    securityAccesses.clear();
                    securityAccesses.add(securityAccess);
                } else if (securityAccess.getPath().length() == currentSecurityAccess.getPath().length()) {
                    securityAccesses.add(securityAccess);
                }
            }
        }
        if (securityAccesses.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("URI [%s] with HTTP method [%s] is NOT secured", path, method));
            }
        }
        return securityAccesses;
    }

}
