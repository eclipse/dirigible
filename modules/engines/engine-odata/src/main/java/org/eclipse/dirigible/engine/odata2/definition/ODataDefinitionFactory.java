package org.eclipse.dirigible.engine.odata2.definition;

import java.sql.Timestamp;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

public class ODataDefinitionFactory {
	
	public static ODataDefinition parseOData(String contentPath, String data) {
		ODataDefinition odataDefinition = GsonHelper.GSON.fromJson(data, ODataDefinition.class);
		odataDefinition.setLocation(contentPath);
		odataDefinition.setHash(DigestUtils.md5Hex(data));
		odataDefinition.setCreatedBy(UserFacade.getName());
		odataDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		return odataDefinition;
	}

}
