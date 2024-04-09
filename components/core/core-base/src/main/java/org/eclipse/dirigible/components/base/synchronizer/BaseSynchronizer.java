/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.synchronizer;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.TenantResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * The Class BaseSynchronizer.
 *
 */
public abstract class BaseSynchronizer<A extends Artefact, ID> implements Synchronizer<A, ID> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseSynchronizer.class);

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public final boolean complete(TopologyWrapper<A> wrapper, ArtefactPhase flow) {
        A artefact = wrapper.getArtefact();
        ArtefactLifecycle lifecycle = artefact.getLifecycle();

        if (!multitenantExecution() || !isMultitenantArtefact(artefact)) {
            logger.debug("[{} will complete artefact with lifecycle [{}] in phase [{}]]...\nArtefact:[{}]", this, lifecycle, flow,
                    artefact);
            return completeImpl(wrapper, flow);
        }

        TenantContext tenantContext = BeanProvider.getTenantContext();
        List<TenantResult<Boolean>> results = tenantContext.executeForEachTenant(() -> {
            logger.debug("[{} will complete artefact with lifecycle [{}] in phase [{}]] for tenant [{}]...\\nArtefact:[{}]", this,
                    lifecycle, flow, tenantContext.getCurrentTenant(), artefact);
            artefact.setLifecycle(lifecycle);
            return completeImpl(wrapper, flow);
        });

        return results.stream()
                      .map(TenantResult::getResult)
                      .allMatch(r -> Boolean.TRUE.equals(r));
    }

    /**
     * Multitenant execution.
     *
     * @return true, if successful
     */
    @Override
    public boolean multitenantExecution() {
        return false;
    }

    /**
     * Checks if is multitenant artefact.
     *
     * @param artefact the artefact
     * @return true, if is multitenant artefact
     */
    protected boolean isMultitenantArtefact(A artefact) {
        return false;
    }

    /**
     * Complete impl.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    protected abstract boolean completeImpl(TopologyWrapper<A> wrapper, ArtefactPhase flow);

    /**
     * Cleanup.
     *
     * @param artefact the artefact
     */
    public final void cleanup(A artefact) {
        if (!multitenantExecution() || !isMultitenantArtefact(artefact)) {
            logger.debug("[{} will cleanup artefact [{}]", this, artefact);
            cleanupImpl(artefact);
            return;
        }

        ArtefactLifecycle lifecycle = artefact.getLifecycle();

        TenantContext tenantContext = BeanProvider.getTenantContext();
        tenantContext.executeForEachTenant(() -> {
            logger.debug("[{} will cleanup artefact [{}] for tenant [{}]", this, artefact, tenantContext.getCurrentTenant());
            artefact.setLifecycle(lifecycle);
            cleanupImpl(artefact);
            return null;
        });
    }

    /**
     * Cleanup impl.
     *
     * @param artefact the artefact
     */
    protected abstract void cleanupImpl(A artefact);

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString()
                   .endsWith(getFileExtension());
    }
}
