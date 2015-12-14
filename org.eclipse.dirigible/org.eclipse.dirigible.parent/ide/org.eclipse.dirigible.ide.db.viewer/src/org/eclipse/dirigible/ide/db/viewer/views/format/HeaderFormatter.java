package org.eclipse.dirigible.ide.db.viewer.views.format;

import java.util.List;

public interface HeaderFormatter<T> {
	T write(List<ColumnDescriptor> columns);
}