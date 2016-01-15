package org.eclipse.dirigible.ide.template.ui.common;

public class TemplateMetadata {

	private String name;

	private String description;

	private String image;

	private TemplateSourceMetadata[] sources;

	private TemplateParameterMetadata[] parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public TemplateSourceMetadata[] getSources() {
		return sources;
	}

	public void setSources(TemplateSourceMetadata[] sources) {
		this.sources = sources;
	}

	public TemplateParameterMetadata[] getParameters() {
		return parameters;
	}

	public void setParameters(TemplateParameterMetadata[] parameters) {
		this.parameters = parameters;
	}

	// public static void main(String[] args) {
	// String source = "{\"name\":\"table\"," + " \"description\":\"Relational Database Table\"," + "
	// \"image\":\"table.png\"," + " \"sources\":["
	// + " {\"name\":\"table.table\"}" + " ]," + " \"parameters\":[" + " {\"name\":\"name\"," + "
	// \"required\":\"true\"}" + " ]"
	// + "}";
	// Gson gson = new Gson();
	// TemplateMetadata templateMetadata = gson.fromJson(source, TemplateMetadata.class);
	// System.out.println(gson.toJson(templateMetadata));
	// }

}
