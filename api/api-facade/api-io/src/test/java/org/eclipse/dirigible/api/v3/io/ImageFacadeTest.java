package org.eclipse.dirigible.api.v3.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ImageFacadeTest {

	@Test
	public void resizeTest() throws IOException {
		InputStream original = ImageFacadeTest.class.getResourceAsStream("/dirigible.png");
		InputStream result = ImageFacade.resize(original, "png", 300, 155);
		FileOutputStream out = new FileOutputStream("./target/dirigible_output.png");
		IOUtils.copy(result, out);
	}

}
