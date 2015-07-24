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

package org.eclipse.dirigible.runtime.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.dirigible.runtime.filter.XSSUtils;
import org.junit.Test;

public class XSSUtilsTest {

	@Test
	public void testXSS() {
		try {
			String script = "<script>alert(\"XSS\");</script>";
			String escaped = XSSUtils.stripXSS(script);
			assertEquals("&lt;script&gt;alert(&quot;XSS&quot;);&lt;/script&gt;",escaped);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
