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

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class DataStructureTemplateStructurePageLabelProvider extends
		ColumnLabelProvider implements ITableLabelProvider {

	private static final long serialVersionUID = 4279129633962596895L;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ColumnDefinition) {
			switch (columnIndex) {
			case 0: // Name
				return ((ColumnDefinition) element).getName();
			case 1: // Type
				return ((ColumnDefinition) element).getType();
			case 2: // Length
				return new Integer(((ColumnDefinition) element).getLength())
						.toString();
			case 3: // NN?
				return new Boolean(((ColumnDefinition) element).isNotNull())
						.toString();
			case 4: // PK?
				return new Boolean(((ColumnDefinition) element).isPrimaryKey())
						.toString();
			case 5: // Default
				return ((ColumnDefinition) element).getDefaultValue();
			default:
				return null;
			}
		}
		return null;
	}

}
