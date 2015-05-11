/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.dirigible.ide.ui.rap.builders;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

@SuppressWarnings("deprecation")
public class MenuBarPopupBilder extends ElementBuilder {

	private Composite content;
	private Image bottom;
	private Image cornerLeft;
	private Image cornerRight;
	private Image left;
	private Image right;
	private Image top;

	public MenuBarPopupBilder(Composite parent, String layoutSetId) {
		super(parent, layoutSetId);
		init();
	}

	private void init() {
		LayoutSet set = getLayoutSet();
		bottom = createImage(set
				.getImagePath(LayoutSetConstants.MENUBAR_BOTTOM_BG));
		cornerLeft = createImage(set
				.getImagePath(LayoutSetConstants.MENUBAR_CORNER_LEFT));
		cornerRight = createImage(set
				.getImagePath(LayoutSetConstants.MENUBAR_CORNER_RIGHT));
		left = createImage(set.getImagePath(LayoutSetConstants.MENUBAR_LEFT_BG));
		right = createImage(set
				.getImagePath(LayoutSetConstants.MENUBAR_RIGHT_BG));
		top = createImage(set.getImagePath(LayoutSetConstants.MENUBAR_TOP_BG));
	}

	public void addControl(Control control, Object layoutData) {
		//
	}

	public void addControl(Control control, String positionId) {
		//
	}

	public void addImage(Image image, Object layoutData) {
		//
	}

	public void addImage(Image image, String positionId) {
		//
	}

	public void build() {
		Composite popup = new Composite(getParent(), SWT.NONE);
		popup.setLayout(new FormLayout());

		// Top Border
		Label topLabel = new Label(popup, SWT.NONE);
		topLabel.setBackgroundImage(top);
		FormData fdTopLabel = new FormData();
		topLabel.setLayoutData(fdTopLabel);
		fdTopLabel.left = new FormAttachment(0);
		fdTopLabel.top = new FormAttachment(0);
		fdTopLabel.right = new FormAttachment(100);
		fdTopLabel.height = top.getBounds().height;

		// Bottom
		Composite bottomComp = new Composite(popup, SWT.NONE);
		bottomComp.setBackgroundMode(SWT.INHERIT_NONE);
		FormData fdBottomCompo = new FormData();
		bottomComp.setLayoutData(fdBottomCompo);
		bottomComp.setLayout(new FormLayout());
		fdBottomCompo.left = new FormAttachment(0);
		fdBottomCompo.right = new FormAttachment(100);
		fdBottomCompo.bottom = new FormAttachment(100);
		fdBottomCompo.height = cornerLeft.getBounds().height;

		Label leftCorner = new Label(bottomComp, SWT.NONE);
		leftCorner.setImage(cornerLeft);
		FormData fdLeftCorner = new FormData();
		leftCorner.setLayoutData(fdLeftCorner);
		fdLeftCorner.left = new FormAttachment(0);
		fdLeftCorner.top = new FormAttachment(0);
		fdLeftCorner.height = cornerLeft.getBounds().height;
		fdLeftCorner.width = cornerLeft.getBounds().width;

		Label rightCorner = new Label(bottomComp, SWT.NONE);
		rightCorner.setImage(cornerRight);
		FormData fdRightCorner = new FormData();
		rightCorner.setLayoutData(fdRightCorner);
		fdRightCorner.top = new FormAttachment(0);
		fdRightCorner.right = new FormAttachment(100);
		fdRightCorner.height = cornerRight.getBounds().height;
		fdRightCorner.width = cornerRight.getBounds().width;

		Label bottomCenter = new Label(bottomComp, SWT.NONE);
		bottomCenter.setBackgroundImage(bottom);
		FormData fdBottomCenter = new FormData();
		bottomCenter.setLayoutData(fdBottomCenter);
		fdBottomCenter.bottom = new FormAttachment(100);
		fdBottomCenter.left = new FormAttachment(leftCorner);
		fdBottomCenter.right = new FormAttachment(rightCorner);
		fdBottomCenter.height = bottom.getBounds().height;

		// Left Border
		Label leftLabel = new Label(popup, SWT.NONE);
		leftLabel.setBackgroundImage(left);
		FormData fdLeftLabel = new FormData();
		leftLabel.setLayoutData(fdLeftLabel);
		fdLeftLabel.left = new FormAttachment(0);
		fdLeftLabel.top = new FormAttachment(topLabel);
		fdLeftLabel.bottom = new FormAttachment(bottomComp);
		fdLeftLabel.width = left.getBounds().width;

		// Right Border
		Label rightLabel = new Label(popup, SWT.NONE);
		rightLabel.setBackgroundImage(right);
		FormData fdRightLabel = new FormData();
		rightLabel.setLayoutData(fdRightLabel);
		fdRightLabel.top = new FormAttachment(topLabel);
		fdRightLabel.right = new FormAttachment(100);
		fdRightLabel.bottom = new FormAttachment(bottomComp);
		fdRightLabel.width = right.getBounds().width;

		content = new Composite(popup, SWT.NONE);
		content.setBackground(Graphics.getColor(0, 0, 0));
		FormData fdContent = new FormData();
		content.setLayoutData(fdContent);
		fdContent.top = new FormAttachment(topLabel);
		fdContent.left = new FormAttachment(leftLabel);
		fdContent.right = new FormAttachment(rightLabel);
		fdContent.bottom = new FormAttachment(bottomComp);
	}

	public void dispose() {
		//
	}

	public Control getControl() {
		return content;
	}

	public Point getSize() {
		return content.getSize();
	}
}
