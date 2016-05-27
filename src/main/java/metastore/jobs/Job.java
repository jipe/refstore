package metastore.jobs;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import metastore.persistence.PersistenceException;
import metastore.persistence.PersistentObject;

public abstract class Job extends PersistentObject implements Runnable {

	public enum Status {
		PENDING,
		RUNNING,
		PAUSED,
		ABORTED,
		FINISHED,
		FAILED
	}

	private JobStore jobStore;

	private Status status;

	private Integer jobDefinitionId;
	private JobDefinition jobDefinition;

	@Override
	public void save() throws PersistenceException {
		try {
			jobStore.saveJobDefinition(jobDefinition);
			jobStore.saveJob(this);
			jobDefinitionId = jobDefinition.getId();
		} catch (JobStoreException e) {
			throw new PersistenceException("Error saving job instance", e);
		}
	}

	public Status getStatus() {
		return status;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	void setJobStore(JobStore jobStore) {
		this.jobStore = jobStore;
	}

	public JobDefinition getJobDefinition() throws JobStoreException {
		if (jobDefinition == null) {
			jobDefinition = jobStore.getJobDefinition(jobDefinitionId);
		}
		return jobDefinition;
	}

	void setJobDefinition(JobDefinition jobDefinition) {
		this.jobDefinition = jobDefinition;
	}

	public String getStatusText() {
		return "No description";
	}

	/**
	 * Read job instance data from the given reader.
	 * 
	 * @param reader
	 * @throws JobException
	 * @throws IOException
	 */
	public abstract void readData(Reader reader) throws JobException, IOException;

	/**
	 * Write job instance data to the given writer.
	 * 
	 * @param writer
	 * @throws JobException
	 * @throws IOException
	 */
	public abstract void writeData(Writer writer) throws JobException, IOException;

}
