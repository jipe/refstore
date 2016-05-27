package metastore.persistence;

import java.util.Date;

public abstract class PersistentObject {

	private Integer id;
	private Date createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isPersisted() {
		return id != null;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public abstract void save() throws PersistenceException;

}
