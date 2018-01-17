package org.eclipse.dirigible.api.v3.core.test;

import java.util.Map;

public interface IBaseObject {

	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter);

}
