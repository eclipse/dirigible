package org.eclipse.dirigible.api.v3.io;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class FilesFacadeTest {

	@Test
	public void traverseTest() throws IOException {
		String json = FilesFacade.traverse(".");
		System.out.println(json);
		assertTrue(json.contains("FilesFacadeTest.class"));
	}
	
	@Test
	public void listTest() throws IOException {
		String json = FilesFacade.list(".");
		System.out.println(json);
		assertTrue(json.contains("about.html"));
	}

}
