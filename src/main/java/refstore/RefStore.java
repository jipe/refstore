package refstore;

import java.util.HashSet;
import java.util.Set;

import refstore.harvesting.Harvester;
import refstore.jobs.Job;
import refstore.jobs.JobScheduler;
import refstore.messaging.MessageQueue;
import refstore.services.ServiceLocator;

public class RefStore {

	private final ServiceLocator locator;

	private final Set<Class<? extends Harvester<?>>> harvesterTypes = new HashSet<>();
	private final Set<Class<? extends Job>> jobTypes = new HashSet<>();

	public RefStore(ServiceLocator locator) {
		this.locator = locator;
	}

	public JobScheduler getJobScheduler() {
		return locator.getJobScheduler();
	}

	public MessageQueue getMessageQueue() {
		return locator.getMessageQueue();
	}
	
	public void registerHarvesterType(Class<? extends Harvester<?>> harvesterType) {
		harvesterTypes.add(harvesterType);
	}

	public void registerJobType(Class<? extends Job> jobType) {
		jobTypes.add(jobType);
	}
}
