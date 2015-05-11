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

public class TwoPointSpline implements Spline {

	private final int x1;

	private final int x2;

	private final int y1;

	private final int y2;

	public TwoPointSpline(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public int getX(float amount) {
		amount = fixAmount(amount);
		return x1 + (int) ((x2 - x1) * amount);
	}

	@Override
	public int getY(float amount) {
		amount = fixAmount(amount);
		if (amount < 0.5f) {
			return y1 + getOffset(amount);
		} else {
			return y2 - getOffset(1.0f - amount);
		}
	}

	private float fixAmount(float amount) {
		float result = amount;
		if (result < 0.0f) {
			result = 0.0f;
		}
		if (result > 1.0f) {
			result = 1.0f;
		}
		return result;
	}

	private int getOffset(float amount) {
		if (y2 == y1) {
			return 0;
		}
		final float coef = (y2 - y1) / (2.0f * 0.5f * 0.5f);
		return (int) (coef * amount * amount);
	}

}
