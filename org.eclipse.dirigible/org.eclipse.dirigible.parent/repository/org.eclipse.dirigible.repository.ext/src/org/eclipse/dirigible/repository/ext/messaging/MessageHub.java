package org.eclipse.dirigible.repository.ext.messaging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.db.DBSequenceUtils;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.ext.security.Messages;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class MessageHub implements IMessagingService {
	
	private static final Logger logger = Logger.getLogger(MessageHub.class);
	
	private static final String DATABASE_ERROR = Messages.getString("SecurityManager.DATABASE_ERROR"); //$NON-NLS-1$
	
	private static final String MSQ_CLIENT_SEQ =		"MSQ_CLIENT_SEQ";
	
	private static final String MSQ_TOPIC_SEQ =			"MSQ_TOPIC_SEQ";
	
	private static final String MSQ_SUBS_SEQ =			"MSQ_SUBS_SEQ";
	
	private static final String MSQ_MESSAGE_SEQ =		"MSQ_MESSAGE_SEQ";
	
	private static final String MSQ_IN_SEQ =			"MSQ_IN_SEQ";
	
	private static final String MSQ_OUT_SEQ =			"MSQ_OUT_SEQ";
	
	private static final String INSERT_CLIENT = 		"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_client.sql"; //$NON-NLS-1$
	
	private static final String REMOVE_CLIENT = 		"/org/eclipse/dirigible/repository/ext/messaging/sql/remove_client.sql"; //$NON-NLS-1$
	
	private static final String GET_CLIENT = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/get_client.sql"; //$NON-NLS-1$
	
	private static final String INSERT_TOPIC = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_topic.sql"; //$NON-NLS-1$
	
	private static final String REMOVE_TOPIC = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/remove_topic.sql"; //$NON-NLS-1$
	
	private static final String GET_TOPIC = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/get_topic.sql"; //$NON-NLS-1$
	
	private static final String INSERT_SUBS = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_subs.sql"; //$NON-NLS-1$
	
	private static final String REMOVE_SUBS = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/remove_subs.sql"; //$NON-NLS-1$
	
	private static final String INSERT_MESSAGE = 		"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_message.sql"; //$NON-NLS-1$
	
	private static final String INSERT_IN = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_in.sql"; //$NON-NLS-1$
	
	private static final String GET_OUT = 				"/org/eclipse/dirigible/repository/ext/messaging/sql/get_out.sql"; //$NON-NLS-1$
	
	private static final String GET_OUT_BY_TOPIC = 		"/org/eclipse/dirigible/repository/ext/messaging/sql/get_out_by_topic.sql"; //$NON-NLS-1$
	
	private static final String SET_RECEIVED_STATUS = 	"/org/eclipse/dirigible/repository/ext/messaging/sql/update_received_status.sql"; //$NON-NLS-1$
	
	private static final String GET_IN_NEW = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/get_in_new.sql"; //$NON-NLS-1$
	
	private static final String INSERT_OUT = 			"/org/eclipse/dirigible/repository/ext/messaging/sql/insert_out.sql"; //$NON-NLS-1$
	
	private static final String GET_SUBS_BY_TOPIC = 	"/org/eclipse/dirigible/repository/ext/messaging/sql/get_subs_by_topic.sql"; //$NON-NLS-1$
	
	private static final String CLEANUP_MESSAGES = 		"/org/eclipse/dirigible/repository/ext/messaging/sql/cleanup_messages.sql"; //$NON-NLS-1$
	
	private static final String CLEANUP_MESSAGES_IN = 	"/org/eclipse/dirigible/repository/ext/messaging/sql/cleanup_messages_in.sql"; //$NON-NLS-1$
	
	private static final String CLEANUP_MESSAGES_OUT =	"/org/eclipse/dirigible/repository/ext/messaging/sql/cleanup_messages_out.sql"; //$NON-NLS-1$
	
	private static final String GET_SUB = 				"/org/eclipse/dirigible/repository/ext/messaging/sql/get_sub.sql"; //$NON-NLS-1$
	
	private static final String SET_ROUTED_STATUS = 	"/org/eclipse/dirigible/repository/ext/messaging/sql/update_routed_status.sql"; //$NON-NLS-1$
	
	private DBUtils dbUtils;
	
	private DBSequenceUtils dbSequenceUtils;

	private DataSource dataSource;
	
	private boolean silentMode = true;

	public MessageHub(DataSource dataSource, boolean silentMode) {
		this.dataSource = dataSource;
		this.dbUtils = new DBUtils(dataSource);
		this.silentMode = silentMode;
		this.dbSequenceUtils = new DBSequenceUtils(dataSource);
	}
	
	public MessageHub(DataSource dataSource) {
		this(dataSource, true);
	}

	public DBUtils getDBUtils() {
		return this.dbUtils;
	}
	
	public boolean isSilentMode() {
		return silentMode;
	}
	
	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}

	@Override
	public void registerClient(String clientName, HttpServletRequest request) throws EMessagingException {
		if (isClientExists(clientName)) {
			logger.warn(String.format("Client with name %s has been already registered.", clientName));
			return;
		}
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_CLIENT, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, this.dbSequenceUtils.getNext(MSQ_CLIENT_SEQ));
				statement.setString(++i, clientName);
				statement.setString(++i, RequestUtils.getUser(request));

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void unregisterClient(String clientName, HttpServletRequest request) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, REMOVE_CLIENT, this.getClass());
				statement = connection.prepareStatement(script);
				
				statement.setString(1, clientName);

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	/**
	 * Get a ClientDefinition by Name or {@code null} if such a Client exists
	 * 
	 * @param clientName
	 */
	private ClientDefinition getClientDefinition(String clientName) throws EMessagingException {
		try {
			ClientDefinition clientDefinition = null;
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_CLIENT, this.getClass());
				statement = connection.prepareStatement(script);
				
				statement.setString(1, clientName);

				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					clientDefinition = new ClientDefinition();
					clientDefinition.setId(resultSet.getInt("MSGCLIENT_ID"));
					clientDefinition.setName(resultSet.getString("MSGCLIENT_NAME"));
					clientDefinition.setCreatedBy(resultSet.getString("MSGCLIENT_CREATED_BY"));
					clientDefinition.setCreatedAt(resultSet.getTimestamp("MSGCLIENT_CREATED_AT"));
				} else {
					throw new EMessagingException(String.format("Client %s does not exist.", clientName));
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return clientDefinition;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void registerTopic(String topic, HttpServletRequest request) throws EMessagingException {
		if (isTopicExists(topic)) {
			logger.warn(String.format("Topic with name %s has been already registered.", topic));
			return;
		}
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_TOPIC, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, this.dbSequenceUtils.getNext(MSQ_TOPIC_SEQ));
				statement.setString(++i, topic);
				statement.setString(++i, RequestUtils.getUser(request));

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void unregisterTopic(String topic, HttpServletRequest request) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, REMOVE_TOPIC, this.getClass());
				statement = connection.prepareStatement(script);
				
				statement.setString(1, topic);

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	/**
	 * Get a TopicDefinition by Name or {@code null} if such a Topic exists
	 * 
	 * @param topic
	 */
	private TopicDefinition getTopicDefinition(String topic) throws EMessagingException {
		try {
			TopicDefinition topicDefinition = null;
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_TOPIC, this.getClass());
				statement = connection.prepareStatement(script);
				
				statement.setString(1, topic);

				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					topicDefinition = new TopicDefinition();
					topicDefinition.setId(resultSet.getInt("MSGTOPIC_ID"));
					topicDefinition.setName(resultSet.getString("MSGTOPIC_NAME"));
					topicDefinition.setCreatedBy(resultSet.getString("MSGTOPIC_CREATED_BY"));
					topicDefinition.setCreatedAt(resultSet.getTimestamp("MSGTOPIC_CREATED_AT"));
				} else {
					throw new EMessagingException(String.format("Topic %s does not exist.", topic));
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return topicDefinition;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void subscribe(String subscriber, String topic, HttpServletRequest request) throws EMessagingException {
		if (isSilentMode()) {
			if (!isClientExists(subscriber)) {
				registerClient(subscriber, request);
			}
			if (!isTopicExists(topic)) {
				registerTopic(topic, request);
			}
		}
		if (isSubscriptionExists(subscriber, topic)) {
			logger.warn(String.format("Subscription with Client %s and Topic %s has been already registered.", subscriber, topic));
			return;
		}
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_SUBS, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, this.dbSequenceUtils.getNext(MSQ_SUBS_SEQ));
				ClientDefinition clientDefinition = getClientDefinition(subscriber);
				statement.setInt(++i, clientDefinition.getId());
				TopicDefinition topicDefinition = getTopicDefinition(topic);
				statement.setInt(++i, topicDefinition.getId());
				statement.setString(++i, RequestUtils.getUser(request));

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	@Override
	public void unsubscribe(String client, String topic, HttpServletRequest request) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, REMOVE_SUBS, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				ClientDefinition clientDefinition = getClientDefinition(client);
				statement.setInt(++i, clientDefinition.getId());
				TopicDefinition topicDefinition = getTopicDefinition(topic);
				statement.setInt(++i, topicDefinition.getId());

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void send(String sender, String topic, String subject, String body, HttpServletRequest request) throws EMessagingException {
		if (isSilentMode()) {
			if (!isClientExists(sender)) {
				registerClient(sender, request);
			}
			if (!isTopicExists(topic)) {
				registerTopic(topic, request);
			}
		}
		int msgId = insertMessage(topic, subject, body, request);
		insertIncoming(msgId, sender, request);
	}
	
	@Override
	public void send(MessageDefinition messageDefinition, HttpServletRequest request) throws EMessagingException {
		send(messageDefinition.getSender(), messageDefinition.getTopic(), messageDefinition.getSubject(), messageDefinition.getBody(), request);
	}

	private int insertMessage(String topic, String subject, String body, HttpServletRequest request) throws EMessagingException {
		try {
			int msgId = 0;
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_MESSAGE, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				msgId = this.dbSequenceUtils.getNext(MSQ_MESSAGE_SEQ);
				statement.setInt(++i, msgId);
				TopicDefinition topicDefinition = getTopicDefinition(topic);
				statement.setInt(++i, topicDefinition.getId());
				statement.setString(++i, subject);
				statement.setString(++i, body);
				statement.setString(++i, RequestUtils.getUser(request));

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			return msgId;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	private void insertIncoming(int msgId, String sender, HttpServletRequest request) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_IN, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, this.dbSequenceUtils.getNext(MSQ_IN_SEQ));
				statement.setInt(++i, msgId);
				ClientDefinition clientDefinition = getClientDefinition(sender);
				statement.setInt(++i, clientDefinition.getId());
				statement.setInt(++i, 0); // new
				statement.setString(++i, RequestUtils.getUser(request));

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}


	@Override
	public List<MessageDefinition> receive(String receiver, HttpServletRequest request) throws EMessagingException {
		if (isSilentMode()) {
			if (!isClientExists(receiver)) {
				registerClient(receiver, request);
			}
		}
		ClientDefinition clientDefinition = getClientDefinition(receiver);
		int receiverId = clientDefinition.getId();
		List<MessageDefinition> results = getOut(receiverId);
		for (MessageDefinition message: results) {
			setReceivedStatus(receiverId, message.getId());
		}
		return results;
	}

	private List<MessageDefinition> getOut(int receiver) throws EMessagingException {
		try {
			List<MessageDefinition> messageDefinitions = new ArrayList<MessageDefinition>();
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_OUT, this.getClass());
				statement = connection.prepareStatement(script);
				
				
				statement.setInt(1, receiver);

				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					MessageDefinition messageDefinition = new MessageDefinition();
					messageDefinition.setId(resultSet.getInt("MSG_ID"));
					messageDefinition.setTopic(resultSet.getString("MSGTOPIC_NAME"));
					messageDefinition.setSubject(resultSet.getString("MSG_SUBJECT"));
					messageDefinition.setBody(resultSet.getString("MSG_BODY"));
					messageDefinition.setSender(resultSet.getString("MSGCLIENT_NAME"));				
					messageDefinition.setCreatedBy(resultSet.getString("MSG_CREATED_BY"));
					messageDefinition.setCreatedAt(resultSet.getTimestamp("MSG_CREATED_AT"));
					messageDefinitions.add(messageDefinition);
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return messageDefinitions;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public List<MessageDefinition> receive(String receiver, String topic, HttpServletRequest request) throws EMessagingException {
		if (isSilentMode()) {
			if (!isClientExists(receiver)) {
				registerClient(receiver, request);
			}
			if (!isTopicExists(topic)) {
				registerTopic(topic, request);
			}
		}
		ClientDefinition clientDefinition = getClientDefinition(receiver);
		int receiverId = clientDefinition.getId();
		List<MessageDefinition> results = getOutByTopic(receiverId, topic);
		for (MessageDefinition message: results) {
			setReceivedStatus(receiverId, message.getId());
		}
		return results;
	}
	
	private List<MessageDefinition> getOutByTopic(int receiver, String topic) throws EMessagingException {
		try {
			List<MessageDefinition> messageDefinitions = new ArrayList<MessageDefinition>();
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_OUT_BY_TOPIC, this.getClass());
				statement = connection.prepareStatement(script);
				
				
				statement.setInt(1, receiver);
				TopicDefinition topicDefinition = getTopicDefinition(topic);
				statement.setInt(2, topicDefinition.getId());

				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					MessageDefinition messageDefinition = new MessageDefinition();
					messageDefinition.setId(resultSet.getInt("MSG_ID"));
					messageDefinition.setTopic(topic);
					messageDefinition.setSubject(resultSet.getString("MSG_SUBJECT"));
					messageDefinition.setBody(resultSet.getString("MSG_BODY"));
					messageDefinition.setSender(resultSet.getString("MSGCLIENT_NAME"));				
					messageDefinition.setCreatedBy(resultSet.getString("MSG_CREATED_BY"));
					messageDefinition.setCreatedAt(resultSet.getTimestamp("MSG_CREATED_AT"));
					messageDefinitions.add(messageDefinition);
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return messageDefinitions;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	private void setReceivedStatus(int receiver, int msgId) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, SET_RECEIVED_STATUS, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, 1); // received
				statement.setInt(++i, receiver);
				statement.setInt(++i, msgId);
				
				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	private void setRoutedStatus(int msgId) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, SET_ROUTED_STATUS, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, 1); // received
				statement.setInt(++i, msgId);
				
				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void route() throws EMessagingException {
		List<IncomingNewDefinition> incomingsNew = getInNew();
		int currentTopic = -1;
		List<SubscriptionPairDefinition> subscribers = null;
		for (IncomingNewDefinition incomingNewDefinition : incomingsNew) {
			int topic = incomingNewDefinition.getTopicId();
			if (topic != currentTopic) {
				subscribers = getSubscribersForTopic(topic);
			}
			for (SubscriptionPairDefinition subscription : subscribers) {
				insertOut(incomingNewDefinition.getMessageId(), subscription.getSubscriberId());
				setRoutedStatus(incomingNewDefinition.getMessageId());
			}
		}
		
	}

	private List<SubscriptionPairDefinition> getSubscribersForTopic(int topicId) throws EMessagingException {
		try {
			List<SubscriptionPairDefinition> subscriptionPairDefinitions = new ArrayList<SubscriptionPairDefinition>();
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_SUBS_BY_TOPIC, this.getClass());
				statement = connection.prepareStatement(script);
				
				
				statement.setInt(1, topicId);

				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					SubscriptionPairDefinition subscriptionPairDefinition = new SubscriptionPairDefinition();
					subscriptionPairDefinition.setTopicId(resultSet.getInt("MSGSUB_MSGTOPIC_ID"));
					subscriptionPairDefinition.setSubscriberId(resultSet.getInt("MSGSUB_SUBSCRIBER"));
					subscriptionPairDefinitions.add(subscriptionPairDefinition);
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return subscriptionPairDefinitions;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}
	
	@Override
	public boolean isSubscriptionExists(String subscriber, String topic) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_SUB, this.getClass());
				statement = connection.prepareStatement(script);
				
				
				ClientDefinition clientDefinition = getClientDefinition(subscriber);
				statement.setInt(1, clientDefinition.getId());
				TopicDefinition topicDefinition = getTopicDefinition(topic);
				statement.setInt(2, topicDefinition.getId());

				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					return true;
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return false;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	private void insertOut(int msgId, int receiverId) throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, INSERT_OUT, this.getClass());
				statement = connection.prepareStatement(script);
				
				int i=0;
				statement.setInt(++i, this.dbSequenceUtils.getNext(MSQ_OUT_SEQ));
				statement.setInt(++i, msgId);
				statement.setInt(++i, receiverId);
				statement.setInt(++i, 0); // new
				statement.setString(++i, ICommonConstants.GUEST);

				statement.executeUpdate();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	private List<IncomingNewDefinition> getInNew() throws EMessagingException {
		try {
			List<IncomingNewDefinition> incomingNewDefinitions = new ArrayList<IncomingNewDefinition>();
			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = dataSource.getConnection();
				String script = getDBUtils().readScript(connection, GET_IN_NEW, this.getClass());
				statement = connection.prepareStatement(script);
				
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					IncomingNewDefinition incomingNewDefinition = new IncomingNewDefinition();
					incomingNewDefinition.setMessageId(resultSet.getInt("MSGIN_MSG_ID"));
					incomingNewDefinition.setTopicId(resultSet.getInt("MSG_MSGTOPIC_ID"));
					incomingNewDefinitions.add(incomingNewDefinition);
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
			
			return incomingNewDefinitions;
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
	}

	@Override
	public void cleanup() throws EMessagingException {
		try {
			Connection connection = null;
			PreparedStatement statement = null;
			GregorianCalendar last = new GregorianCalendar();
			last.add(Calendar.WEEK_OF_YEAR, -1);
			try {
				connection = dataSource.getConnection();

				String script;
				try {
					script = getDBUtils().readScript(connection, CLEANUP_MESSAGES_OUT, this.getClass());
					statement = connection.prepareStatement(script);
					statement.setTimestamp(1, new Timestamp(last.getTime().getTime()));
					statement.executeUpdate();
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
				
				try {
					script = getDBUtils().readScript(connection, CLEANUP_MESSAGES_IN, this.getClass());
					statement = connection.prepareStatement(script);
					statement.setTimestamp(1, new Timestamp(last.getTime().getTime()));
					statement.executeUpdate();
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
				
				try {
					script = getDBUtils().readScript(connection, CLEANUP_MESSAGES, this.getClass());
					statement = connection.prepareStatement(script);
					statement.setTimestamp(1, new Timestamp(last.getTime().getTime()));
					statement.executeUpdate();
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
				
				
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error(DATABASE_ERROR, e);
				}
			}
		} catch (Exception e) {
			throw new EMessagingException(e);
		}
		
	}

	@Override
	public boolean isClientExists(String clientName) throws EMessagingException {
		try {
			getClientDefinition(clientName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isTopicExists(String topicName) throws EMessagingException {
		try {
			getTopicDefinition(topicName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
