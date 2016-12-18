package org.eclipse.dirigible.repository.ext.db.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@SuppressWarnings("javadoc")
public class Messages {

	private static final String BUNDLE_NAME = "org.eclipse.dirigible.repository.ext.db.model.messages"; //$NON-NLS-1$
	public static String DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCIES_ARRAY_IN_THE_TABLE_MODEL_S;
	public static String DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S;
	public static String DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S;
	public static String DataStructureModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S;
	public static String DataStructureModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_MODEL_S;
	public static String DataStructureModel_ELEMENT_S_MUST_BE_ARRAY_IN_THE_MODEL_S;
	public static String TableModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S;
	public static String TableModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMNS_ARRAY_IN_THE_TABLE_MODEL_S;
	public static String TableModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S;
	public static String TopologicalSorter_CYCLIC_DEPENDENCY_S_IN_S;

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
