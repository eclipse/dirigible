package org.eclipse.dirigible.api.v3.core.test;

import java.util.Map;

public interface IBaseObject {

	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter);

	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter, String s);

	public String doSomethingElse(Map parameters, IBaseParameter inheritedParameter, String s);

}
