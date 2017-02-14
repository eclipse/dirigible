package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.dirigible.ide.template.ui.common.table.TableColumn;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MobileForEntityTemplateTablePageLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		TableColumn column = (TableColumn) element;
		switch (columnIndex) {
		case 0:
			return column.getName();
		case 1:
			return column.getType();
		case 2:
			return column.getSize() + "";
		case 3:
			return column.getWidgetType();
		case 4:
			return column.getLabel();
		default:
			return "";
		}
	}

}
