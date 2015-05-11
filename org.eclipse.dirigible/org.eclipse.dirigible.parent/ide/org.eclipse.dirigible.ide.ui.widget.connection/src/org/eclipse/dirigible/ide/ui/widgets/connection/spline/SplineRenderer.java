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

package org.eclipse.dirigible.ide.ui.widgets.connection.spline;

import org.eclipse.swt.graphics.GC;

public class SplineRenderer {

	private final int precision;

	public SplineRenderer(int precision) {
		this.precision = precision;
	}

	public void renderSpline(GC gc, Spline spline) {
		for (int i = 1; i <= precision; ++i) {
			final float startAmount = i / (float) precision;
			final float endAmount = (i - 1) / (float) precision;

			final int startX = spline.getX(startAmount);
			final int startY = spline.getY(startAmount);

			final int endX = spline.getX(endAmount);
			final int endY = spline.getY(endAmount);

			gc.drawLine(startX, startY, endX, endY);
		}
	}

}
