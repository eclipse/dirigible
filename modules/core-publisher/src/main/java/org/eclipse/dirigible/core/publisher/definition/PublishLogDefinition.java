package org.eclipse.dirigible.core.publisher.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "DIRIGIBLE_PUBLISH_LOGS")
public class PublishLogDefinition {

	@Id
	@GeneratedValue
	@Column(name = "PUBLOG_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	@Column(name = "PUBLOG_SOURCE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String source;

	@Column(name = "PUBLOG_TARGET", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String target;

	@Column(name = "PUBLOG_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
	private String createdBy;

	@Column(name = "PUBLOG_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	public long getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedAt() {
		if (createdAt == null) {
			return null;
		}
		return new Timestamp(createdAt.getTime());
	}

	public void setCreatedAt(Timestamp createdAt) {
		if (createdAt == null) {
			this.createdAt = null;
			return;
		}
		this.createdAt = new Timestamp(createdAt.getTime());
	}

}
