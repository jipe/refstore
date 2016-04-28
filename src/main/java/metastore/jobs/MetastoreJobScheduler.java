package metastore.jobs;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetastoreJobScheduler implements JobScheduler {

    private class JobRunner implements Runnable {

        private AtomicBoolean pause = new AtomicBoolean(false);
        private AtomicBoolean stop  = new AtomicBoolean(false);
        
        @Override
        public void run() {
            log.info("Job runner run method entry");
            while (!stop.get()) {
                if (pause.get()) {
                    
                } else {
                    
                }
            }
            log.info("Job runner run method exit");
        }
        
        public void pause() {
            this.pause.set(true);
        }
        
        public void resume() {
            this.pause.set(false);
        }
        
        public void stop() {
            this.stop.set(true);
        }
        
    }
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JobStore jobStore;
    private final JobRunner jobRunner = new JobRunner();
    private final Thread jobRunnerThread = new Thread(jobRunner, "Job runner");
    
    public MetastoreJobScheduler(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    @Override
    public void add(JobDefinition jobDefinition) throws JobStoreException {
        jobStore.saveJobDefinition(jobDefinition);
    }
    
    @Override
    public void remove(JobDefinition jobDefinition) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void add(Schedule schedule, Job... jobs) {
        /* TODO 
         * Add first job with given schedule and rest of jobs with 
         * await termination (success) schedule of previous job
         */
    }

    @Override
    public void remove(Job job) {
    }

    @Override
    public void reschedule(Job job, Schedule schedule) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Job> listRunningJobs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Job> listJobs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void start() {
        if (jobRunnerThread.isAlive()) {
            jobRunner.resume();
        } else {
            log.info("Starting job runner thread");
            jobRunnerThread.start();
        }
    }

    @Override
    public void pause() {
        if (jobRunnerThread.isAlive()) {
            log.info("Pausing job runner thread");
            jobRunner.pause();
        }
    }

    @Override
    public void shutDown() {
        log.info("Stopping job runner thread");
        jobRunner.stop();
        try {
            log.info("Waiting for job runner thread to exit");
            jobRunnerThread.join();
            log.info("Job runner thread exited");
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for job runner exit", e);
        }
    }

    @Override
    public List<ScheduledJob> listScheduledJobs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobDefinition createJobDefinition(String name, Class<? extends Job> type) {
        return new JobDefinition(name, type, jobStore);
    }
}
