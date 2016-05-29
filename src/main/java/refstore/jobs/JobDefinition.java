package refstore.jobs;

import refstore.persistence.PersistenceException;
import refstore.persistence.PersistentObject;

public class JobDefinition extends PersistentObject {

	private final String name;
	private final Class<? extends Job> type;
	private final JobStore jobStore;

	public JobDefinition(String name, Class<? extends Job> type, JobStore jobStore) {
		this.name     = name;
		this.type     = type;
		this.jobStore = jobStore;
	}

	public String getName() {
		return name;
	}

	public Class<? extends Job> getType() {
		return type;
	}

	@Override
	public void save() throws PersistenceException {
		try {
			jobStore.saveJobDefinition(this);
		} catch (JobStoreException e) {
			throw new PersistenceException("Error saving job definition", e);
		}
	}

	public Job createJob() throws JobStoreException, PersistenceException {
		try {
			save();
			Job job = type.newInstance();
			job.setJobDefinition(this);
			job.setJobStore(jobStore);
			return job;
		} catch (InstantiationException e) {
			throw new JobStoreException(String.format("Error instantiating job. Class %s has wrong type or is missing nullary constructor", type.getCanonicalName()));
		} catch (IllegalAccessException e) {
			throw new JobStoreException(String.format("Error instantiating job. Class %s has no accessible nullary constructor", type.getCanonicalName()));
		}
	}
}
