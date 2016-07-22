package refstore.services;

import refstore.indexing.IndexDocument;
import refstore.indexing.Indexer;
import refstore.jobs.JobScheduler;
import refstore.messaging.MessageQueue;
import refstore.records.RecordStore;

public interface ServiceLocator {

	RecordStore getRecordStore();

	Indexer<? extends IndexDocument> getIndexer();

	JobScheduler getJobScheduler();
	
	MessageQueue getMessageQueue();
	
}