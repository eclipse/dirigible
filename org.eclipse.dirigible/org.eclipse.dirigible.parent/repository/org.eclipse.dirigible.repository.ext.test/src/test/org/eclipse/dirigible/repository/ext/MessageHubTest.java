package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.ext.messaging.EMessagingException;
import org.eclipse.dirigible.repository.ext.messaging.MessageDefinition;
import org.eclipse.dirigible.repository.ext.messaging.MessageHub;
import org.junit.Before;
import org.junit.Test;

public class MessageHubTest {
	
	private DataSource dataSource;
	
	private MessageHub messageHub;

	@Before
	public void setUp() {
		dataSource = DataSourceUtils.createLocal();
		messageHub = MessageHub.getInstance(dataSource);
	}

	@Test
	public void testRegisterClient() {
		try {
			String clientName = "MSG_CLIENT_TEST1";
			messageHub.registerClient(clientName, null);
			assertTrue(messageHub.isClientExists(clientName));
			messageHub.unregisterClient(clientName, null);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnregisterClient() {
		try {
			String clientName = "MSG_CLIENT_TEST2";
			messageHub.registerClient(clientName, null);
			assertTrue(messageHub.isClientExists(clientName));
			messageHub.unregisterClient(clientName, null);
			assertFalse(messageHub.isClientExists(clientName));
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRegisterTopic() {
		try {
			String topicName = "MSG_TOPIC_TEST1";
			messageHub.registerTopic(topicName, null);
			assertTrue(messageHub.isTopicExists(topicName));
			messageHub.unregisterTopic(topicName, null);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnregisterTopic() {
		try {
			String topicName = "MSG_TOPIC_TEST2";
			messageHub.registerTopic(topicName, null);
			assertTrue(messageHub.isTopicExists(topicName));
			messageHub.unregisterTopic(topicName, null);
			assertFalse(messageHub.isTopicExists(topicName));
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSubscribe() {
		try {
			String client = "MSG_SUB_CLIENT_TEST1";
			String topic = "MSG_SUB_TOPIC_TEST1";
			try {
				messageHub.subscribe(client, topic, null);
			} catch (Exception e) {
				assertTrue(e.getMessage(), e.getMessage().contains("Client MSG_SUB_CLIENT_TEST1 does not exist"));
			}
			messageHub.registerClient(client, null);
			try {
				messageHub.subscribe(client, topic, null);
			} catch (Exception e) {
				assertTrue(e.getMessage(), e.getMessage().contains("Topic MSG_SUB_TOPIC_TEST1 does not exist"));
			}
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(client, topic, null);
			assertTrue(messageHub.isSubscriptionExists(client, topic));
			messageHub.unsubscribe(client, topic, null);
			messageHub.unregisterClient(client, null);
			messageHub.unregisterTopic(topic, null);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnsubscribe() {
		try {
			String client = "MSG_SUB_CLIENT_TEST2";
			String topic = "MSG_SUB_TOPIC_TEST2";
			messageHub.registerClient(client, null);
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(client, topic, null);
			assertTrue(messageHub.isSubscriptionExists(client, topic));
			messageHub.unsubscribe(client, topic, null);
			assertFalse(messageHub.isSubscriptionExists(client, topic));
			messageHub.unregisterClient(client, null);
			messageHub.unregisterTopic(topic, null);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSendReceiveStandard() {
		try {
			String sender = "MSG_SEND_SENDER_TEST1";
			String receiver = "MSG_SEND_RECEIVER_TEST1";
			String topic = "MSG_SEND_TOPIC_TEST1";
			String subject = "Subject1";
			String body = "Body1";
			messageHub.registerClient(sender, null);
			messageHub.registerClient(receiver, null);
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(receiver, topic, null);
			messageHub.send(sender, topic, subject, body, null);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(receiver, null);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHub.unsubscribe(receiver, topic, null);
			messageHub.unregisterClient(sender, null);
			messageHub.unregisterClient(receiver, null);
			messageHub.unregisterTopic(topic, null);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendReceiveSecondTime() {
		try {
			String sender = "MSG_SEND_SENDER_TEST2";
			String receiver = "MSG_SEND_RECEIVER_TEST2";
			String topic = "MSG_SEND_TOPIC_TEST2";
			String subject = "Subject2";
			String body = "Body2";
			messageHub.registerClient(sender, null);
			messageHub.registerClient(receiver, null);
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(receiver, topic, null);
			messageHub.send(sender, topic, subject, body, null);
			messageHub.route();
			
			List<MessageDefinition> messages = messageHub.receive(receiver, null);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messages = messageHub.receive(receiver, null);
			assertNotNull("Messages object is null", messages);
			assertTrue("Messages list is not empty", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic, null);
			messageHub.unregisterClient(sender, null);
			messageHub.unregisterClient(receiver, null);
			messageHub.unregisterTopic(topic, null);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendReceiveByTopic() {
		try {
			String sender = "MSG_SEND_SENDER_TEST3";
			String receiver = "MSG_SEND_RECEIVER_TEST3";
			String topic = "MSG_SEND_TOPIC_TEST3";
			String subject = "Subject3";
			String body = "Body3";
			messageHub.registerClient(sender, null);
			messageHub.registerClient(receiver, null);
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(receiver, topic, null);
			messageHub.send(sender, topic, subject, body, null);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(receiver, topic, null);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHub.unsubscribe(receiver, topic, null);
			messageHub.unregisterClient(sender, null);
			messageHub.unregisterClient(receiver, null);
			messageHub.unregisterTopic(topic, null);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendReceiveByAnotherTopic() {
		try {
			String sender = "MSG_SEND_SENDER_TEST4";
			String receiver = "MSG_SEND_RECEIVER_TEST4";
			String topic = "MSG_SEND_TOPIC_TEST4";
			String anotherTopic = "ANOTHER_TOPIC4";
			String subject = "Subject4";
			String body = "Body4";
			messageHub.registerClient(sender, null);
			messageHub.registerClient(receiver, null);
			messageHub.registerTopic(topic, null);
			messageHub.registerTopic(anotherTopic, null);
			messageHub.subscribe(receiver, topic, null);
			messageHub.send(sender, topic, subject, body, null);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(receiver, anotherTopic, null);
			assertNotNull(messages);
			assertTrue("Messages list is not empty for another topic", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic, null);
			messageHub.unregisterClient(sender, null);
			messageHub.unregisterClient(receiver, null);
			messageHub.unregisterTopic(topic, null);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendReceiveByAnotherReceiver() {
		try {
			String sender = "MSG_SEND_SENDER_TEST5";
			String receiver = "MSG_SEND_RECEIVER_TEST5";
			String anotherReceiver = "MSG_SEND_ANOTHER_RECEIVER_TEST5";
			String topic = "MSG_SEND_TOPIC_TEST5";
			String subject = "Subject3";
			String body = "Body3";
			messageHub.registerClient(sender, null);
			messageHub.registerClient(receiver, null);
			messageHub.registerClient(anotherReceiver, null);
			messageHub.registerTopic(topic, null);
			messageHub.subscribe(receiver, topic, null);
			messageHub.send(sender, topic, subject, body, null);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(anotherReceiver, null);
			assertNotNull(messages);
			assertTrue("Messages list is not empty for another topic", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic, null);
			messageHub.unregisterClient(sender, null);
			messageHub.unregisterClient(receiver, null);
			messageHub.unregisterTopic(topic, null);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCleanup() {
		try {
			messageHub.cleanup();
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
