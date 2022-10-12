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
package org.eclipse.dirigible.components.base.artefact.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;

/**
 * A factory for creating Topology objects.
 */
public class TopologyFactory {
	
	/**
	 * Wrap.
	 *
	 * @param artefacts the artefacts
	 * @param synchronizers the synchronizers
	 * @return the list< topology wrapper<? extends artefact>>
	 */
	public static final List<TopologyWrapper<? extends Artefact>> wrap(List<? extends Artefact> artefacts, List<Synchronizer<Artefact>> synchronizers) {
		List<TopologyWrapper<? extends Artefact>> list = new ArrayList<TopologyWrapper<? extends Artefact>>();
		Map<String, TopologyWrapper<? extends Artefact>> wrappers = new HashMap<String, TopologyWrapper<? extends Artefact>>();
		for (Artefact artefact : artefacts) {
			for (Synchronizer<Artefact> synchronizer : synchronizers) {
				if (synchronizer.isAccepted(artefact.getType())) {
					TopologyWrapper<? extends Artefact> wrapper = new TopologyWrapper(artefact, wrappers, synchronizer);
					list.add(wrapper);
					break;
				}
			}
		}
		return list;
	}

}
