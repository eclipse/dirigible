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

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SamplesLabelProvider extends LabelProvider {
	private static final long serialVersionUID = 6752428028085740772L;

	private static final Image SAMPLE_ICON = ImageUtils
			.createImage(SampleProjectWizardGitTemplatePage.getIconURL("icon-sample.png"));

	@Override
	public String getText(Object element) {
		String text = null;
		if (element instanceof SamplesCategory) {
			text = ((SamplesCategory) element).getName();
		} else if (element instanceof SamplesProject) {
			text = ((SamplesProject) element).getTemplate().getDescription();
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof SamplesCategory) {
			image = SAMPLE_ICON;
		} else if (element instanceof SamplesProject) {
			image = ((SamplesProject) element).getTemplate().getImage();
		}
		return image;
	}
}
