/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.test;

import java.util.ArrayList;
import java.util.List;

public class Process {

	private String name;

	private List<Task> tasks = new ArrayList<Task>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public Task createTask(String taskName) {
		Task task = new Task();
		task.setName(taskName);
		tasks.add(task);
		return task;
	}

	public boolean existsTask(Task task) {
		return tasks.contains(task);
	}

}
