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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TopologicalSorterTest {

	@Test
	public void test() {
		SortableNode nodeA = new SortableNode("A");
		SortableNode nodeB = new SortableNode("B_A", nodeA);
		SortableNode nodeC = new SortableNode("C_A", nodeA);
		SortableNode nodeD = new SortableNode("D_AB", nodeA, nodeB);
		SortableNode nodeE = new SortableNode("E_CD", nodeC, nodeD);
		SortableNode nodeF = new SortableNode("F");
		SortableNode nodeG = new SortableNode("G_F", nodeF);
		SortableNode nodeH = new SortableNode("H_FA", nodeF, nodeA);
		SortableNode nodeI = new SortableNode("I_FC", nodeF, nodeC);
		
		List<TopologicallySortable> list = new ArrayList<>();
//		list.add(nodeG);
//		list.add(nodeB);
//		list.add(nodeH);
//		list.add(nodeA);
//		list.add(nodeD);
//		list.add(nodeE);
//		list.add(nodeF);
//		list.add(nodeI);
//		list.add(nodeC);
		
		list.add(nodeC);
		list.add(nodeD);
		list.add(nodeG);
		list.add(nodeB);
		list.add(nodeI);
		list.add(nodeH);
		list.add(nodeA);
		list.add(nodeE);
		list.add(nodeF);
		
		
		
		TopologicalSorter sorter = new TopologicalSorter();
		List<TopologicallySortable> results = sorter.sort(list);
		for (TopologicallySortable sortable : results) {
			System.out.println(sortable.getId());
		}
		
		assertEquals(results.get(0).getId(), "F");
		
//		F
//		A
//		H_FA
//		C_A
//		I_FC
//		B_A
//		D_AB
//		E_CD
//		G_F

		
		
	}
	
	public static class SortableNode implements TopologicallySortable {
		
		public String id;
		
		public List<TopologicallySortable> dependencies;
		
		public SortableNode(String id, TopologicallySortable ... dependencies) {
			this.id = id;
			this.dependencies = Arrays.asList(dependencies);
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public List<TopologicallySortable> getDependencies() {
			return this.dependencies;
		}
		
	}

}
