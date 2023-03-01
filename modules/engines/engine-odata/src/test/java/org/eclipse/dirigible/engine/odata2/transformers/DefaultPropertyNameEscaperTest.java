/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * The Class DefaultPropertyNameEscaperTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultPropertyNameEscaperTest {

    /** The escaper. */
    private DefaultPropertyNameEscaper escaper;

    /**
     * Sets the up.
     */
    @Before
    public void setUp(){
        this.escaper = new DefaultPropertyNameEscaper();
    }
    
    /**
     * Test escape dots.
     */
    @Test
    public void testEscapeDots(){
        assertEquals("Unexpected escaped property name", "Property_Name_With_Dots", escaper.escape("Property.Name.With.Dots"));
    }

    /**
     * Test escape dot.
     */
    @Test
    public void testEscapeDot(){
        assertEquals("Unexpected escaped property name", "Property_Name", escaper.escape("Property.Name"));
    }

    /**
     * Test escape valid name.
     */
    @Test
    public void testEscapeValidName(){
        assertEquals("Unexpected escaped property name", "PropertyName", escaper.escape("PropertyName"));
    }

}