package org.eclipse.dirigible.api.v3.io;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for working with images
 */
public class ImageFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(FilesFacade.class);

	/**
	 * Resize an image to the given boundaries
	 * @param path path to file
	 * @return the created input stream
	 * @throws IOException in case of failure in underlying layer
	 */
	public static final InputStream resize(InputStream original, String type, int width, int height) throws IOException {
        BufferedImage bufferedImage = javax.imageio.ImageIO.read(original);
        Image scaledImage = bufferedImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
       
        int bufferedImageType = bufferedImage.getType();
        BufferedImage buffer = new BufferedImage(width, height, bufferedImageType);
        buffer.getGraphics().drawImage(scaledImage, 0, 0, null);
       
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(buffer, type, temp);
                       
        temp.flush();
		return temp.toInputStream();
	}

}
