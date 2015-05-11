/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

public class TemplateType {

	private static final String DOT = ".";

	private String text;

	private String location;

	private Image image;

	private Set<String> validParameters = new HashSet<String>();

	private static final ResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());

	public static TemplateType createTemplateType(String type, String location,
			String imageLocation, Class<?> loader, String... parameters)
			throws MalformedURLException {
		Image image = createImage(loader.getResource(imageLocation));
		TemplateType templateType = new TemplateType(type, location, image);
		for (int i = 0; i < parameters.length; i++) {
			templateType.getValidParameters().add(parameters[i]);
		}
		return templateType;
	}

	private TemplateType(String text, String location, Image image) {
		super();
		this.text = text;
		this.location = location;
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLocation() {
		return location;
	}

	public String getExtension() {
		int dotIndex = location.lastIndexOf(DOT);
		if(dotIndex!=-1){
			return location.substring(dotIndex+1);
		}
		return null;
	}

	public void setLocation(String location) {
		this.location = location;
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

}
