package org.eclipse.dirigible.engine.odata2.sql;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.engine.odata2.sql.entities.User;
import org.eclipse.dirigible.engine.odata2.sql.entities.Group;
import org.junit.Test;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.junit.Assert.assertTrue;

public class ODataSQLProcessorManyToManyTest extends AbstractSQLProcessorTest {

    @Override
    protected Class<?>[] getODataEntities() {
        return new Class<?>[]{User.class, Group.class};
    }

    @Test
    public void testSQLProcessorWithMappingTable() throws Exception {
        String UUID = "ec20bbaf-ee7a-4405-91d0-7ad8be889270";
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Users('" + UUID + "')", "Groups") //
                .accept("application/atom+xml").executeRequest(GET);
        String content = IOUtils.toString((InputStream) response.getEntity());
        assertTrue(content.contains("Mid"));
    }
}
