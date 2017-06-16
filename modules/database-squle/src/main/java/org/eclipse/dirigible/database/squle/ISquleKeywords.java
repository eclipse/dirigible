package org.eclipse.dirigible.database.squle;

public interface ISquleKeywords {
	
	public static final String KEYWORD_SELECT = "SELECT"; //$NON-NLS-1$
	public static final String KEYWORD_DISTINCT = "DISTINCT"; //$NON-NLS-1$
	public static final String KEYWORD_FROM = "FROM"; //$NON-NLS-1$
	public static final String KEYWORD_JOIN = "JOIN"; //$NON-NLS-1$
	public static final String KEYWORD_INNER = "INNER"; //$NON-NLS-1$
	public static final String KEYWORD_OUTER = "OUTER"; //$NON-NLS-1$
	public static final String KEYWORD_LEFT = "LEFT"; //$NON-NLS-1$
	public static final String KEYWORD_RIGHT = "RIGHT"; //$NON-NLS-1$
	public static final String KEYWORD_FULL = "FULL"; //$NON-NLS-1$
	public static final String KEYWORD_WHERE = "WHERE"; //$NON-NLS-1$
	public static final String KEYWORD_GROUP_BY = "GROUP BY"; //$NON-NLS-1$
	public static final String KEYWORD_HAVING = "HAVING"; //$NON-NLS-1$
	public static final String KEYWORD_ORDER_BY = "ORDER BY"; //$NON-NLS-1$
	public static final String KEYWORD_UNION = "UNION"; //$NON-NLS-1$
	public static final String KEYWORD_ASC = "ASC"; //$NON-NLS-1$
	public static final String KEYWORD_DESC = "DESC"; //$NON-NLS-1$
	public static final String KEYWORD_AND = "AND"; //$NON-NLS-1$
	public static final String KEYWORD_OR = "OR"; //$NON-NLS-1$
	public static final String KEYWORD_AS = "AS"; //$NON-NLS-1$
	public static final String KEYWORD_ON = "ON"; //$NON-NLS-1$
	public static final String KEYWORD_LIMIT = "LIMIT"; //$NON-NLS-1$
	public static final String KEYWORD_OFFSET = "OFFSET"; //$NON-NLS-1$
	public static final String KEYWORD_INSERT = "INSERT"; //$NON-NLS-1$
	public static final String KEYWORD_INTO = "INTO"; //$NON-NLS-1$
	public static final String KEYWORD_VALUES = "VALUES"; //$NON-NLS-1$
	public static final String KEYWORD_UPDATE = "UPDATE"; //$NON-NLS-1$
	public static final String KEYWORD_SET = "SET"; //$NON-NLS-1$
	public static final String KEYWORD_CREATE = "CREATE"; //$NON-NLS-1$
	public static final String KEYWORD_TABLE = "TABLE"; //$NON-NLS-1$
	public static final String KEYWORD_PRIMARY = "PRIMARY"; //$NON-NLS-1$
	public static final String KEYWORD_KEY = "KEY"; //$NON-NLS-1$
	public static final String KEYWORD_DROP = "DROP"; //$NON-NLS-1$
	public static final String KEYWORD_DELETE = "DELETE"; //$NON-NLS-1$

	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String SPACE = " "; //$NON-NLS-1$
	public static final String OPEN = "("; //$NON-NLS-1$
	public static final String CLOSE = ")"; //$NON-NLS-1$
	public static final String QUESTION = "?"; //$NON-NLS-1$
	public static final String EQUALS = "="; //$NON-NLS-1$
	
	public static final String METADATA_SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$
	public static final String METADATA_LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$
	public static final String METADATA_GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$
	public static final String METADATA_SYNONYM = "SYNONYM"; //$NON-NLS-1$
	public static final String METADATA_ALIAS = "ALIAS"; //$NON-NLS-1$
	public static final String METADATA_VIEW = "VIEW"; //$NON-NLS-1$
	public static final String METADATA_TABLE = "TABLE"; //$NON-NLS-1$

	public static final String[] METADATA_TABLE_TYPES = { METADATA_TABLE, METADATA_VIEW, METADATA_ALIAS, METADATA_SYNONYM, METADATA_GLOBAL_TEMPORARY, METADATA_LOCAL_TEMPORARY, METADATA_SYSTEM_TABLE };
	
}
