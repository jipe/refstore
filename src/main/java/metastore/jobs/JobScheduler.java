package metastore.jobs;

import java.util.List;

public interface JobScheduler {

    void add(JobDefinition jobDefinition) throws JobStoreException;
    
    void remove(JobDefinition jobDefinition);
    
    void add(Schedule schedule, Job... jobs);
    
    void remove(Job job);

    void reschedule(Job job, Schedule schedule);

    List<ScheduledJob> listScheduledJobs();
    
    List<Job> listRunningJobs();
    
    List<Job> listJobs();

    void start();
    
    void pause();
    
    void shutDown();
    
    JobDefinition createJobDefinition(String name, Class<? extends Job> type);
}
