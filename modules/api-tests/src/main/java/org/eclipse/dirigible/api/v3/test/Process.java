package org.eclipse.dirigible.api.v3.test;

import java.util.List;

public class Process {

	private String name;

	private List<Task> tasks;

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
		return task;
	}

}
