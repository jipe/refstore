package refstore.jobs.schedules;

import java.util.Set;

import refstore.jobs.Job;
import refstore.jobs.Schedule;

public class AwaitJobStatusSchedule extends Schedule {

	private Set<Job.Status> triggeringStatuses;

	/**
	 * Create new schedule that will trigger when a given job's
	 * status is one of the given triggering statuses
	 * 
	 * @param watchedJob The job to watch for termination
	 * @param triggeringStatuses
	 */
	public AwaitJobStatusSchedule(Job watchedJob, Set<Job.Status> triggeringStatuses) {
	}

}
