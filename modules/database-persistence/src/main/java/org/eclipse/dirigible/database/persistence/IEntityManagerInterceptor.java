package org.eclipse.dirigible.database.persistence;

import java.lang.reflect.Field;

public interface IEntityManagerInterceptor {

	public Object onSetValueBeforeUpdate(int index, String dataType, Object value);

	public Object onSetValueAfterQuery(Object pojo, Field field, Object value);

}
