/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.CrossTenantResult;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.util.AuthoritiesUtil;
import org.eclipse.dirigible.components.configurations.tenant.TenantConfigurationService;
import org.eclipse.dirigible.components.tenants.audit.CrossTenantAuditAction;
import org.eclipse.dirigible.components.tenants.audit.CrossTenantAuditService;
import org.eclipse.dirigible.components.tenants.tenant.TenantSelectionConstants;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.tenant.DirigibleTestTenant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * One identity - an accountant of a practice - holding a role in two client tenants at once, with
 * the token-groups tenant resolution: they see both, switch between them without signing in again
 * and act on each tenant's own data, lose a tenant on revocation from the very next request, run a
 * bulk operation that visits each granted tenant with that tenant's roles, and every step lands in
 * the cross-tenant audit trail, which each tenant's administrators read for their own tenant only.
 */
// One Dirigible boot for the whole class: provisioning a tenant is expensive.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Tag("slow")
class CrossTenantRoleIT extends IntegrationTest {

    private static final String TENANT_SELECTION_ENDPOINT = "/services/security/tenant-selection";
    private static final String TENANT_CONFIGURATIONS_PATH = "/services/core/configurations/tenant";
    private static final String AUDIT_ENDPOINT = "/services/security/cross-tenant-audit";

    private static final String MARKER_KEY = "CROSS_TENANT_ROLE_IT_MARKER";
    private static final String MARKER_OF_ACME = "marker-of-acme";
    private static final String MARKER_OF_GLOBEX = "marker-of-globex";

    private static final String APP_ID = "ledger";
    private static final String GROUPS_CLAIM = "groups";
    private static final String ACCOUNTANT = "accountant@practice.example";

    /** The configurations endpoint the tenant data is read through is for administrators. */
    private static final String ADMINISTRATOR_GROUP = "ADMINISTRATOR";

    private static DirigibleTestTenant acme;
    private static DirigibleTestTenant globex;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TenantContext tenantContext;

    @Autowired
    private TenantConfigurationService tenantConfigurationService;

    @Autowired
    private CrossTenantRunner crossTenantRunner;

    @Autowired
    private CrossTenantAuditService audit;

    @BeforeAll
    static void useTokenGroupsResolution() {
        DirigibleConfig.MULTI_TENANT_MODE_ENABLED.setBooleanValue(true);
        DirigibleConfig.TENANT_RESOLUTION_STRATEGY.setStringValue("TOKEN_GROUPS");
        DirigibleConfig.APP_ID.setStringValue(APP_ID);
        DirigibleConfig.TENANT_GROUPS_CLAIM.setStringValue(GROUPS_CLAIM);
    }

