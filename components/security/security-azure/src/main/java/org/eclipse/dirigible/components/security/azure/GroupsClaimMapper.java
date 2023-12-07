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
package org.eclipse.dirigible.components.security.azure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.ClaimAccessor;

public class GroupsClaimMapper {

    private final String groupsClaim;
    private final Map<String, List<String>> groupToAuthorities;

    public GroupsClaimMapper(String groupsClaim, Map<String, List<String>> groupToAuthorities) {
        this.groupsClaim = groupsClaim;
        this.groupToAuthorities = Collections.unmodifiableMap(groupToAuthorities);
    }

    public Collection<? extends GrantedAuthority> mapAuthorities(ClaimAccessor source) {
        List<String> groups = source.getClaimAsStringList(groupsClaim);
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }

        List<GrantedAuthority> result = new ArrayList<>();
        for (String g : groups) {
            List<String> authNames = groupToAuthorities.get(g);
            if (authNames == null) {
                continue;
            }

            List<SimpleGrantedAuthority> mapped = authNames.stream()
                                                           .map(SimpleGrantedAuthority::new)
                                                           .collect(Collectors.toList());

            result.addAll(mapped);
        }

        return result;
    }

}
