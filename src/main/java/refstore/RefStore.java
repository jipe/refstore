package refstore;

import java.util.HashSet;
import java.util.Set;

import refstore.configuration.Configuration;
import refstore.configuration.ConfigurationStore;
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

	public ConfigurationStore getConfigurationStore() {
		return locator.getConfigurationStore();
	}
	
	public Configuration getConfiguration() {
		return locator.getConfiguration();
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
