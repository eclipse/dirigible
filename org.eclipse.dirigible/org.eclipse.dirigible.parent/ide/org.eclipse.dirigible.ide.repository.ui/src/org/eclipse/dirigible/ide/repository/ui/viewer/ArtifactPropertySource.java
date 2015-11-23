/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.repository.ui.viewer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public final class ArtifactPropertySource implements IPropertySource {

	private static final Logger logger = Logger.getLogger(ArtifactPropertySource.class);

	private static final String PROPERTY_NAME = "Name"; //$NON-NLS-1$

	private static final String PROPERTY_PATH = "Path"; //$NON-NLS-1$

	// private static final String PROPERTY_OWNER = "Owner"; //$NON-NLS-1$
	//
	// private static final String PROPERTY_GROUP = "Group"; //$NON-NLS-1$
	//
	// private static final String PROPERTY_PERMISSIONS = "Permissions"; //$NON-NLS-1$

	private static final String PROPERTY_DATE_CREATED = "Created At"; //$NON-NLS-1$

	private static final String PROPERTY_USER_CREATED = "Created By"; //$NON-NLS-1$

	private static final String PROPERTY_DATE_MODIFIED = "Modified At"; //$NON-NLS-1$

	private static final String PROPERTY_USER_MODIFIED = "Modified By"; //$NON-NLS-1$

	// private static final String PROPERTY_SIZE = "Size"; //$NON-NLS-1$
	//
	// private static final String PROPERTY_MIME_TYPE = "Mime-type"; //$NON-NLS-1$

	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private final Map<String, Object> properties = new HashMap<String, Object>();

	public ArtifactPropertySource(IEntity entity) {
		IEntityInformation information = null;
		try {
			information = entity.getInformation();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (information != null) {
			fillProperties(information);
		}
	}

	private void fillProperties(IEntityInformation information) {
		properties.put(PROPERTY_NAME, information.getName());
		properties.put(PROPERTY_PATH, information.getPath());
		// properties.put(PROPERTY_OWNER, information.getOwner());
		// properties.put(PROPERTY_GROUP, information.getGroup());
		// final String permission =
		// PermissionsUtil.permissionsToString(information.getPermissions());
		// properties.put(PROPERTY_PERMISSIONS, permission);
		if (information.getCreatedAt() != null) {
			properties.put(PROPERTY_DATE_CREATED, dateFormatter.format(information.getCreatedAt())); // Date Created
		} else {
			logger.warn(String.format("Creation date of the resource %s is null", information.getPath())); //$NON-NLS-1$
		}
		properties.put(PROPERTY_USER_CREATED, information.getCreatedBy());// User Created
		if (information.getModifiedAt() != null) {
			properties.put(PROPERTY_DATE_MODIFIED, dateFormatter.format(information.getModifiedAt())); // Date Modified
		} else {
			logger.warn(String.format("Modification date of the resource %s is null", information.getPath())); //$NON-NLS-1$
		}
		properties.put(PROPERTY_USER_MODIFIED, information.getModifiedBy());// User Modified
		// properties.put(PROPERTY_SIZE, information.getSize());
		// properties.put(PROPERTY_MIME_TYPE, information.getMimeType());
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		int propertyCount = properties.size();
		IPropertyDescriptor[] descriptors = new IPropertyDescriptor[propertyCount];
		Iterator<Entry<String, Object>> entryIterator = properties.entrySet().iterator();
		for (int i = 0; ((i < propertyCount) && entryIterator.hasNext()); ++i) {
			Entry<String, Object> entry = entryIterator.next();
			descriptors[i] = createDescriptor(entry);
		}
		return descriptors;
	}

	private static IPropertyDescriptor createDescriptor(Entry<String, Object> entry) {
		PropertyDescriptor descriptor = new PropertyDescriptor(entry.getKey(), entry.getKey());
		descriptor.setLabelProvider(new LabelProvider() {
			/**
			 *
			 */
			private static final long serialVersionUID = -4014575485655343006L;

			@Override
			public String getText(Object element) {
				return String.valueOf(element);
			}
		});
		return descriptor;
	}

	@Override
	public Object getPropertyValue(Object id) {
		return properties.get(id);
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		//
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		//
	}

}
