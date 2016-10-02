package refstore;

import java.util.HashSet;
import java.util.Set;

import refstore.harvesting.Harvester;
import refstore.jobs.Job;
import refstore.jobs.JobScheduler;
import refstore.messaging.Messenger;
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

	public Messenger getMessenger() {
		return locator.getMessenger();
	}
	
	public void registerHarvesterType(Class<? extends Harvester<?>> harvesterType) {
		harvesterTypes.add(harvesterType);
	}

	public void registerJobType(Class<? extends Job> jobType) {
		jobTypes.add(jobType);
	}
}
