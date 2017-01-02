package refstore.services;

import org.codehaus.jackson.JsonFactory;

import refstore.configuration.Configuration;
import refstore.configuration.ConfigurationStore;
import refstore.indexing.IndexDocument;
import refstore.indexing.Indexer;
import refstore.jobs.JobScheduler;
import refstore.messaging.Messenger;
import refstore.records.RecordStore;

public interface ServiceLocator {

	Configuration getConfiguration();
	
	ConfigurationStore getConfigurationStore();
	
	RecordStore getRecordStore();

	Indexer<? extends IndexDocument> getIndexer();

	JobScheduler getJobScheduler();
	
	Messenger getMessenger();
	
	JsonFactory getJsonFactory();
	
}