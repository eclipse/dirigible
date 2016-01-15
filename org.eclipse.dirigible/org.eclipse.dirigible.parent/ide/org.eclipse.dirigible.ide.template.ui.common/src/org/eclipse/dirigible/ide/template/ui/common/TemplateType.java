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
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

import com.google.gson.Gson;

public class TemplateType {

	private static final String DOT = ".";

	private String name;

	private String[] sourceNames;

	private String[] sourceLocations;

	private boolean[] sourceGenerates;

	private String[] sourceRenamings;

	private Image image;

	private Set<String> validParameters = new HashSet<String>();

	private static Gson gson = new Gson();

	private static final ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	// public static TemplateType createTemplateType(String type, String location,
	// String imageLocation, Class<?> loader, String... parameters)
	// throws MalformedURLException {
	// Image image = createImage(loader.getResource(imageLocation));
	// TemplateType templateType = new TemplateType(type, location, image);
	// for (int i = 0; i < parameters.length; i++) {
	// templateType.getValidParameters().add(parameters[i]);
	// }
	// return templateType;
	// }

	public static TemplateType createTemplateType(String type, String location) throws IOException {
		IRepository repository = RepositoryFacade.getInstance().getRepository();
		ICollection templateRoot = repository.getCollection(location);
		if (!templateRoot.exists()) {
			throw new IOException(String.format("Template location: %s is not valid", location));
		}
		IResource templateMetadataResource = templateRoot.getResource("template.json"); //$NON-NLS-1$
		if (!templateMetadataResource.exists()) {
			throw new IOException(String.format("Template metadata does not exist at: %s", location));
		}

		TemplateMetadata templateMetadata = gson.fromJson(new String(templateMetadataResource.getContent()), TemplateMetadata.class);

		Image image = TemplateUtils.createImageFromResource(templateRoot, templateMetadata.getImage());

		TemplateSourceMetadata[] sources = templateMetadata.getSources();
		String[] names = new String[sources.length];
		String[] locations = new String[sources.length];
		boolean[] generates = new boolean[sources.length];
		String[] renamings = new String[sources.length];
		for (int i = 0; i < sources.length; i++) {
			IResource contentResource = templateRoot.getResource(sources[i].getName());
			if (!contentResource.exists()) {
				throw new IOException(String.format("Template source does not exist at: %s", contentResource.getPath()));
			}
			names[i] = sources[i].getName();
			locations[i] = contentResource.getPath();
			generates[i] = sources[i].isGenerate();
			renamings[i] = sources[i].getRename();
		}

		TemplateType templateType = new TemplateType(templateMetadata.getName(), names, locations, generates, renamings, image);
		return templateType;
	}

	private TemplateType(String name, String[] names, String[] locations, boolean[] generates, String[] renamings, Image image) {
		super();
		this.name = name;
		this.sourceNames = names;
		this.sourceLocations = locations;
		this.sourceGenerates = generates;
		this.sourceRenamings = renamings;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getSourceNames() {
		return sourceNames;
	}

	public String[] getSourceLocations() {
		return sourceLocations;
	}

	public boolean[] getSourceGenerates() {
		return sourceGenerates;
	}

	public String[] getSourceRenamings() {
		return sourceRenamings;
	}

	public String getExtension() {
		return getExtensionFor(0);
	}

	public String getExtensionFor(int i) {
		int dotIndex = sourceLocations[i].lastIndexOf(DOT);
		if (dotIndex != -1) {
			return sourceLocations[i].substring(dotIndex + 1);
		}
		return "";
	}

	public void setLocations(String[] locations) {
		this.sourceLocations = locations;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	private static Image createImage(URL imageURL) {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(imageURL);
		return resourceManager.createImage(imageDescriptor);
	}

	public Set<String> getValidParameters() {
		return validParameters;
	}

	public String getLocation() {
		return this.sourceLocations[0];
	}

}
