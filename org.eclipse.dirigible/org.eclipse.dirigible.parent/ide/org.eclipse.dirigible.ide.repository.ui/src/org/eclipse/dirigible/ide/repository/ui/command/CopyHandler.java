/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.repository.ui.command;

import java.util.SortedSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.jface.dialogs.MessageDialog;

public class CopyHandler extends AbstractClipboardHandler {

	private static final String COPY_FUNCTION_IS_DISABLED_IN_THIS_INSTANCE = Messages.CopyHandler_COPY_FUNCTION_IS_DISABLED_IN_THIS_INSTANCE;
	private static final String COPY_ERROR = Messages.CopyHandler_COPY_ERROR;

	@Override
	protected void execute(ExecutionEvent event, SortedSet<IEntity> resources) {
		if (resources.size() == 0) {
			return;
		}

		if (!CommonIDEParameters.isRolesEnabled()) {
			// assume trial instance, hence disable this function
			MessageDialog.openInformation(null, COPY_ERROR, COPY_FUNCTION_IS_DISABLED_IN_THIS_INSTANCE);
			return;
		}

		Clipboard clipboard = Clipboard.getInstance();
		clipboard.clear();
		clipboard.setCommand(COPY);

		for (IEntity resource : resources) {
			clipboard.add(resource);
		}
	}

}
