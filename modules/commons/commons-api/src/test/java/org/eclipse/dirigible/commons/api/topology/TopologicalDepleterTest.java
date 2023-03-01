/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.topology;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TopologicalDepleterTest {

	@Test
	public void testDepleted() {
		System.out.println("Depleted:");
		
		DepletableNode nodeA = new DepletableNode("A", 0);
		DepletableNode nodeB = new DepletableNode("B", 3);
		DepletableNode nodeC = new DepletableNode("C", 2);
		DepletableNode nodeD = new DepletableNode("D", 0);
		DepletableNode nodeE = new DepletableNode("E", 4);
		DepletableNode nodeF = new DepletableNode("F", 3);
		DepletableNode nodeG = new DepletableNode("G", 5);
		DepletableNode nodeH = new DepletableNode("H", 1);
		DepletableNode nodeI = new DepletableNode("I", 1);
		
		List<DepletableNode> list = new ArrayList<>();
		list.add(nodeG);
		list.add(nodeB);
		list.add(nodeH);
		list.add(nodeA);
		list.add(nodeD);
		list.add(nodeE);
		list.add(nodeF);
		list.add(nodeI);
		list.add(nodeC);
		
//		A
//		D
//		H
//		I
//		C
//		B
//		F
//		E
//		G
		
		TopologicalDepleter<DepletableNode> depleter = new TopologicalDepleter<>();
		List<DepletableNode> results = depleter.deplete(list, "");
		for (ITopologicallyDepletable depletable : results) {
			System.out.println(depletable.getId());
		}
		
		assertEquals(results.size(), 0);		
		
	}
	
	@Test
	public void testNotDepleted() {
		System.out.println("Not depleted:");
		
		DepletableNode nodeA = new DepletableNode("A", 0);
		DepletableNode nodeB = new DepletableNode("B", 3);
		DepletableNode nodeC = new DepletableNode("C", 2);
		DepletableNode nodeD = new DepletableNode("D", 8);
		DepletableNode nodeE = new DepletableNode("E", 4);
		DepletableNode nodeF = new DepletableNode("F", 3);
		DepletableNode nodeG = new DepletableNode("G", 5);
		DepletableNode nodeH = new DepletableNode("H", 1);
		DepletableNode nodeI = new DepletableNode("I", 1);
		
		List<DepletableNode> list = new ArrayList<>();
		list.add(nodeG);
		list.add(nodeB);
		list.add(nodeH);
		list.add(nodeA);
		list.add(nodeD);
		list.add(nodeE);
		list.add(nodeF);
		list.add(nodeI);
		list.add(nodeC);
		
//		A
//		H
//		I
//		C
//		B
//		F
//		E
//		G
//		D
		
		TopologicalDepleter<DepletableNode> depleter = new TopologicalDepleter<>();
		List<DepletableNode> results = depleter.deplete(list, "");
		for (ITopologicallyDepletable depletable : results) {
			System.out.println(depletable.getId() + " remained");
		}
		
		assertEquals(results.size(), 1);		
		
	}
	
	public static class DepletableNode implements ITopologicallyDepletable {
		
		public String id;
		
		int completable;
		
		public DepletableNode(String id, int completable) {
			this.id = id;
			this.completable = completable;
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public boolean complete(String flow) {
			if (completable == 0) {
				System.out.println(this.id);
				return true;
			}
			--completable;
			return false;
		}
		
	}

}
