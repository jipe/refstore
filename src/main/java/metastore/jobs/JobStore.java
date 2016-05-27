package metastore.jobs;

import java.util.List;

public interface JobStore {

	List<JobDefinition> listJobDefinitions() throws JobStoreException;

	void saveJobDefinition(JobDefinition jobDefinition) throws JobStoreException;

	JobDefinition getJobDefinition(int id) throws JobStoreException;

	JobDefinition createJobDefinition(String name, Class<? extends Job> type);

	void deleteJobDefinition(int id) throws JobStoreException;

	List<Job> listJobs(JobDefinition jobDefinition) throws JobStoreException;

	void saveJob(Job job) throws JobStoreException;

	Job getJob(int id) throws JobStoreException;

}
