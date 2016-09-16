package org.eclipse.dirigible.repository.project;

public class ProjectMetadata {

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
		return licenses;
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
		return dependencies;
	}

	public void setDependencies(ProjectMetadataDependency[] dependencies) {
		this.dependencies = dependencies;
	}

}
