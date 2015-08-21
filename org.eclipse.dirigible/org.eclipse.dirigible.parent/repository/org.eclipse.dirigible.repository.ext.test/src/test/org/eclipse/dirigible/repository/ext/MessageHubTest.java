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
	
	private MessageHub messageHubSilent;

	@Before
	public void setUp() {
		dataSource = DataSourceUtils.createLocal();
		messageHub = new MessageHub(dataSource, false, null);
		messageHubSilent = new MessageHub(dataSource, null);
	}

	@Test
	public void testRegisterClient() {
		try {
			String clientName = "MSG_CLIENT_TEST1";
			messageHub.registerClient(clientName);
			assertTrue(messageHub.isClientExists(clientName));
			messageHub.unregisterClient(clientName);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnregisterClient() {
		try {
			String clientName = "MSG_CLIENT_TEST2";
			messageHub.registerClient(clientName);
			assertTrue(messageHub.isClientExists(clientName));
			messageHub.unregisterClient(clientName);
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
			messageHub.registerTopic(topicName);
			assertTrue(messageHub.isTopicExists(topicName));
			messageHub.unregisterTopic(topicName);
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnregisterTopic() {
		try {
			String topicName = "MSG_TOPIC_TEST2";
			messageHub.registerTopic(topicName);
			assertTrue(messageHub.isTopicExists(topicName));
			messageHub.unregisterTopic(topicName);
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
				messageHub.subscribe(client, topic);
			} catch (Exception e) {
				assertTrue(e.getMessage(), e.getMessage().contains("Client MSG_SUB_CLIENT_TEST1 does not exist"));
			}
			messageHub.registerClient(client);
			try {
				messageHub.subscribe(client, topic);
			} catch (Exception e) {
				assertTrue(e.getMessage(), e.getMessage().contains("Topic MSG_SUB_TOPIC_TEST1 does not exist"));
			}
			messageHub.registerTopic(topic);
			messageHub.subscribe(client, topic);
			assertTrue(messageHub.isSubscriptionExists(client, topic));
			messageHub.unsubscribe(client, topic);
			messageHub.unregisterClient(client);
			messageHub.unregisterTopic(topic);
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
			messageHub.registerClient(client);
			messageHub.registerTopic(topic);
			messageHub.subscribe(client, topic);
			assertTrue(messageHub.isSubscriptionExists(client, topic));
			messageHub.unsubscribe(client, topic);
			assertFalse(messageHub.isSubscriptionExists(client, topic));
			messageHub.unregisterClient(client);
			messageHub.unregisterTopic(topic);
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
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerTopic(topic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(receiver);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendReceiveSecondReceive() {
		try {
			String sender = "MSG_SEND_SENDER_TEST2";
			String receiver = "MSG_SEND_RECEIVER_TEST2";
			String topic = "MSG_SEND_TOPIC_TEST2";
			String subject = "Subject2";
			String body = "Body2";
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerTopic(topic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			
			List<MessageDefinition> messages = messageHub.receive(receiver);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messages = messageHub.receive(receiver);
			assertNotNull("Messages object is null", messages);
			assertTrue("Messages list is not empty", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
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
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerTopic(topic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receiveByTopic(receiver, topic);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
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
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerTopic(topic);
			messageHub.registerTopic(anotherTopic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receiveByTopic(receiver, anotherTopic);
			assertNotNull(messages);
			assertTrue("Messages list is not empty for another topic", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
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
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerClient(anotherReceiver);
			messageHub.registerTopic(topic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			List<MessageDefinition> messages = messageHub.receive(anotherReceiver);
			assertNotNull(messages);
			assertTrue("Messages list is not empty for another topic", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSendReceiveSecondRoute() {
		try {
			String sender = "MSG_SEND_SENDER_TEST6";
			String receiver = "MSG_SEND_RECEIVER_TEST6";
			String topic = "MSG_SEND_TOPIC_TEST6";
			String subject = "Subject6";
			String body = "Body6";
			messageHub.registerClient(sender);
			messageHub.registerClient(receiver);
			messageHub.registerTopic(topic);
			messageHub.subscribe(receiver, topic);
			messageHub.send(sender, topic, subject, body);
			messageHub.route();
			
			List<MessageDefinition> messages = messageHub.receive(receiver);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHub.route();
			
			messages = messageHub.receive(receiver);
			assertNotNull("Messages object is null", messages);
			assertTrue("Messages list is not empty", messages.size() == 0);
			
			messageHub.unsubscribe(receiver, topic);
			messageHub.unregisterClient(sender);
			messageHub.unregisterClient(receiver);
			messageHub.unregisterTopic(topic);
			
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

	@Test
	public void testSendReceiveSilent() {
		try {
			String sender = "MSG_SEND_SENDER_TEST7";
			String receiver = "MSG_SEND_RECEIVER_TEST7";
			String topic = "MSG_SEND_TOPIC_TEST7";
			String subject = "Subject7";
			String body = "Body7";
			// no preliminary registrations needed in this case - 'silent'
			messageHubSilent.subscribe(receiver, topic);
			messageHubSilent.send(sender, topic, subject, body);
			messageHubSilent.route();
			List<MessageDefinition> messages = messageHubSilent.receive(receiver);
			assertNotNull(messages);
			assertTrue(messages.size() > 0);
			MessageDefinition message = messages.get(0);
			assertEquals(message.getSender(), sender);
			assertEquals(message.getTopic(), topic);
			assertEquals(message.getSubject(), subject);
			assertEquals(message.getBody(), body);
			
			messageHubSilent.unsubscribe(receiver, topic);
			messageHubSilent.unregisterClient(sender);
			messageHubSilent.unregisterClient(receiver);
			messageHubSilent.unregisterTopic(topic);
			
		} catch (EMessagingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
