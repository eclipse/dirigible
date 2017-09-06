package org.eclipse.dirigible.database.ds.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;

public interface IDataStructuresCoreService extends ICoreService {

	public static final String FILE_EXTENSION_TABLE = ".table";

	public static final String FILE_EXTENSION_VIEW = ".view";

	public static final String TYPE_TABLE = "TABLE";

	public static final String TYPE_VIEW = "VIEW";

	// Tables

	public DataStructureTableModel createTable(String location, String name, String hash) throws DataStructuresException;

	public DataStructureTableModel getTable(String location) throws DataStructuresException;

	public DataStructureTableModel getTableByName(String name) throws DataStructuresException;

	public boolean existsTable(String location) throws DataStructuresException;

	public void removeTable(String location) throws DataStructuresException;

	public void updateTable(String location, String name, String hash) throws DataStructuresException;

	public List<DataStructureTableModel> getTables() throws DataStructuresException;

	public DataStructureTableModel parseTable(String json);

	public DataStructureTableModel parseTable(byte[] json);

	public String serializeTable(DataStructureTableModel tableModel);

	// Views

	public DataStructureViewModel createView(String location, String name, String hash) throws DataStructuresException;

	public DataStructureViewModel getView(String location) throws DataStructuresException;

	public DataStructureViewModel getViewByName(String name) throws DataStructuresException;

	public boolean existsView(String location) throws DataStructuresException;

	public void removeView(String location) throws DataStructuresException;

	public void updateView(String location, String name, String hash) throws DataStructuresException;

	public List<DataStructureViewModel> getViews() throws DataStructuresException;

	public DataStructureViewModel parseView(String json);

	public DataStructureViewModel parseView(byte[] json);

	public String serializeView(DataStructureViewModel viewModel);

}
