package org.eclipse.dirigible.api.v3.core.test;

import java.util.Map;

public abstract class BaseObject implements IBaseObject {

	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter) {
		return "";
	}

	@Override
	public String doSomething(Map parameters, IBaseParameter inheritedParameter, ExactParameter exactParameter, String s) {
		return "";
	}

	@Override
	public String doSomethingElse(Map parameters, IBaseParameter inheritedParameter, String s) {
		return "";
	}

}
