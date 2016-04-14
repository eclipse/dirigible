package org.eclipse.dirigible.runtime.chrome.debugger.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.handlers.OnExceptionHandler;

import com.google.gson.Gson;

public class MessageDispatcher {

	private static final Map<Session, List<String>> SENT_MSG = new HashMap<Session, List<String>>();
	private static final Map<Session, List<String>> RECEIVED_MSG = new HashMap<Session, List<String>>();
	private static final Logger LOGGER = Logger.getLogger(DebuggingService.class.getCanonicalName());

	public static void sendMessage(final String message, final Session recipient) {
		sendAsyncMessage(message, recipient);
	}

	public static void sendAsyncMessage(final String message, final Session sender) {
		storeSentMessage(message, sender);
		sender.getAsyncRemote().sendText(message);
		LOGGER.debug(String.format("Sending asynchronous message to client with id [ %s ]. Content: [ %s ].",
				sender.getId(), message));
	}

	public static void sendSyncMessage(final String message, final Session sender) {
		storeSentMessage(message, sender);
		try {
			sender.getBasicRemote().sendText(message);
		} catch (final IOException e) {
			LOGGER.error(String.format("Could not send synchronous message to client %s.", sender.getId()), e);
			try {
				new OnExceptionHandler().handle(e.getMessage(), sender);
			} catch (final IOException e1) {
			}
		}
	}

	private static void storeSentMessage(final String message, final Session sender) {
		List<String> sentMsgs = SENT_MSG.get(sender);
		if (sentMsgs == null) {
			sentMsgs = new ArrayList<String>();
		}
		sentMsgs.add(message);
		SENT_MSG.put(sender, sentMsgs);
	}

	public static void receiveFrom(final String message, final Session recipient) {
		List<String> receivedMsgs = RECEIVED_MSG.get(recipient);
		if (receivedMsgs == null) {
			receivedMsgs = new ArrayList<String>();
		}
		receivedMsgs.add(message);
		RECEIVED_MSG.put(recipient, receivedMsgs);
		LOGGER.debug(String.format("Received message [ %s ] from client with id [ %s ].", message, recipient.getId()));
	}

	public static boolean sentMessagesForSessionContain(String text, String sessionId){
		List<String> receivedMessages = receivedMessages(sessionId);
		if(receivedMessages == null){
			return false;
		}
		return receivedMessages.contains(text);
	}
	
	public static boolean sentMessageContain(final String text) {
		return nestedCollectionContains(SENT_MSG.values(), text);
	}

	public static boolean receivedMessagesContain(final String text) {
		return nestedCollectionContains(RECEIVED_MSG.values(), text);
	}

	private static boolean nestedCollectionContains(Collection<? extends Collection<String>> collection, String text) {
		for (Collection<String> msgs : collection) {
			if (msgs.contains(text)) {
				return true;
			}
		}
		return false;
	}

	public static Map<Session, List<String>> getHistory() {
		Map<Session, List<String>> history = new HashMap<Session, List<String>>();
		history.putAll(SENT_MSG);
		history.putAll(RECEIVED_MSG);
		return history;
	}

	public static boolean sessionHistoryContains(String sessionId, String text) {
		List<String> userHistory = getSessionHistory(sessionId);
		for(String message : userHistory){
			if(message.contains(text)){
				return true;
			}
		}
		return false;
	}

	public static List<String> getSessionHistory(String sessionId) {
		List<String> userHistory = new ArrayList<String>();
		Map<Session, List<String>> history = getHistory();
		for(Map.Entry<Session, List<String>> e : history.entrySet()){
			Session session = e.getKey();
			if(session.getId().equalsIgnoreCase(sessionId)){
				userHistory.addAll(e.getValue());
			}
		}
		return userHistory;
	}
	
	public static Integer getMessageIdForMessageMethod(String sessionId, String method){
		List<String> sessionHistory = getSessionHistory(sessionId);
		for(String message : sessionHistory){
			Gson gson = new Gson();
			MessageRequest request = gson.fromJson(message, MessageRequest.class);
			String requestMethod = request.getMethod();
			if(requestMethod.equalsIgnoreCase(method)){
				return request.getId();
			}
		}
		
		return null;
	}

	public static List<String> receivedMessages(String sessionId) {
		for(Map.Entry<Session, List<String>> e : RECEIVED_MSG.entrySet()){
			Session session = e.getKey();
			if(session.getId().equalsIgnoreCase(sessionId)){
				return e.getValue();
			}
		}
		return null;
	}
}
