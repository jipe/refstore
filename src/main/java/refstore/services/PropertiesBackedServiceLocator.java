package refstore.services;

import java.util.Properties;

import javax.naming.NamingException;

import refstore.database.JndiDataSource;
import refstore.indexing.IndexDocument;
import refstore.indexing.Indexer;
import refstore.jobs.JdbcJobStore;
import refstore.jobs.JobScheduler;
import refstore.jobs.JobStore;
import refstore.jobs.JobStoreException;
import refstore.jobs.RefStoreJobScheduler;
import refstore.messaging.Messenger;
import refstore.records.RecordStore;

public class PropertiesBackedServiceLocator implements ServiceLocator {

	private final JobScheduler jobScheduler;

	public PropertiesBackedServiceLocator(Properties props) throws JobStoreException {
		this.jobScheduler = setupJobScheduler(props);
	}

	private JobStore setupJobStore(Properties props) throws JobStoreException {
		try {
			return new JdbcJobStore(new JndiDataSource("jdbc/metastore-jobstore"));
		} catch (NamingException e) {
			throw new JobStoreException(e);
		}
	}

	private JobScheduler setupJobScheduler(Properties props) throws JobStoreException {
		return new RefStoreJobScheduler(setupJobStore(props));
	}

	@Override
	public RecordStore getRecordStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Indexer<? extends IndexDocument> getIndexer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobScheduler getJobScheduler() {
		return jobScheduler;
	}

	@Override
	public Messenger getMessenger() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
