/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import java.io.IOException;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

import com.google.gson.Gson;

public class TemplateType {

	private static final String DOT = ".";

	private TemplateMetadata templateMetadata;

	private static Gson gson = new Gson();

	public static TemplateType createTemplateType(String category, String location, String type) throws IOException {
		return createTemplateType(category, location, type, null);
	}

	public static TemplateType createTemplateType(String category, String location, String type, IRepository repository) throws IOException {

		if (repository == null) {
			repository = RepositoryFacade.getInstance().getRepository();
		}

		ICollection templateRoot = repository.getCollection(location);
		if (!templateRoot.exists()) {
			throw new IOException(String.format("Template location: %s is not valid", location));
		}
		IResource templateMetadataResource = templateRoot.getResource("template.json"); //$NON-NLS-1$
		if (!templateMetadataResource.exists()) {
			throw new IOException(String.format("Template metadata does not exist at: %s", location));
		}

		TemplateMetadata templateMetadata = gson.fromJson(new String(templateMetadataResource.getContent()), TemplateMetadata.class);
		templateMetadata.setCategory(category);
		templateMetadata.setType(type);

		String image = templateRoot.getResource(templateMetadata.getImage()).getPath();
		templateMetadata.setImage(image);

		TemplateSourceMetadata[] sources = templateMetadata.getSources();
		for (TemplateSourceMetadata source : sources) {
			IResource contentResource = templateRoot.getResource(source.getName());
			if (!contentResource.exists()) {
				throw new IOException(String.format("Template source does not exist at: %s", contentResource.getPath()));
			}
			source.setLocation(contentResource.getPath());
		}

		TemplateType templateType = new TemplateType(templateMetadata);
		return templateType;
	}

	private TemplateType(TemplateMetadata templateMetadata) {
		super();
		this.templateMetadata = templateMetadata;
	}

	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// public String[] getSourceNames() {
	// return sourceNames;
	// }
	//
	// public String[] getSourceLocations() {
	// return sourceLocations;
	// }
	//
	// public boolean[] getSourceGenerates() {
	// return sourceGenerates;
	// }
	//
	// public String[] getSourceRenamings() {
	// return sourceRenamings;
	// }
	//
	public String getExtension() {
		return getExtensionFor(0);
	}

	public String getExtensionFor(int i) {
		int dotIndex = this.templateMetadata.getSources()[i].getLocation().lastIndexOf(DOT);
		if (dotIndex != -1) {
			return this.templateMetadata.getSources()[i].getLocation().substring(dotIndex + 1);
		}
		return "";
	}
	//
	// public void setLocations(String[] locations) {
	// this.sourceLocations = locations;
	// }
	//
	// public String getImage() {
	// return image;
	// }
	//
	// public void setImage(String image) {
	// this.image = image;
	// }

	// private static Image createImage(URL imageURL) {
	// // ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(imageURL);
	// // return resourceManager.createImage(imageDescriptor);
	// return null;
	// }

	// public Set<TemplateParameterMetadata> getValidParameters() {
	// return validParameters;
	// }
	//
	public String getLocation() {
		return this.templateMetadata.getSources()[0].getLocation();
	}

	public TemplateMetadata getTemplateMetadata() {
		return templateMetadata;
	}

}
