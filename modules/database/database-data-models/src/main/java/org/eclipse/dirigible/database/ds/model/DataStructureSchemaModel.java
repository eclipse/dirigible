package org.eclipse.dirigible.database.ds.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The schema model representation.
 */
public class DataStructureSchemaModel extends DataStructureContentModel {
	
	private List<DataStructureTableModel> tables = new ArrayList<DataStructureTableModel>();

	private List<DataStructureViewModel> views = new ArrayList<DataStructureViewModel>();

	/**
	 * Get the tables
	 * @return the tables list
	 */
	public List<DataStructureTableModel> getTables() {
		return tables;
	}

	/**
	 * Get the views
	 * @return the views list
	 */
	public List<DataStructureViewModel> getViews() {
		return views;
	}

}
