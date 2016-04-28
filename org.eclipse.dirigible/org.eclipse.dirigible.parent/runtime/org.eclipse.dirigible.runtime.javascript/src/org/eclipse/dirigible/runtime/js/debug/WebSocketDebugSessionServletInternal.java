package org.eclipse.dirigible.runtime.js.debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.DebugManager;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugExecutor.DebugCommand;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;

import com.google.gson.Gson;

public class WebSocketDebugSessionServletInternal {
	private static final Logger logger = Logger.getLogger(WebSocketDebugBridgeServletInternal.class);
	private static final Gson GSON = new Gson();
	private static Map<String, List<Session>> openUserSessions = new ConcurrentHashMap<String, List<Session>>();
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		String userId = session.getUserPrincipal().getName();
		List<Session> userSessions = openUserSessions.get(userId);
		if (userSessions == null) {
			userSessions = new ArrayList<Session>();
		}
		userSessions.add(session);
		openUserSessions.put(userId, userSessions);
		logger.debug("[Internal] onOpen: " + userId);
	}

	@OnError
	public void onError(Session session, String error) {
		logger.error("[Internal] onError: " + error);
	}
	
	@OnMessage
	public void onMessage(String message, Session session){
		DebugSessionDTO dto = GSON.fromJson(message, DebugSessionDTO.class);
		String userId = RequestUtils.getUser(session);
		DebugModel debugModel = DebugManager.getDebugModel(userId);
		DebugSessionModel sessionModel = debugModel.getSessionModelById(dto.sessionId);
		debugModel.setActiveSession(sessionModel);
		DebugManager.registerDebugModel(userId, debugModel);
		
		logger.info("Setting sesion with id " + dto.sessionId);
	}
	
	@OnClose
	public void onClose(Session session) {
		String userId = session.getUserPrincipal().getName();
		List<Session> userSessions = openUserSessions.get(userId);
		if (userSessions == null) {
			logger.error("[Internal] onClose: Could not find the given session for currently active user!");
			return;
		}
		Iterator<Session> iterator = userSessions.iterator();
		while (iterator.hasNext()) {
			Session nextSession = iterator.next();
			if (nextSession.getId().equalsIgnoreCase(session.getId())) {
				iterator.remove();
			}
		}
		if (userSessions.isEmpty()) {
			openUserSessions.remove(userId);
		}
		logger.debug("[Internal] onClose: Session " + userId + " has ended");
	}
		
	public static void sendCurrentDebugModelSessionsToUser(String userId, DebugModel debugModel){
		List<DebugSessionModel> sessions = debugModel.getSessions();
		List<DebugSessionDTO> sessionDTOs = getSessionDTOs(userId, sessions);
		sendToUser(userId, sessionDTOs);
	}
	
	private static List<DebugSessionDTO> getSessionDTOs(String userId, List<DebugSessionModel> models) {
		Set<String> sessionIds = getSessionIds(models);
		return getSessionDTOsForIds(sessionIds);
	}

	private static Set<String> getSessionIds(List<DebugSessionModel> models) {
		Set<String> sessionIds = new HashSet<String>();
		for(int i = 0; i<models.size(); i++){
			DebugSessionModel session = models.get(i);
			String uniqueSessionId = getDebugSessionId(i, session);
			sessionIds.add(uniqueSessionId);
		}
		return sessionIds;
	}

	private static String getDebugSessionId(int index, DebugSessionModel session){
		StringBuilder label = new StringBuilder();
		label.append(session.getUserId()).append(ICommonConstants.DEBUG_SEPARATOR).append(index + 1).append(ICommonConstants.DEBUG_SEPARATOR)
		.append(session.getExecutionId()).append(ICommonConstants.DEBUG_SEPARATOR);
		return label.toString();
	}

	private static List<DebugSessionDTO> getSessionDTOsForIds(Set<String> debugSessionIds){
		List<DebugSessionDTO> sessionDTOs = new ArrayList<DebugSessionDTO>();
		for(String sessionId : debugSessionIds){
			DebugSessionDTO dto = new DebugSessionDTO(sessionId);
			sessionDTOs.add(dto);
		}
		return sessionDTOs;
	}

	private static void sendToUser(String userId, List<DebugSessionDTO> sessionDTOs) {
		List<Session> userSessions = openUserSessions.get(userId);
		for(Session session : userSessions){
			try {
				session.getBasicRemote().sendText(GSON.toJson(sessionDTOs));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void debugActionPerformedOnSession(String userId, DebugCommand command){
		if(command == null){
			return;
		}
		switch (command) {
			case CONTINUE:
			case SKIP_ALL_BREAKPOINTS:
				clearCurrentSession(userId);
				break;
			case PAUSE:
			case STEPINTO:
			case STEPOVER:
			default:
				break;
		}
	}

	public static void clearCurrentSession(String userId) {
		DebugModel debugModel = DebugManager.getDebugModel(userId);
		DebugSessionModel currentSession = debugModel.getActiveSession();
		debugModel.removeSession(currentSession);
		sendCurrentDebugModelSessionsToUser(userId, debugModel);
	}

	private static class DebugSessionDTO{
		private String sessionId;
	
		public DebugSessionDTO(String sessionId) {
			this.sessionId = sessionId;
		}
	}
}
