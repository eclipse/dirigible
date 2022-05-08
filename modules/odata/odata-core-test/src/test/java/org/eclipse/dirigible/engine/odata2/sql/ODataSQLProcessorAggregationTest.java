package org.eclipse.dirigible.engine.odata2.sql;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.engine.odata2.sql.entities.Customer;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.junit.Assert.assertTrue;

public class ODataSQLProcessorAggregationTest extends AbstractSQLProcessorTest {

    @Override
    protected Class<?>[] getODataEntities() {
        return new Class<?>[]{Customer.class};
    }

    @Test
    public void testSQLProcessorWithGroupBy() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Customers") //
                .accept("application/atom+xml").executeRequest(GET);
        String content = IOUtils.toString((InputStream) response.getEntity());
        // Check the SUM of the NUMBER column
        assertTrue(content.contains("6"));
    }
}
