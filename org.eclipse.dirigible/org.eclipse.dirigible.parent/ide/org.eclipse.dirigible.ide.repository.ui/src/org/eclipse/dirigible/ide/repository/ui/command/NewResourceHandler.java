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

import java.io.IOException;
import java.util.SortedSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

public class NewResourceHandler extends AbstractClipboardHandler {

	@Override
	protected void execute(ExecutionEvent event, SortedSet<IEntity> resources) {
		if (resources.size() == 0) {
			return;
		}

		IRepository repository = RepositoryFacade.getInstance().getRepository();

		String targetReposiotryPath = resources.first().getPath().toString();

		InputDialog dlg = new InputDialog(null, "", "Resource Name", "", new PathValidator());
		if (dlg.open() == Window.OK) {
			try {
				repository.createResource(targetReposiotryPath + IRepository.SEPARATOR + dlg.getValue());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		RefreshHandler.refreshActivePart(event);

	}

	class PathValidator implements IInputValidator {
		/**
		 * Validates the String. Returns null for no error, or an error message
		 *
		 * @param newText
		 *            the String to validate
		 * @return String
		 */
		@Override
		public String isValid(String newText) {
			int len = newText.length();

			// TODO

			// Input must be OK
			return null;
		}
	}

}
