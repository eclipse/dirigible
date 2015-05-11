/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class HtmlForEntityTemplateTablePageLabelProvider extends LabelProvider {

	private static final long serialVersionUID = -7474193796019351783L;

	@Override
	public String getText(Object element) {
		if (element instanceof TableColumn) {
			return ((TableColumn) element).getName();
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

}
