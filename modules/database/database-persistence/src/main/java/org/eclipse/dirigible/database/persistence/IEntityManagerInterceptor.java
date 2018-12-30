/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.persistence;

import java.lang.reflect.Field;

/**
 * The Entity Manager Interceptor interface.
 */
public interface IEntityManagerInterceptor {

	/**
	 * On set value before update.
	 *
	 * @param index
	 *            the index
	 * @param dataType
	 *            the data type
	 * @param value
	 *            the value
	 * @return the object
	 */
	public Object onGetValueBeforeUpdate(int index, String dataType, Object value);

	/**
	 * On set value after query.
	 *
	 * @param pojo
	 *            the pojo
	 * @param field
	 *            the field
	 * @param value
	 *            the value
	 * @return the object
	 */
	public Object onSetValueAfterQuery(Object pojo, Field field, Object value);

}
