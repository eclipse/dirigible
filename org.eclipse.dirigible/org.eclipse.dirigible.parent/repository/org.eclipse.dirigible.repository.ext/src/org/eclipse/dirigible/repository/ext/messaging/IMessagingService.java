package org.eclipse.dirigible.repository.ext.messaging;

import java.util.List;

/**
 * Messaging Service is the facade for accessing the messages store
 */
public interface IMessagingService {

	/**
	 * Registers a Client by Name or does nothing if such a Client exists
	 *
	 * @param clientName
	 *            the name of the client
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void registerClient(String clientName) throws EMessagingException;

	/**
	 * Unregisters a Client by Name
	 *
	 * @param clientName
	 *            the name of the client
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void unregisterClient(String clientName) throws EMessagingException;

	/**
	 * Checks the existence of a Client by Name
	 *
	 * @param clientName
	 *            the name of the client
	 * @return true if exists
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public boolean isClientExists(String clientName) throws EMessagingException;

	/**
	 * Registers a Topic by Name or does nothing if such a Topic exists
	 *
	 * @param topic
	 *            the name of the topic
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void registerTopic(String topic) throws EMessagingException;

	/**
	 * Unregisters a Topic by Name
	 *
	 * @param topic
	 *            the name of the topic
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void unregisterTopic(String topic) throws EMessagingException;

	/**
	 * Checks the existence of a Topic by Name
	 *
	 * @param topicName
	 *            the name of the topic
	 * @return whether the topic exists
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public boolean isTopicExists(String topicName) throws EMessagingException;

	/**
	 * Subscribes a given Client for a given Topic,
	 * so that this Client will get the new messages
	 * from this Topic after the Routing Process
	 *
	 * @param client
	 *            the name of the client
	 * @param topic
	 *            the name of the topic
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void subscribe(String client, String topic) throws EMessagingException;

	/**
	 * Un-subscribes a given Client from a given Topic
	 *
	 * @param client
	 *            the name of the client
	 * @param topic
	 *            the name of the topic
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void unsubscribe(String client, String topic) throws EMessagingException;

	/**
	 * Checks whether subscription of a given Client to a given Topic exists
	 *
	 * @param subscriber
	 *            the name of the subscriber
	 * @param topic
	 *            the name of the topic
	 * @return true if subscription exists
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public boolean isSubscriptionExists(String subscriber, String topic) throws EMessagingException;

	/**
	 * Sends a message to the hub
	 *
	 * @param sender
	 *            the name of the sender
	 * @param topic
	 *            the name of the topic
	 * @param subject
	 *            the subject
	 * @param body
	 *            the body
	 * @throws EMessagingException
	 *             Message
	 */
	public void send(String sender, String topic, String subject, String body) throws EMessagingException;

	/**
	 * Sends a message to the hub
	 *
	 * @param messageDefinition
	 *            the message definition
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void sendMessage(MessageDefinition messageDefinition) throws EMessagingException;

	/**
	 * Get all the new messages for this Client for all the Topics
	 *
	 * @param receiver
	 *            the receiver
	 * @return the list of message definitions
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public List<MessageDefinition> receive(String receiver) throws EMessagingException;

	/**
	 * Get all the new messages for this Client for a given Topic
	 *
	 * @param receiver
	 *            the receiver
	 * @param topic
	 *            the name of the topic
	 * @return the list of message definitions by the given topic
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public List<MessageDefinition> receiveByTopic(String receiver, String topic) throws EMessagingException;

	/**
	 * Triggers the Routing Process.
	 * Takes new Incoming messages and creates
	 * Outgoing links for all subscribed Clients,
	 * so that they can retrieve them by calling {@code receive()} method
	 *
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void route() throws EMessagingException;

	/**
	 * Removes older messages
	 *
	 * @throws EMessagingException
	 *             Message Exception
	 */
	public void cleanup() throws EMessagingException;

}
