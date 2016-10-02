package refstore.services;

import refstore.indexing.IndexDocument;
import refstore.indexing.Indexer;
import refstore.jobs.JobScheduler;
import refstore.messaging.Messenger;
import refstore.records.RecordStore;

public interface ServiceLocator {

	RecordStore getRecordStore();

	Indexer<? extends IndexDocument> getIndexer();

	JobScheduler getJobScheduler();
	
	Messenger getMessenger();
	
}