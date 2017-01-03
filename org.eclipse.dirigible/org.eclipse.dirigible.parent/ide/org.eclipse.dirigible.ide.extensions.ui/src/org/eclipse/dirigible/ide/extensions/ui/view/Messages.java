package org.eclipse.dirigible.ide.extensions.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.extensions.ui.view.messages"; //$NON-NLS-1$
	public static String ExtensionsManagerView_DELETE_ACTION_TOOL_TIP;
	public static String ExtensionsManagerView_DELETE_EXTENSIONS_DIALOG_DESCRIPTION;
	public static String ExtensionsManagerView_DELETE_EXTENSIONS_TITLE;
	public static String ExtensionsManagerView_DELETE_LABEL;
	public static String ExtensionsManagerView_EXTENSIONS;
	public static String ExtensionsManagerView_FAILED_TO_DELETE_EXTENSION;
	public static String ExtensionsManagerView_REFRESH_ACTION_TOOLTIP;
	public static String ExtensionsManagerView_REFRESH_LABEL;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
