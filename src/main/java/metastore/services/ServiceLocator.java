package metastore.services;

import metastore.indexing.IndexDocument;
import metastore.indexing.Indexer;
import metastore.jobs.JobScheduler;
import metastore.records.RecordStore;

public interface ServiceLocator {

	RecordStore getRecordStore();

	Indexer<? extends IndexDocument> getIndexer();

	JobScheduler getJobScheduler();
}