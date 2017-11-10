/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.project;

public class ProjectMetadata {

	public static final String PROJECT_METADATA_FILE_NAME = "project.json"; //$NON-NLS-1$

	private String guid;

	private String name;

	private String component;

	private String description;

	private String author;

	private ProjectMetadataLicense[] licenses;

	private ProjectMetadataRepository repository;

	private ProjectMetadataDependency[] dependencies;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ProjectMetadataLicense[] getLicenses() {
		return licenses != null ? licenses.clone() : new ProjectMetadataLicense[] {};
	}

	public void setLicenses(ProjectMetadataLicense[] licenses) {
		this.licenses = licenses;
	}

	public ProjectMetadataRepository getRepository() {
		return repository;
	}

	public void setRepository(ProjectMetadataRepository repository) {
		this.repository = repository;
	}

	public ProjectMetadataDependency[] getDependencies() {
		return dependencies != null ? dependencies.clone() : new ProjectMetadataDependency[] {};
	}

	public void setDependencies(ProjectMetadataDependency[] dependencies) {
		this.dependencies = dependencies;
	}

}
