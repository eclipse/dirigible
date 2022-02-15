package org.eclipse.dirigible.commons.api.topology;

import java.util.List;

public interface ITopologicallySortable {
	
	public String getId();
	
	public List<ITopologicallySortable> getDependencies();

}
