/*****************************************************************************************
 * Copyright (c) 2010, 2011 Texas Center for Applied Technology (TEES) (TAMUS) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Austin Riddle (Texas Center for Applied Technology) - initial API and implementation
 *    EclipseSource - ongoing development
 *****************************************************************************************/
package org.eclipse.swt.internal.widgets;

public interface ValidationStrategy {

	boolean validate(String filename);

}