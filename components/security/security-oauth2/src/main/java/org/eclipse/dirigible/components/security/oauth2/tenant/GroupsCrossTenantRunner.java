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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.groups.UserTenantAssignments;
import org.eclipse.dirigible.components.base.util.AuthoritiesUtil;
import org.eclipse.dirigible.components.tenants.tenant.CrossTenantRunEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * The {@link CrossTenantRunner} of the token-groups tenant resolution: the tenants a user holds a
 * role in are the ones their identity provider groups grant, exactly as the tenant picker offers
 * them.
 *
 * <p>
 * What keeps a visit inside its tenant:
 * <ul>
 * <li>the memberships are read from the authentication when the run starts, and only a tenant the
 * picker would let the user enter - granted, registered and provisioned - is visited;</li>
 * <li>each visit runs in the tenant's scope and with an authentication of its own, carrying the
 * user's global roles plus the roles granted <em>in that tenant</em> - the roles of the tenant the
 * session has selected never ride along. That authentication lives on the calling thread only and
 * is never saved to the session;</li>
 * <li>the caller's tenant scope and authentication are restored after every visit, also when the
 * work fails;</li>
 * <li>a run cannot start from within a run, and a run without a signed-in user - a job, a listener
 * - is refused, since there are no memberships to read.</li>
 * </ul>
 */
@Component
@Conditional(TokenGroupsResolutionCondition.class)
class GroupsCrossTenantRunner implements CrossTenantRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupsCrossTenantRunner.class);

    /** The authority prefix of a role. */
    private static final String ROLE_PREFIX = "ROLE_";

    /** Whether the calling thread is inside a run. */
    private static final ThreadLocal<Boolean> RUNNING = new ThreadLocal<>();

    private final TenantSelectionManager tenantSelectionManager;

    private final TenantContext tenantContext;

    private final ApplicationEventPublisher events;

    /**
     * Instantiates the runner.
     *
     * @param tenantSelectionManager what the memberships of a user are read from
     * @param tenantContext the tenant context
     * @param events where each visit is announced, for the audit trail
     */
    GroupsCrossTenantRunner(TenantSelectionManager tenantSelectionManager, TenantContext tenantContext, ApplicationEventPublisher events) {
        this.tenantSelectionManager = tenantSelectionManager;
        this.tenantContext = tenantContext;
        this.events = events;
    }

    @Override
    public List<GrantedTenant> grantedTenants() {
        Authentication authentication = signedInUser();
        UserTenantAssignments assignments = tenantSelectionManager.assignmentsOf(authentication);
        List<GrantedTenant> granted = new ArrayList<>();
        for (TenantOption option : tenantSelectionManager.availableTenants(authentication)) {
            if (option.provisionedHere()) {
                granted.add(new GrantedTenant(option.id(), option.name(), Set.copyOf(assignments.rolesFor(option.id()))));
            }
        }
        return granted;
    }

    @Override
    public <R> List<CrossTenantResult<R>> forEachGrantedTenant(String operation, Function<String, R> work) {
        if (operation == null || operation.isBlank()) {
            throw new IllegalArgumentException("A cross-tenant run names its operation - it is what the audit trail records");
        }
        if (work == null) {
            throw new IllegalArgumentException("A cross-tenant run needs the work to run in each tenant");
        }
        if (Boolean.TRUE.equals(RUNNING.get())) {
            throw new IllegalStateException("A cross-tenant run cannot be started from within a cross-tenant run");
        }
        Authentication authentication = signedInUser();
        UserTenantAssignments assignments = tenantSelectionManager.assignmentsOf(authentication);
        List<GrantedTenant> tenants = grantedTenants();
        LOGGER.info("User [{}] runs [{}] across [{}] tenants.", authentication.getName(), operation, tenants.size());

        SecurityContext callerContext = SecurityContextHolder.getContext();
        List<CrossTenantResult<R>> results = new ArrayList<>(tenants.size());
        RUNNING.set(Boolean.TRUE);
        try {
            for (GrantedTenant tenant : tenants) {
                results.add(visit(tenant.id(), operation, work, inTenant(authentication, assignments, tenant.id()), callerContext));
            }
        } finally {
            SecurityContextHolder.setContext(callerContext);
            RUNNING.remove();
        }
        return results;
    }

    private <R> CrossTenantResult<R> visit(String tenantId, String operation, Function<String, R> work, Authentication visitor,
            SecurityContext callerContext) {
        SecurityContext visitContext = SecurityContextHolder.createEmptyContext();
        visitContext.setAuthentication(visitor);
        SecurityContextHolder.setContext(visitContext);
        CrossTenantResult<R> result;
        try {
            R value = tenantContext.execute(tenantId, () -> work.apply(tenantId));
            result = new CrossTenantResult<>(tenantId, true, value, null);
        } catch (RuntimeException e) {
            LOGGER.warn("Cross-tenant run [{}] of user [{}] failed in tenant [{}]", operation, visitor.getName(), tenantId, e);
            result = new CrossTenantResult<>(tenantId, false, null, e.getMessage() == null ? e.getClass()
                                                                                              .getName()
                    : e.getMessage());
        } finally {
            SecurityContextHolder.setContext(callerContext);
        }
        events.publishEvent(
                new CrossTenantRunEvent(tenantId, visitor.getName(), operation, result.succeeded(), result.error(), Instant.now()));
        return result;
    }

    /**
     * The authentication a visit of a tenant runs as: the caller's authorities that are not roles, plus
     * the global roles and the roles granted in that tenant.
     */
    private static Authentication inTenant(Authentication authentication, UserTenantAssignments assignments, String tenantId) {
        Collection<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority() != null && !authority.getAuthority()
                                                              .startsWith(ROLE_PREFIX)) {
                authorities.add(authority);
            }
        }
        Set<String> roles = new LinkedHashSet<>(assignments.globalRoles());
        roles.addAll(assignments.rolesFor(tenantId));
        authorities.addAll(AuthoritiesUtil.toAuthorities(roles));
        if (authentication instanceof OAuth2AuthenticationToken oauth2) {
            OAuth2AuthenticationToken visitor =
                    new OAuth2AuthenticationToken(oauth2.getPrincipal(), authorities, oauth2.getAuthorizedClientRegistrationId());
            visitor.setDetails(oauth2.getDetails());
            return visitor;
        }
        JwtAuthenticationToken bearer = (JwtAuthenticationToken) authentication;
        JwtAuthenticationToken visitor = new JwtAuthenticationToken(bearer.getToken(), authorities, bearer.getName());
        visitor.setDetails(bearer.getDetails());
        return visitor;
    }

    private static Authentication signedInUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken || authentication instanceof JwtAuthenticationToken) {
            return authentication;
        }
        throw new IllegalStateException("A cross-tenant run is made for a signed-in user, whose memberships it reads - there is none here");
    }
}
