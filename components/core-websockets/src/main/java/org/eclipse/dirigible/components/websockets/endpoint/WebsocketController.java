package org.eclipse.dirigible.components.websockets.endpoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.components.api.websockets.WebsocketsFacade;
import org.eclipse.dirigible.components.websockets.message.InputMessage;
import org.eclipse.dirigible.components.websockets.message.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WebsocketController.class);
	
	/** The processor. */
	private final WebsocketProcessor processor;
	
	@Autowired
	@Qualifier("clientOutboundChannel")
	private MessageChannel clientOutboundChannel;
	
	/**
	 * Instantiates a new websockets service.
	 *
	 * @param processor the processor
	 */
	@Autowired
	public WebsocketController(WebsocketProcessor processor) {
		this.processor = processor;
	}
	
	/**
	 * Gets the processor.
	 *
	 * @return the processor
	 */
	public WebsocketProcessor getProcessor() {
		return processor;
	}
	
	@MessageMapping("/js/{endpoint}")
    @SendTo("/topic/messages/{endpoint}")
    public OutputMessage onMessage(@DestinationVariable String endpoint, final InputMessage message) throws Exception {
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        if (logger.isTraceEnabled()) {logger.trace(String.format("[websocket] Endpoint '%s' received message:%s ", endpoint, message));}
		Map<Object, Object> context = new HashMap<>();
		context.put("message", message);
    	context.put("method", "onmessage");
    	try {
    		Object result = getProcessor().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_MESSAGE, context);
    		return new OutputMessage(message.getFrom(), result != null ? result.toString() : "", time);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			return new OutputMessage(message.getFrom(), e.getMessage(), time);
		}
    }
	
//	/**
//	 * On open callback.
//	 *
//	 * @param session the session
//	 * @param endpoint the endpoint
//	 */
//	@OnOpen
//	public void onOpen(Session session, @PathParam("endpoint") String endpoint) {
//		if (logger.isDebugEnabled()) {logger.debug(String.format("[websocket] Endpoint '%s' openned.", endpoint));}
//		Map<Object, Object> context = new HashMap<>();
//    	context.put("method", "onopen");
//    	try {
//    		getProcessor().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_OPEN, context);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
//		}
//	}
//	
//
//	/**
//	 * On message callback.
//	 *
//	 * @param message the message
//	 * @param session the session
//	 * @param endpoint the endpoint
//	 */
//	@OnMessage
//	public void onMessage(String message, Session session, @PathParam("endpoint") String endpoint) {
//		if (logger.isTraceEnabled()) {logger.trace(String.format("[websocket] Endpoint '%s' received message:%s ", endpoint, message));}
//		Map<Object, Object> context = new HashMap<>();
//		context.put("message", message);
//    	context.put("method", "onmessage");
//    	try {
//    		getProcessor().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_MESSAGE, context);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
//		}
//	}
//
//	/**
//	 * On error callback.
//	 *
//	 * @param session the session
//	 * @param throwable the throwable
//	 * @param endpoint the endpoint
//	 */
//	@OnError
//	public void onError(Session session, Throwable throwable, @PathParam("endpoint") String endpoint) {
//		if (logger.isErrorEnabled()) {logger.error(String.format("[ws:console] Endpoint '%s' error %s", endpoint, throwable.getMessage()));}
//		if (logger.isErrorEnabled()) {logger.error("[websocket] " + throwable.getMessage(), throwable);}
//		Map<Object, Object> context = new HashMap<>();
//		context.put("error", throwable.getMessage());
//    	context.put("method", "onerror");
//    	try {
//    		getProcessor().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_ERROR, context);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
//		}
//	}
//
//	/**
//	 * On close callback.
//	 *
//	 * @param session the session
//	 * @param closeReason the close reason
//	 * @param endpoint the endpoint
//	 */
//	@OnClose
//	public void onClose(Session session, CloseReason closeReason, @PathParam("endpoint") String endpoint) {
//		if (logger.isDebugEnabled()) {logger.debug(String.format("[websocket] Endpoint '%s' closed because of %s", endpoint, closeReason));}
//		Map<Object, Object> context = new HashMap<>();
//    	context.put("method", "onclose");
//    	try {
//    		getProcessor().processEvent(endpoint, WebsocketsFacade.DIRIGIBLE_WEBSOCKET_WRAPPER_MODULE_ON_CLOSE, context);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
//		}
//	}

}
