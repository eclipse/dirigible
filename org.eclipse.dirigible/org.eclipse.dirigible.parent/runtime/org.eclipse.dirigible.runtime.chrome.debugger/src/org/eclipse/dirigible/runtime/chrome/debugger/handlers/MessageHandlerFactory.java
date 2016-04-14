package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;

public class MessageHandlerFactory {

	public static MessageHandler getHandler(final String message) {
		if (RequestUtils.isSetBreakpointMessage(message)) {
			return new SetBreakpointMessageHandler();
		}else if(RequestUtils.isRemoveBreakpointMessage(message)){
			return new RemoveBreakpointMessageHandler();
		}else if(RequestUtils.isDebuggerStepMessage(message)){
			return new DebuggerStepHandler();
		}else if(RequestUtils.isGetResourceContent(message)){
			return new GetResourceContentHandler();
		}else if(RequestUtils.isGetResourceTree(message)){
			return new GetResourceTreeHandler();
		}else if(RequestUtils.isGetScritpSource(message)){
			return new GetScriptSourceHandler();
		}else if(RequestUtils.isSetScriptSource(message)){
			return new SetScriptSourceHandler();
		}else if(RequestUtils.isGetProperties(message)){
			return new GetPropertiesHandler();
		}else if(RequestUtils.isEvaluateOnCallFrame(message)){
			return new EvaluateOnCallFrameHandler();
		}else{
			return new EmptyResultHandler();
		}
	}

}
