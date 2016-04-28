package metastore.jobs;

public class ScheduledJob {

    private Schedule schedule;
    private Job job;
    
    public ScheduledJob(Schedule schedule, Job job) {
        this.schedule = schedule;
        this.job      = job;
    }
    
    public Job getJob() {
        return job;
    }
    
    public Schedule getSchedule() {
        return schedule;
    }
}
