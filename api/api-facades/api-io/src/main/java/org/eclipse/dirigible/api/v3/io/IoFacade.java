package org.eclipse.dirigible.api.v3.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

public class IoFacade {
	
	public static final int read(InputStream input) throws IOException {
		return input.read();
	}
	
	public static final void close(InputStream input) throws IOException {
		input.close();
	}
	
	public static final void write(OutputStream output, int value) throws IOException {
		output.write(value);
	}
	
	public static final void close(OutputStream output) throws IOException {
		output.close();
	}
	
	public static final void copy(InputStream input, OutputStream output) throws IOException {
		IOUtils.copy(input, output);
	}
	
	public static final void copy(ByteArrayInputStream input, ByteArrayOutputStream output) throws IOException {
		IOUtils.copy(input, output);
	}
	
	public static final ByteArrayInputStream createByteArrayInputStream(byte[] input) throws IOException {
		return new ByteArrayInputStream(input);
	}
	
	public static final ByteArrayInputStream createByteArrayInputStream(String input) throws IOException {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return new ByteArrayInputStream(bytes);
	}
	
	public static final ByteArrayOutputStream createByteArrayOutputStream() throws IOException {
		return new ByteArrayOutputStream();
	}
	
	public static final byte[] getBytes(ByteArrayOutputStream output) throws IOException {
		byte[] bytes = output.toByteArray();
		return bytes;
	}

}
