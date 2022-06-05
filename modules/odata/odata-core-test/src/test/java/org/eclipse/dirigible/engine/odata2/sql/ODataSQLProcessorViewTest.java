package org.eclipse.dirigible.engine.odata2.sql;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.engine.odata2.sql.entities.View;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.junit.Assert.assertTrue;

public class ODataSQLProcessorViewTest extends AbstractSQLProcessorTest {

    @Override
    protected Class<?>[] getODataEntities() {
        return new Class<?>[]{View.class};
    }

    @Test
    public void testSQLProcessorWithGeneratedId() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Views") //
                .accept("application/atom+xml").executeRequest(GET);
        String content = IOUtils.toString((InputStream) response.getEntity());
        assertTrue(content.contains("First"));
    }
}
