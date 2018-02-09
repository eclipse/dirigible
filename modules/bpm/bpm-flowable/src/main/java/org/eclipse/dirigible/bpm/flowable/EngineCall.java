package org.eclipse.dirigible.bpm.flowable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;

public class EngineCall implements JavaDelegate {
	
	private FixedValue handler;
	
	/**
	 * Getter for the handler attribute
	 * @return
	 */
	public FixedValue getHandler() {
		return handler;
	}
	
	/**
	 * Setter of the handler attribute
	 * @param handler
	 */
	public void setHandler(FixedValue handler) {
		this.handler = handler;
	}
	
	@Override
	public void execute(DelegateExecution execution) {
		
		try {
			Map<Object, Object> context = new HashMap<>();
			context.putAll(execution.getVariables());
			ScriptEngineExecutorsManager.executeServiceModule(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, this.handler.getExpressionText(), context);
			for (Map.Entry<Object, Object> entry : context.entrySet()) {
				if (entry.getKey() != null) {
					Object original = execution.getVariables().get(entry.getKey().toString());
					if (original != null && !original.equals(entry.getValue())) {
						execution.setVariable(entry.getKey().toString(), entry.getValue());
					}
				}
			}
		} catch (ScriptingException e) {
			throw new BpmnError(e.getMessage());
		}		
	}

}
