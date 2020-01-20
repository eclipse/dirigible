package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.engine.odata2.definition.ODataAssociationDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;

public class ODataMetadataUtil {
	
    public static ODataEntityDefinition getEntity(ODataDefinition model, String name, String navigation) {
		for (ODataEntityDefinition entity : model.getEntities()) {
			if (name.equals(entity.getName())) {
				return entity;
			}
		}
		throw new IllegalArgumentException(String.format("There is no entity with name: %s, referenced by the navigation: %s", name, navigation));
	}
    
	public static ODataAssociationDefinition getAssociation(ODataDefinition model, String name, String navigation) {
		for (ODataAssociationDefinition association : model.getAssociations()) {
			if (name.equals(association.getName())) {
				return association;
			}
		}
		throw new IllegalArgumentException(String.format("There is no association with name: %s, referenced by the navigation: %s", name, navigation));
	}

}
