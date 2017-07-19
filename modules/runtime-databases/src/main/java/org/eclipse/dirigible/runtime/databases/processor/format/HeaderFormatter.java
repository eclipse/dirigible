package org.eclipse.dirigible.runtime.databases.processor.format;

import java.util.List;

public interface HeaderFormatter<T> {
	T write(List<ColumnDescriptor> columns);
}