package refstore.services;

import net.jcip.annotations.ThreadSafe;
import refstore.configuration.Configuration;
import refstore.configuration.ConfigurationStore;
import refstore.indexing.IndexDocument;
import refstore.indexing.Indexer;
import refstore.jobs.JobScheduler;
import refstore.messaging.Messenger;
import refstore.records.RecordStore;
import refstore.wiring.Wiring;

@ThreadSafe
public class WiringBackedServiceLocator implements ServiceLocator {

	private final Wiring wiring;
	private final Class<?>[] requiredWirings = { RecordStore.class, JobScheduler.class };

	public WiringBackedServiceLocator(Wiring wiring) {
		this.wiring = wiring;
		for (Class<?> requiredWiring : requiredWirings) {
			if (wiring.getWiring(requiredWiring) == null) {
				throw new RuntimeException(String.format("No wiring found for %s", requiredWiring));
			}
		}
	}

	@Override
	public ConfigurationStore getConfigurationStore() {
		return wiring.getWiring(ConfigurationStore.class);
	}
	
	@Override
	public Configuration getConfiguration() {
		return wiring.getWiring(Configuration.class);
	}
	
	@Override
	public RecordStore getRecordStore() {
		return get(RecordStore.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Indexer<? extends IndexDocument> getIndexer() {
		return wiring.getWiring(Indexer.class);
	}

	@Override
	public JobScheduler getJobScheduler() {
		return wiring.getWiring(JobScheduler.class);
	}
	
	@Override
	public Messenger getMessenger() {
		return wiring.getWiring(Messenger.class);
	}

	protected <T> T get(Class<T> type) {
		return wiring.getWiring(type);
	}

	protected <T> T get(Class<T> type, Object id) {
		return wiring.getWiring(type, id);
	}
}
