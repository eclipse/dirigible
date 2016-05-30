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

package org.eclipse.dirigible.ide.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.conf.ConfigurationStore;
import org.eclipse.dirigible.repository.ext.conf.IConfigurationStore;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class AbstractRepositoryPreferenceStore implements IPreferenceStore {
	
	private static final long serialVersionUID = 1L;

	private static final String CHANAGED_BY = "Chanaged by ";

	private static final Logger logger = Logger.getLogger(AbstractRepositoryPreferenceStore.class);
	
	private PreferenceStore delegate;
	
	private String path;
	
	private String name;
	
	public AbstractRepositoryPreferenceStore(String path, String name) {
		delegate = new PreferenceStore();
		this.path = path;
		this.name = name;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		delegate.addPropertyChangeListener(listener);
	}

	public boolean contains(String name) {
		return delegate.contains(name);
	}

	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	public void firePropertyChangeEvent(String name, Object oldValue,
			Object newValue) {
		delegate.firePropertyChangeEvent(name, oldValue, newValue);
	}

	public boolean getBoolean(String name) {
		return delegate.getBoolean(name);
	}

	public boolean getDefaultBoolean(String name) {
		return delegate.getDefaultBoolean(name);
	}

	public double getDefaultDouble(String name) {
		return delegate.getDefaultDouble(name);
	}

	public float getDefaultFloat(String name) {
		return delegate.getDefaultFloat(name);
	}

	public int getDefaultInt(String name) {
		return delegate.getDefaultInt(name);
	}

	public long getDefaultLong(String name) {
		return delegate.getDefaultLong(name);
	}

	public String getDefaultString(String name) {
		return delegate.getDefaultString(name);
	}

	public double getDouble(String name) {
		return delegate.getDouble(name);
	}

	public float getFloat(String name) {
		return delegate.getFloat(name);
	}

	public int getInt(String name) {
		return delegate.getInt(name);
	}

	public long getLong(String name) {
		return delegate.getLong(name);
	}

	public String getString(String name) {
		return delegate.getString(name);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean isDefault(String name) {
		return delegate.isDefault(name);
	}

	public void list(PrintStream out) {
		delegate.list(out);
	}

	public void list(PrintWriter out) {
		delegate.list(out);
	}

	public void load() throws IOException {
//		delegate.load();
		IConfigurationStore configurationStorage = getConfigurationStore();
		byte[] bytes = loadSettings(configurationStorage);
		delegate.load(new ByteArrayInputStream(bytes));
	}

	protected abstract byte[] loadSettings(IConfigurationStore configurationStorage)
			throws IOException;
			
	public void load(InputStream in) throws IOException {
		delegate.load(in);
	}

	public boolean needsSaving() {
		return delegate.needsSaving();
	}

	public String[] preferenceNames() {
		return delegate.preferenceNames();
	}

	public void putValue(String name, String value) {
		delegate.putValue(name, value);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		delegate.removePropertyChangeListener(listener);
	}

	public void save() throws IOException {
//		delegate.save();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		delegate.save(baos, CHANAGED_BY + CommonIDEParameters.getUserName());
		saveSettingd(baos);
	}

	protected abstract void saveSettingd(ByteArrayOutputStream baos) throws IOException;
	

	public void save(OutputStream out, String header) throws IOException {
		delegate.save(out, header);
	}

	public void setDefault(String name, double value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, float value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, int value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, long value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, String value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, boolean value) {
		delegate.setDefault(name, value);
	}

	public void setFilename(String name) {
		delegate.setFilename(name);
	}

	public void setToDefault(String name) {
		delegate.setToDefault(name);
	}

	public void setValue(String name, double value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, float value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, int value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, long value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, String value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, boolean value) {
		delegate.setValue(name, value);
	}

	public String toString() {
		return delegate.toString();
	}

	
	protected static IConfigurationStore getConfigurationStore() {
		IRepository repository = RepositoryFacade.getInstance().getRepository();
		IConfigurationStore configurationStorage = 
				new ConfigurationStore(repository);
		return configurationStorage;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
}
