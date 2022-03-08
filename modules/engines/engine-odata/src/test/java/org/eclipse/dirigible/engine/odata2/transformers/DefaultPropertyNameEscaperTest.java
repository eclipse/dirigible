package org.eclipse.dirigible.engine.odata2.transformers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPropertyNameEscaperTest {

    private DefaultPropertyNameEscaper escaper;

    @Before
    public void setUp(){
        this.escaper = new DefaultPropertyNameEscaper();
    }
    
    @Test
    public void testEscapeDots(){
        assertEquals("Unexpected escaped property name", "Property_Name_With_Dots", escaper.escape("Property.Name.With.Dots"));
    }

    @Test
    public void testEscapeDot(){
        assertEquals("Unexpected escaped property name", "Property_Name", escaper.escape("Property.Name"));
    }

    @Test
    public void testEscapeValidName(){
        assertEquals("Unexpected escaped property name", "PropertyName", escaper.escape("PropertyName"));
    }

}