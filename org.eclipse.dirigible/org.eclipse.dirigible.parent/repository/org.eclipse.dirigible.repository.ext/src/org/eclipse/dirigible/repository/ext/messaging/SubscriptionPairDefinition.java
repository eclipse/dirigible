package org.eclipse.dirigible.repository.ext.messaging;

class SubscriptionPairDefinition {
	
	private int topicId;
	
	private int subscriberId;

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

}
