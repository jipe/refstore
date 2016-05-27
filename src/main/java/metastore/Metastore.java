package metastore;

import java.util.HashSet;
import java.util.Set;

import metastore.harvesting.Harvester;
import metastore.jobs.Job;
import metastore.jobs.JobScheduler;
import metastore.services.ServiceLocator;

public class Metastore {

	private final ServiceLocator locator;

	private final Set<Class<? extends Harvester<?>>> harvesterTypes = new HashSet<>();
	private final Set<Class<? extends Job>> jobTypes = new HashSet<>();

	public Metastore(ServiceLocator locator) {
		this.locator = locator;
	}

	public JobScheduler getJobScheduler() {
		return locator.getJobScheduler();
	}

	public void registerHarvesterType(Class<? extends Harvester<?>> harvesterType) {
		harvesterTypes.add(harvesterType);
	}

	public void registerJobType(Class<? extends Job> jobType) {
		jobTypes.add(jobType);
	}
}
