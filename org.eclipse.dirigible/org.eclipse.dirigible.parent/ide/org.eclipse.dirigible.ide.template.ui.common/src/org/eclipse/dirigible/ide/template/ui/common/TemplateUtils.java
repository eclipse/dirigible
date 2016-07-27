package org.eclipse.dirigible.ide.template.ui.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class TemplateUtils {

	private static final ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	public static Image createImageFromResource(ICollection collection, String imageName) throws IOException {
		IResource imageResource = collection.getResource(imageName);
		if (!imageResource.exists()) {
			throw new IOException(String.format("Template image does not exist at: %s", imageName));
		}
		Image image = createImage(imageResource.getContent());
		return image;
	}

	public static Image createImageFromResource(String imagePath) throws IOException {
		IResource imageResource = RepositoryFacade.getInstance().getRepository().getResource(imagePath);
		if (!imageResource.exists()) {
			throw new IOException(String.format("Template image does not exist at: %s", imagePath));
		}
		Image image = createImage(imageResource.getContent());
		return image;
	}

	public static Image createImageFromStream(File project, String imageName) throws IOException, FileNotFoundException {
		Image image;
		String imgPath = project.getCanonicalPath() + IRepository.SEPARATOR + imageName;
		File imgFile = new File(imgPath);
		byte[] imgContent = IOUtils.toByteArray(new FileInputStream(imgFile));
		image = TemplateUtils.createImage(imgContent);
		return image;
	}

	private static Image createImage(byte[] data) {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageData(new ImageData(new ByteArrayInputStream(data)));
		return resourceManager.createImage(imageDescriptor);
	}

	public static byte[] normalizeEscapes(byte[] bytes) {
		String content = new String(bytes);
		content = content.replace("\\$", "$"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\{", "{"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\}", "}"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\[", "["); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\]", "]"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
		byte[] result = content.getBytes();
		return result;
	}

}
