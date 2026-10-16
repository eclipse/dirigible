/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.oauth2.tenant;

import org.eclipse.dirigible.components.base.tenant.TenantResolutionStrategy;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Matches when the tenant of a request is resolved from the user's identity provider groups - the
 * one resolution under which a user holds roles in several tenants at once.
 */
public class TokenGroupsResolutionCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return TenantResolutionStrategy.TOKEN_GROUPS == TenantResolutionStrategy.fromConfiguration();
    }
}
