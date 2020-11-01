/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaFacade implements IScriptingFacade {
	
	private static final String DEFAULT_BOOTSTRAP_SERVER = "localhost:9092";

	private static final Logger logger = LoggerFactory.getLogger(KafkaFacade.class);
	
	private static Map<String, Producer<String, String>> PRODUCERS = Collections.synchronizedMap(new HashMap());
	private static Map<String, KafkaConsumerRunner> CONSUMERS = Collections.synchronizedMap(new HashMap());

	/**
	 * Send a key-value pair to a topic.
	 *
	 * @param destination the destination
	 * @param key     the key
	 * @param value   the value
	 * @param configuration   the configuration
	 */
	public static final void send(String destination, String key, String value, String configuration) {
		if (configuration == null) {
			configuration = "{}";
		}
		
		Map map = GsonHelper.GSON.fromJson(configuration, Map.class);
		Producer<String, String> producer = null;
		
		String server = map.get("bootstrap.servers") != null ? map.get("bootstrap.servers").toString() : DEFAULT_BOOTSTRAP_SERVER;
		if (server != null) {
			producer = PRODUCERS.get(server);
		}
		
		if (producer == null) {
			Properties props = new Properties();
			for (Object k : map.keySet()) {
				props.put(k, map.get(k));
			}
			if (props.get("bootstrap.servers") == null) {
				// default to localhost
				props.put("bootstrap.servers", DEFAULT_BOOTSTRAP_SERVER);
			}
			if (props.get("acks") == null) {
				// default to all
				props.put("acks", "all");
			}
			if (props.get("key.serializer") == null) {
				// default to org.apache.kafka.common.serialization.StringSerializer
				props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			}
			if (props.get("value.serializer") == null) {
				// default to org.apache.kafka.common.serialization.StringSerializer
				props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			}
			producer = new KafkaProducer<>(props);
			PRODUCERS.put(server, producer);
			logger.info("Kafka Producer [{}] created.", server);
		}
		
		producer.send(new ProducerRecord<String, String>(destination, key, value));

		//producer.close();
	}
	
	/**
	 * Close the producer per server endpoint
	 * 
	 * @param configuration
	 */
	public static final void closeProducer(String configuration) {
		if (configuration == null) {
			configuration = "{}";
		}
		
		Map map = GsonHelper.GSON.fromJson(configuration, Map.class);
		Producer<String, String> producer = null;
		
		String server = map.get("bootstrap.servers") != null ? map.get("bootstrap.servers").toString() : DEFAULT_BOOTSTRAP_SERVER;
		if (server != null) {
			producer = PRODUCERS.get(server);
		}
		
		if (producer != null) {
			producer.close();
			PRODUCERS.remove(server);
		} else {
			logger.warn("Kafka Producer [{}] has not been started yet.", server);
		}
	}
	
	public static final void startListening(String destination, String handler, int timeout, String configuration) {
		if (configuration == null) {
			configuration = "{}";
		}
		
		Map map = GsonHelper.GSON.fromJson(configuration, Map.class);
		
		Consumer<String, String> consumer = null;
		KafkaConsumerRunner consumerRunner = null;
		
		String location = null;
		String server = map.get("bootstrap.servers") != null ? map.get("bootstrap.servers").toString() : DEFAULT_BOOTSTRAP_SERVER;
		location = createLocation(destination, server); 
		consumerRunner = CONSUMERS.get(location);
		
		if (consumerRunner == null) {
			Properties props = new Properties();
			for (Object k : map.keySet()) {
				props.put(k, map.get(k));
			}
			if (props.get("bootstrap.servers") == null) {
				// default to localhost
				props.put("bootstrap.servers", DEFAULT_BOOTSTRAP_SERVER);
			}
			if (props.get("group.id") == null) {
				// default to handler
				props.put("group.id", handler != null ? handler : destination);
			}
			if (props.get("enable.auto.commit") == null) {
				// autocommit
				props.put("enable.auto.commit", "true");
			}
			if (props.get("auto.commit.interval.ms") == null) {
				// autocommit interval 1000
				props.put("auto.commit.interval.ms", "1000");
			}
			if (props.get("key.deserializer") == null) {
				// default to org.apache.kafka.common.serialization.StringSerializer
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			}
			if (props.get("value.deserializer") == null) {
				// default to org.apache.kafka.common.serialization.StringSerializer
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			}
			
			consumer = new KafkaConsumer<>(props);
			
			consumerRunner = new KafkaConsumerRunner(consumer, destination, handler, timeout);
			Thread consumerThread = new Thread(consumerRunner);
			consumerThread.setDaemon(false);
			consumerThread.start();
			CONSUMERS.put(location, consumerRunner);
			logger.info("Kafka Consumer [{}] created.", location);
		} else {
			logger.warn("Kafka Consumer [{}] has already been started.", location);
		}
	}
	
	public static final void stopListening(String destination, String configuration) {
		if (configuration == null) {
			configuration = "{}";
		}
		
		Map map = GsonHelper.GSON.fromJson(configuration, Map.class);
		
		KafkaConsumerRunner consumerRunner = null;
		
		String location = null;
		String server = map.get("bootstrap.servers") != null ? map.get("bootstrap.servers").toString() : DEFAULT_BOOTSTRAP_SERVER;
		location = createLocation(destination, server);
		consumerRunner = CONSUMERS.get(location);
		
		if (consumerRunner != null) {
			consumerRunner.stop();
			CONSUMERS.remove(location);
		} else {
			logger.warn("Kafka Consumer [" + location + "] has not been started yet.");
		}
	}

	/**
	 * Create internal identifier for a consumer
	 * 
	 * @param destination the destination
	 * @param server the server
	 * @return the identifier
	 */
	private static String createLocation(String destination, String server) {
		return "[" + server + "]:[" + destination + "]";
	}

}