    @BeforeEach
    void provisionTenantsAndSeedMarkers() throws SQLException {
        if (acme != null) {
            return;
        }
        DirigibleTestTenant createdAcme = new DirigibleTestTenant("cross-tenant-acme-it");
        DirigibleTestTenant createdGlobex = new DirigibleTestTenant("cross-tenant-globex-it");
        createTenants(createdAcme, createdGlobex);
        waitForTenantProvisioning(createdAcme);
        waitForTenantProvisioning(createdGlobex);
        acme = createdAcme;
        globex = createdGlobex;
        seedMarker(acme.getId(), MARKER_OF_ACME);
        seedMarker(globex.getId(), MARKER_OF_GLOBEX);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void theAccountantSeesBothTenantsAndSwitchesBetweenThemOnEachTenantsData() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Authentication accountant = userOf(ADMINISTRATOR_GROUP, role(acme, "Accountant"), role(globex, "Accountant"));

        mvc.perform(get(TENANT_SELECTION_ENDPOINT).with(authentication(accountant)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.tenants.length()").value(2));

        select(session, accountant, acme);
        mvc.perform(get(TENANT_CONFIGURATIONS_PATH).session(session)
                                                   .with(authentication(accountant)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.key=='" + MARKER_KEY + "')].value", contains(MARKER_OF_ACME)));

        select(session, accountant, globex);
        mvc.perform(get(TENANT_CONFIGURATIONS_PATH).session(session)
                                                   .with(authentication(accountant)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.key=='" + MARKER_KEY + "')].value", contains(MARKER_OF_GLOBEX)));

        assertThat(audit.ofPrincipal(ACCOUNTANT, 500)).anySatisfy(record -> {
            assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.SWITCHED);
            assertThat(record.getTenantId()).isEqualTo(globex.getId());
            assertThat(record.getFromTenantId()).isEqualTo(acme.getId());
        });
    }

    /**
     * The request on which the revocation is first seen had its tenant scope opened in the revoked
     * tenant - it must not be served there.
     */
    @Test
    void aRevokedTenantIsLostFromTheVeryNextRequest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        select(session, userOf(ADMINISTRATOR_GROUP, role(acme, "Accountant"), role(globex, "Accountant")), globex);

        // globex revoked the practice: the next token carries acme only.
        Authentication revoked = userOf(ADMINISTRATOR_GROUP, role(acme, "Accountant"));
        mvc.perform(get(TENANT_CONFIGURATIONS_PATH).session(session)
                                                   .with(authentication(revoked)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.key=='" + MARKER_KEY + "')].value", contains(MARKER_OF_ACME)));

        assertThat(session.getAttribute(TenantSelectionConstants.SELECTED_TENANT_ID_SESSION_ATTRIBUTE)).isEqualTo(acme.getId());
        assertThat(audit.ofTenant(globex.getId(), 500)).anySatisfy(record -> {
            assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.REVOKED);
            assertThat(record.getPrincipal()).isEqualTo(ACCOUNTANT);
        });
        mvc.perform(post(TENANT_SELECTION_ENDPOINT).session(session)
                                                   .with(authentication(revoked))
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("{\"tenantId\":\"" + globex.getId() + "\"}"))
           .andExpect(status().isForbidden())
           .andExpect(jsonPath("$.reason").value("NOT_A_MEMBER"));
        assertThat(audit.ofTenant(globex.getId(), 500)).anySatisfy(record -> {
            assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.REFUSED);
            assertThat(record.getDetail()).isEqualTo("NOT_A_MEMBER");
        });
    }

    @Test
    void aBulkRunVisitsEachGrantedTenantWithItsDataAndItsRoles() {
        setAuthentication(userOf(role(acme, "Accountant"), role(globex, "Viewer")));
        Map<String, Set<String>> rolesSeen = new LinkedHashMap<>();

        List<CrossTenantResult<String>> results = crossTenantRunner.forEachGrantedTenant("period-close-it", tenantId -> {
            rolesSeen.put(tenantId, Set.copyOf(AuthoritiesUtil.toRoleNames(SecurityContextHolder.getContext()
                                                                                                .getAuthentication()
                                                                                                .getAuthorities())));
            return readMarker();
        });

        assertThat(results).containsExactly(new CrossTenantResult<>(acme.getId(), true, MARKER_OF_ACME, null),
                new CrossTenantResult<>(globex.getId(), true, MARKER_OF_GLOBEX, null));
        assertThat(rolesSeen.get(acme.getId())).containsExactly("Accountant");
        assertThat(rolesSeen.get(globex.getId())).containsExactly("Viewer");
        assertThat(audit.ofTenant(acme.getId(), 500)).anySatisfy(record -> {
            assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.RUN_SUCCEEDED);
            assertThat(record.getOperation()).isEqualTo("period-close-it");
        });
    }

    @Test
    void anOwnerReadsTheTrailOfTheirOwnTenantOnly() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Authentication owner = userOf(role(acme, "Owner"), role(globex, "Viewer"));
        select(session, owner, globex);
        select(session, owner, acme);

        mvc.perform(get(AUDIT_ENDPOINT).session(session)
                                       .with(authentication(owner)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[*].action", hasItem("SWITCHED")))
           // A record of another tenant shows only as the switch away from acme.
           .andExpect(jsonPath("$[?(@.tenantId != '" + acme.getId() + "')].fromTenantId", everyItem(is(acme.getId()))));

        // A Viewer of globex is no administrator there.
        select(session, owner, globex);
        mvc.perform(get(AUDIT_ENDPOINT).session(session)
                                       .with(authentication(owner)))
           .andExpect(status().isForbidden());

        // Their own trail is theirs to read in any tenant.
        mvc.perform(get(AUDIT_ENDPOINT + "/mine").session(session)
                                                 .with(authentication(owner)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[*].principal", everyItem(is(ACCOUNTANT))));
    }

    private void select(MockHttpSession session, Authentication user, DirigibleTestTenant tenant) throws Exception {
        mvc.perform(post(TENANT_SELECTION_ENDPOINT).session(session)
                                                   .with(authentication(user))
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("{\"tenantId\":\"" + tenant.getId() + "\"}"))
           .andExpect(status().isOk());
    }

    private String readMarker() {
        try {
            return tenantConfigurationService.readStoredForCurrentTenant(MARKER_KEY);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void seedMarker(String tenantId, String marker) throws SQLException {
        try {
            tenantContext.execute(tenantId, () -> {
                tenantConfigurationService.set(MARKER_KEY, marker);
                return null;
            });
        } catch (SQLException | RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to seed the marker of tenant [" + tenantId + "]", ex);
        }
    }

    private static String role(DirigibleTestTenant tenant, String role) {
        return tenant.getId() + "." + APP_ID + "." + role;
    }

    private static void setAuthentication(Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * An authenticated user whose groups are the given ones - what an OIDC login leaves behind.
     */
    private static Authentication userOf(String... groups) {
        OidcIdToken idToken = new OidcIdToken("id-token", Instant.now(), Instant.now()
                                                                                .plusSeconds(300),
                Map.of("sub", ACCOUNTANT, GROUPS_CLAIM, List.of(groups)));
        OidcUser oidcUser = new DefaultOidcUser(List.of(new SimpleGrantedAuthority("ROLE_USER")), idToken);
        return new OAuth2AuthenticationToken(oidcUser, List.of(), "keycloak");
    }
}
