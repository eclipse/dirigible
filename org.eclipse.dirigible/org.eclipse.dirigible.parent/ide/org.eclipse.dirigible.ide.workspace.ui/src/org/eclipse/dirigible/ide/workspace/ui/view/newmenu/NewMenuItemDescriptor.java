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

package org.eclipse.dirigible.ide.workspace.ui.view.newmenu;

import org.eclipse.core.commands.IHandler;

public class NewMenuItemDescriptor implements Comparable<NewMenuItemDescriptor> {

	private String text;

	private String toolTip;

	private int order;

	private String imageBundle;

	private String imagePrefix;

	private String imageName;

	private IHandler defaultHandler;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getImageBundle() {
		return imageBundle;
	}

	public void setImageBundle(String imageBundle) {
		this.imageBundle = imageBundle;
	}

	public String getImagePrefix() {
		return imagePrefix;
	}

	public void setImagePrefix(String imagePrefix) {
		this.imagePrefix = imagePrefix;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public IHandler getDefaultHandler() {
		return defaultHandler;
	}

	public void setDefaultHandler(IHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	@Override
	public int compareTo(NewMenuItemDescriptor that) {
		int result = this.getOrder() - that.getOrder();
		if (result == 0) {
			result = this.getText().compareTo(that.getText());
		}
		return result;
	}

}
