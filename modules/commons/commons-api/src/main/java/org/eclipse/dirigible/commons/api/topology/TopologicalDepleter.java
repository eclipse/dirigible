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
package org.eclipse.dirigible.commons.api.topology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TopologicalDepleter {
	
	public List<ITopologicallyDepletable> deplete(List<ITopologicallyDepletable> list) {
		List<ITopologicallyDepletable> depletables = new ArrayList<ITopologicallyDepletable>();
		depletables.addAll(list);
		int count = depletables.size();
		boolean repeat = true;
		do {
			Iterator<ITopologicallyDepletable> iterator = depletables.iterator();
			while (iterator.hasNext()) {
				ITopologicallyDepletable depletable = iterator.next();
				if (depletable.complete()) {
					iterator.remove();
				}
			}
			repeat = count > depletables.size();
			count = depletables.size();
		} while (repeat);
		return depletables;
	}

}
