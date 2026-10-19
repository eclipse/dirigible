/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.initializers.synchronizer.tenants;

import java.util.Set;

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: forgets the tenant's artefact outcomes - its rows of the per-tenant artefact
 * ledger, from which the activation state of the tenant is read.
 */
@Component
class TenantArtefactLedgerTenantTeardown implements TenantTeardownParticipant {

    /** The order of this participant: a row describing the tenant. */
    static final int ORDER = 500;

    /** The ledger. */
    private final TenantArtefactLedgerService ledger;

    /**
     * Instantiates a new tenant artefact ledger teardown.
     *
     * @param ledger the ledger
     */
    TenantArtefactLedgerTenantTeardown(TenantArtefactLedgerService ledger) {
        this.ledger = ledger;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        ledger.clear(Set.of(tenantId));
    }
}
