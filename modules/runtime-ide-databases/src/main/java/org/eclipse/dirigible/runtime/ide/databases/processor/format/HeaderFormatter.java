package org.eclipse.dirigible.runtime.ide.databases.processor.format;

import java.util.List;

public interface HeaderFormatter<T> {
	T write(List<ColumnDescriptor> columns);
}