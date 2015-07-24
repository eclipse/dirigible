package org.eclipse.dirigible.runtime.scripting;

public interface IInjectedAPIAliases {
			
	// API
	public static final String API 							= "$"; //$NON-NLS-1$
			
	// Object
	public static final String EXECUTION_CONTEXT 			= "context"; //$NON-NLS-1$
	public static final String SYSTEM_OUTPUT 				= "out"; //$NON-NLS-1$
	public static final String DEFAULT_DATASOURCE 			= "datasource"; //$NON-NLS-1$
	public static final String HTTP_REQUEST 				= "request"; //$NON-NLS-1$
	public static final String HTTP_RESPONSE 				= "response"; //$NON-NLS-1$
	public static final String HTTP_SESSION 				= "session"; //$NON-NLS-1$
	public static final String REPOSITORY 					= "repository"; //$NON-NLS-1$
	public static final String REQUEST_INPUT				= "input"; //$NON-NLS-1$
	public static final String USER							= "user"; //$NON-NLS-1$
	public static final String INITIAL_CONTEXT				= "jndi"; //$NON-NLS-1$
	public static final String STORAGE						= "storage"; //$NON-NLS-1$
	public static final String FILE_STORAGE					= "fileStorage"; //$NON-NLS-1$
	public static final String CONFIGURATION_STORAGE		= "config"; //$NON-NLS-1$
		
	// Services
	public static final String MAIL_SERVICE 				= "mail"; //$NON-NLS-1$
	public static final String EXTENSIONS_SERVICE			= "extensionManager"; //$NON-NLS-1$
	public static final String INDEXING_SERVICE				= "indexer"; //$NON-NLS-1$
	public static final String MESSAGE_HUB					= "messageHub"; //$NON-NLS-1$
	
	public static final String CONNECTIVITY_SERVICE			= "connectivity"; //$NON-NLS-1$		
	
	// Utils
	public static final String IO_UTILS 					= "io"; //$NON-NLS-1$
	public static final String HTTP_UTILS 					= "http"; //$NON-NLS-1$
	public static final String BASE64_UTILS 				= "base64"; //$NON-NLS-1$
	public static final String HEX_UTILS 					= "hex"; //$NON-NLS-1$
	public static final String DIGEST_UTILS					= "digest"; //$NON-NLS-1$
	public static final String URL_UTILS					= "url"; //$NON-NLS-1$
	public static final String UPLOAD_UTILS					= "upload"; //$NON-NLS-1$
	public static final String UUID_UTILS					= "uuid"; //$NON-NLS-1$
	public static final String DB_UTILS						= "db"; //$NON-NLS-1$
	public static final String XSS_UTILS					= "xss"; //$NON-NLS-1$
	public static final String XML_UTILS					= "xml"; //$NON-NLS-1$
	public static final String WIKI_UTILS					= "wiki"; //$NON-NLS-1$
	
}
