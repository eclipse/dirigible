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
package org.eclipse.dirigible.components.base.artefact.topology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TopologicalDepleter.
 *
 * @param <T> the generic type
 */
public class TopologicalDepleter<T extends TopologicallyDepletable> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TopologicalDepleter.class);

    /**
     * Deplete.
     *
     * @param list the list
     * @param flow the flow
     * @return the list
     */
    public List<T> deplete(List<T> list, ArtefactPhase flow) {
        List<T> depletables = new ArrayList<>();
        depletables.addAll(list);
        int count = depletables.size();
        boolean repeat = true;
        do {
            Iterator<T> iterator = depletables.iterator();
            while (iterator.hasNext()) {
                TopologicallyDepletable depletable = iterator.next();
                try {
                    if (depletable.complete(flow)) {
                        iterator.remove();
                    }
                } catch (Exception e) {
                    logger.error("Error has been thrown on depleting artefact: [{}] at phase: [{}]", depletable.getId(), flow.getValue());
                    iterator.remove();
                }
            }
            repeat = count > depletables.size();
            count = depletables.size();
        } while (repeat);
        return depletables;
    }

}
