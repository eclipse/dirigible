package org.eclipse.dirigible.repository.ext.messaging;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Messaging Service is the facade for accessing the messages store 
 * @see MessagingBus
 *
 */
public interface IMessagingService {
	
	/**
	 * Registers a Client by Name or does nothing if such a Client exists
	 * 
	 * @param clientName
	 */
	public void registerClient(String clientName, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Unregisters a Client by Name
	 * 
	 * @param clientName
	 */
	public void unregisterClient(String clientName, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Checks the existence of a Client by Name
	 * 
	 * @param clientName
	 */
	public boolean isClientExists(String clientName) throws EMessagingException;
	
	/**
	 * Registers a Topic by Name or does nothing if such a Topic exists
	 * 
	 * @param topic
	 */
	public void registerTopic(String topic, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Unregisters a Topic by Name
	 * 
	 * @param topic
	 */
	public void unregisterTopic(String topic, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Checks the existence of a Topic by Name
	 * 
	 * @param topicName
	 */
	public boolean isTopicExists(String topicName) throws EMessagingException;
	
	/**
	 * Subscribes a given Client for a given Topic,
	 * so that this Client will get the new messages 
	 * from this Topic during the Routing Process 
	 * 
	 * @param client
	 * @param topic
	 */
	public void subscribe(String client, String topic, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Un-subscribes a given Client from a given Topic
	 * 
	 * @param client
	 * @param topic
	 */
	public void unsubscribe(String client, String topic, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Checks whether subscription of a given Client to a given Topic exists
	 * 
	 * @param subscriber
	 * @param topic
	 * @return
	 * @throws EMessagingException
	 */
	public boolean isSubscriptionExists(String subscriber, String topic) throws EMessagingException;
	
	/**
	 * Sends a message to the hub
	 * 
	 * @param sender
	 * @param topic
	 * @param subject
	 * @param body
	 */
	public void send(String sender, String topic, String subject, String body, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Sends a message to the hub
	 * 
	 * @param messageDefinition
	 * @throws EMessagingException
	 */
	public void send(MessageDefinition messageDefinition, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Get all the new messages for this Client for all the Topics
	 * 
	 * @param receiver
	 */
	public List<MessageDefinition> receive(String receiver, HttpServletRequest request) throws EMessagingException;
	
	/**
	 * Get all the new messages for this Client for a given Topic
	 * 
	 * @param receiver
	 */
	public List<MessageDefinition> receive(String receiver, String topic, HttpServletRequest request) throws EMessagingException;

	/**
	 * Triggers the Routing Process.
	 * Takes new Incoming messages and creates
	 * Outgoing links for all subscribed Clients,
	 * so that they can retrieve them by calling {@code receive()} method
	 * 
	 */
	public void route() throws EMessagingException;
	
	/**
	 * Removes older messages
	 */
	public void cleanup() throws EMessagingException;

}
