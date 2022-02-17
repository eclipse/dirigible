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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TopologicalSorter<T extends ITopologicallySortable> {
	
	
    private Stack<TopologicalSorterNode<T>> stack;
 
    public TopologicalSorter() {
        this.stack = new Stack<>();
    }
    
    public List<T> sort(List<T> list) {
    	Map<String, TopologicalSorterNode<T>> nodes = new HashMap<>();
    	for (ITopologicallySortable sortable : list) {
    		TopologicalSorterNode<T> node = new TopologicalSorterNode(sortable, nodes);
    		nodes.put(sortable.getId(), node);
    	}
    	
    	for (TopologicalSorterNode<T> node : nodes.values()) {
	        topologicalSort(node);
    	}
    	
    	List<T> results = new ArrayList<>();
    	for (TopologicalSorterNode<T> node : stack) {
    		results.add(node.getData());
    	}
    	
    	return results;
    }

	private void topologicalSort(TopologicalSorterNode<T> node) {
		List<TopologicalSorterNode<T>> dependencies = node.getDependencies();
		for (int i = 0; i < dependencies.size(); i++) {
			TopologicalSorterNode<T> n = dependencies.get(i);
		    if (n != null && !n.isVisited()) {
		    	n.setVisited(true);
		    	topologicalSort(n);
		    }
		}
		if (!stack.contains(node)) {
			stack.push(node);
		}
	}

}
