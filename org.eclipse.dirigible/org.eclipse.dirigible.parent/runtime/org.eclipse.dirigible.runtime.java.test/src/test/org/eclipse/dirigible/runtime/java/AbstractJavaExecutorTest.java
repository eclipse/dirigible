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

package test.org.eclipse.dirigible.runtime.java;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;

import test.org.eclipse.dirigible.runtime.java.executors.JavaExecutorStub;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.runtime.java.dynamic.compilation.ClassFileManager;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

public abstract class AbstractJavaExecutorTest implements IJavaExecutorTestResorces {

	private IRepository repository;
	private IScriptExecutor executor;
	
	private PrintStream backupOut;
	private PrintStream backupErr;

	private ByteArrayOutputStream baos;
	private PrintStream printStream;

	@Before
	public void setUp() throws Exception {
		repository = new DBRepository(createLocal(), USER, true);
		executor = createExecutor();
		
		backupOut = System.out;
		backupErr = System.err;
		
		baos = new ByteArrayOutputStream();
		printStream = new PrintStream(baos);
		
		System.setOut(printStream);
		System.setErr(printStream);
	}

	protected IScriptExecutor createExecutor() {
		try {
			return new JavaExecutorStub(getRepository(), ClassFileManager.getJars(getLibDirectory()), getRootPaths());
		} catch (IOException e) {
			System.setErr(printStream);
			fail(e.getMessage());
		}
		return null;
	}

	protected IRepository getRepository() {
		return repository;
	}
	
	protected File getLibDirectory() {
		return new File(LIB_DIRECTORY);
	}
	
	protected String[] getRootPaths() {
		return new String[] { REPOSITORY_PUBLIC_DEPLOY_PATH };
	}

	private static DataSource createLocal() {
		EmbeddedDataSource dataSource = new EmbeddedDataSource();
		dataSource.setDatabaseName("derby"); //$NON-NLS-1$
		dataSource.setCreateDatabase("create"); //$NON-NLS-1$
		return dataSource;
	}
	
	@After
	public void tearDown() throws Exception {
		if (baos != null) {
			baos.close();
		}
		if(printStream != null){
			printStream.close();
		}
		System.setOut(backupOut);
		System.setErr(backupErr);
	}

	protected String getOutput() throws IOException {
		String output = null;
		if(baos != null){
			output = baos.toString();
			baos.reset();
		}
		return output;
	}

	protected byte[] readSource(File source) throws IOException{
		return FileUtils.readFileToString(source).getBytes();
	}
	
	protected long getExecutionTime(String module) throws IOException {
		long startTime = System.currentTimeMillis();
		execute(module);
		return System.currentTimeMillis() - startTime;
	}
	
	protected Object execute(String module) throws IOException {
		return executor.executeServiceModule(null, null, module, null);
	}
	
	protected void createResource(String path, File source) throws IOException {
		getRepository().createResource(path, readSource(source));
	}
	
	protected void assertCacheExecutionTime(long firstExecutionTime, long secondExecutionTime) {
		String format = "Java's compiler cache is not working. Second execution time \"%d ms\" (execution with cache) should be less than first execution time \"%d ms\""; 
		String message = String.format(format, secondExecutionTime, firstExecutionTime);
		assertTrue(message, firstExecutionTime > secondExecutionTime);
	}
	
}
