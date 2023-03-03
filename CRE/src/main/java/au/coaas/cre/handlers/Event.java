package au.coaas.cre.handlers;

import au.coaas.cqp.proto.ContextEntity;

public class Event {
	private String subscriptionID;
	private String providerID;
	private String subscriptionValue;
	private String timestamp;
	private ContextEntity contextEntity;

	public String getSubscriptionID() {
		return subscriptionID;
	}
	public void setSubscriptionID(String subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public String getProviderID() {
		return providerID;
	}
	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getSubscriptionValue() {
		return subscriptionValue;
	}
	public void setSubscriptionValue(String subscriptionValue) {
		this.subscriptionValue = subscriptionValue;
	}

	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public ContextEntity getContextEntity() {
		return contextEntity;
	}
	public void setContextEntity(ContextEntity contextEntity) {
		this.contextEntity = contextEntity;
	}
}
