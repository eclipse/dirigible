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

package org.eclipse.dirigible.ide.workspace.wizard.project.create;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

public class ProjectTemplateType {

	private static final String PARAM_CATEGORY = "category";

	private static final String EXT_PROPERTIES = ".properties"; //$NON-NLS-1$

	private static final String SEPARATOR = "/"; //$NON-NLS-1$

	private static final String PARAM_DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String PARAM_CONTENT = "content"; //$NON-NLS-1$

	private static final String PARAM_IMAGE = "image"; //$NON-NLS-1$

	private static final String PARAM_IMAGE_PREVIEW = "preview"; //$NON-NLS-1$

	private static final String PARAM_NAME = "name"; //$NON-NLS-1$

	private static final String PROJECT_TEMPLATE_CONTENT_DOES_NOT_EXIST_AT_S = Messages.ProjectTemplateType_PROJECT_TEMPLATE_CONTENT_DOES_NOT_EXIST_AT_S;

	private static final String PROJECT_TEMPLATE_IMAGE_DOES_NOT_EXIST_AT_S = Messages.ProjectTemplateType_PROJECT_TEMPLATE_IMAGE_DOES_NOT_EXIST_AT_S;

	private static final String PROJECT_TEMPLATE_METADATA_DOES_NOT_EXIST_AT_S = Messages.ProjectTemplateType_PROJECT_TEMPLATE_METADATA_DOES_NOT_EXIST_AT_S;

	private static final String PROJECT_TEMPLATE_LOCATION_S_IS_NOT_VALID = Messages.ProjectTemplateType_PROJECT_TEMPLATE_LOCATION_S_IS_NOT_VALID;

	private String location;

	private String name;

	private String description;

	private Image image;

	private Image imagePreview;

	private String contentPath;

	private String category;

	private static final ResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());

	public static ProjectTemplateType createTemplateType(IRepository repository, String location)
			throws IOException {
		ICollection projectTemplateRoot = repository.getCollection(location);
		if (!projectTemplateRoot.exists()) {
			throw new IOException(String.format(PROJECT_TEMPLATE_LOCATION_S_IS_NOT_VALID, location));
		}
		IResource projectMetadataResource = projectTemplateRoot.getResource("project.properties"); //$NON-NLS-1$
		if (!projectMetadataResource.exists()) {
			throw new IOException(String.format(PROJECT_TEMPLATE_METADATA_DOES_NOT_EXIST_AT_S,
					location));
		}

		Properties projectMetadata = new Properties();
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(projectMetadataResource.getContent());
			projectMetadata.load(byteArrayInputStream);

			String name = (String) projectMetadata.get(PARAM_NAME); //$NON-NLS-1$
			String description = (String) projectMetadata.get(PARAM_DESCRIPTION); //$NON-NLS-1$
			String imageName = (String) projectMetadata.get(PARAM_IMAGE); //$NON-NLS-1$
			String imagePreviewName = (String) projectMetadata.get(PARAM_IMAGE_PREVIEW); //$NON-NLS-1$
			String contentName = (String) projectMetadata.get(PARAM_CONTENT); //$NON-NLS-1$

			Image image = createImageFromResource(projectTemplateRoot, imageName);
			Image imagePreview = createImageFromResource(projectTemplateRoot, imagePreviewName);

			IResource contentResource = projectTemplateRoot.getResource(contentName);
			if (!contentResource.exists()) {
				throw new IOException(String.format(PROJECT_TEMPLATE_CONTENT_DOES_NOT_EXIST_AT_S,
						contentName));
			}

			ProjectTemplateType templateType = new ProjectTemplateType(name, description, location,
					image, imagePreview, contentResource.getPath(), "template");
			return templateType;
		} finally {
			if (byteArrayInputStream != null) {
				byteArrayInputStream.close();
			}
		}
	}

	private static Image createImageFromResource(ICollection projectTemplateRoot, String imageName)
			throws IOException {
		IResource imageResource = projectTemplateRoot.getResource(imageName);
		if (!imageResource.exists()) {
			throw new IOException(String.format(PROJECT_TEMPLATE_IMAGE_DOES_NOT_EXIST_AT_S,
					imageName));
		}
		Image image = createImage(imageResource.getContent());
		return image;
	}

	public static ProjectTemplateType createGitTemplateType(File project)
			throws FileNotFoundException, IOException {

		Image image = null;
		Image imagePreview = null;
		String name = null;
		String imageName = null;
		String imagePreviewName = null;
		String description = null;
		String contentPath = null;
		String contentName = null;
		String category = null;
		Properties projectMetadata = new Properties();

		for (File nextFile : project.listFiles()) {
			String fileName = nextFile.getName();

			if (isPropertiesFile(fileName)) {
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(nextFile);
					projectMetadata.load(fileInputStream);

					name = projectMetadata.getProperty(PARAM_NAME);
					imageName = projectMetadata.getProperty(PARAM_IMAGE);
					imagePreviewName = projectMetadata.getProperty(PARAM_IMAGE_PREVIEW);
					contentName = projectMetadata.getProperty(PARAM_CONTENT);
					description = projectMetadata.getProperty(PARAM_DESCRIPTION);

					contentPath = project.getCanonicalPath() + SEPARATOR + contentName;
					category = projectMetadata.getProperty(PARAM_CATEGORY);
					image = createImageFromStream(project, imageName);
					imagePreview = createImageFromStream(project, imagePreviewName);
				} finally {
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				}
			}
		}

		ProjectTemplateType templateType = new ProjectTemplateType(name, description,
				project.getCanonicalPath(), image, imagePreview, contentPath, category);
		return templateType;
	}

	private static Image createImageFromStream(File project, String imageName) throws IOException,
			FileNotFoundException {
		Image image;
		String imgPath = project.getCanonicalPath() + SEPARATOR + imageName;
		File imgFile = new File(imgPath);
		byte[] imgContent = IOUtils.toByteArray(new FileInputStream(imgFile));
		image = createImage(imgContent);
		return image;
	}

	private ProjectTemplateType(String name, String description, String location, Image image,
			Image imagePreview, String contentPath, String category) {
		super();
		this.name = name;
		this.description = description;
		this.location = location;
		this.image = image;
		this.imagePreview = imagePreview;
		this.contentPath = contentPath;
		this.category = category;
	}

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

	public String getLocation() {
		return location;
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

	public Image getImagePreview() {
		return imagePreview;
	}

	public void setImagePreview(Image imagePreview) {
		this.imagePreview = imagePreview;
	}

	private static Image createImage(byte[] data) {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageData(new ImageData(
				new ByteArrayInputStream(data)));
		return resourceManager.createImage(imageDescriptor);
	}

	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public static boolean isPropertiesFile(String fileName) {
		String suffix = EXT_PROPERTIES;
		if (fileName.endsWith(suffix)) {
			return true;
		} else {
			return false;
		}
	}
}
