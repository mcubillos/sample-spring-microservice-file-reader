package org.process.model;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class BaseEntity {
	@Id
	protected ObjectId id;
	@CreatedDate
	protected Date creationTime;
	@LastModifiedDate
	protected Date modificationTime;

	public BaseEntity() {
		super();
	}

	public ObjectId getId() {
		return id;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public Date getModificationTime() {
		return modificationTime;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public abstract String toString();

}
