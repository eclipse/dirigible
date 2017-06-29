package org.eclipse.dirigible.runtime.ide.databases.processor.format;

import java.util.List;

public class StringHeaderFormatter implements HeaderFormatter<String>{

	@Override
	public String write(List<ColumnDescriptor> columnDescriptors){
		StringBuilder headerSb = new StringBuilder();
		if(columnDescriptors.size()>0)
			headerSb.append(ResultSetStringWriter.DELIMITER);
		for (ColumnDescriptor columnDescriptor : columnDescriptors) {
			String lbl = String.format("%-"+columnDescriptor.getDisplaySize()+"s", columnDescriptor.getLabel());
			headerSb.append(lbl);
			headerSb.append(ResultSetStringWriter.DELIMITER);
			lbl = "";
		}		
		int headerlength = headerSb.length();
		headerSb.append(ResultSetStringWriter.NEWLINE_CHARACTER);

		for (int i = 0; i < headerlength; i++) {
			headerSb.append("-");	
		}
		headerSb.append(ResultSetStringWriter.NEWLINE_CHARACTER);
		return headerSb.toString();
	}
	
}
