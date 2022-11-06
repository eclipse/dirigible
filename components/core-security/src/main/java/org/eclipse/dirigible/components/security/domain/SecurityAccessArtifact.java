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
package org.eclipse.dirigible.components.security.domain;

import java.util.ArrayList;
import java.util.List;

public class SecurityAccessArtifact {

    private List<SecurityAccessConstraint> constraints = new ArrayList<>();

    public List<SecurityAccess> buildSecurityAccesses(String location) {
        List<SecurityAccess> securityAccesses = new ArrayList<>();

        Integer keyIndex = 1;

        for (SecurityAccessConstraint securityAccessConstraint : constraints) {
            for (String role : securityAccessConstraint.getRoles()) {
                SecurityAccess securityAccess = new SecurityAccess();
                securityAccess.setLocation(location);
                securityAccess.setType(SecurityAccess.ARTEFACT_TYPE);
                securityAccess.setName(keyIndex.toString());
                securityAccess.updateKey();
                securityAccess.setMethod(securityAccessConstraint.getMethod());
                securityAccess.setRole(role);
                securityAccess.setPath(securityAccessConstraint.getPath());
                securityAccess.setScope(securityAccessConstraint.getScope());

                securityAccesses.add(securityAccess);
                keyIndex++;
            }
        }

        return securityAccesses;
    }
}
