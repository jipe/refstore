package metastore.jobs;

import java.util.Date;

public interface JobListener {

    void started(Job job, Date timestamp);
    void terminated(Job job, Date timestamp);
}
