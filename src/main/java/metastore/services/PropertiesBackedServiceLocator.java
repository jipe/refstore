package metastore.services;

import java.util.Properties;

import javax.naming.NamingException;

import metastore.database.JndiDataSource;
import metastore.indexing.IndexDocument;
import metastore.indexing.Indexer;
import metastore.jobs.JdbcJobStore;
import metastore.jobs.JobScheduler;
import metastore.jobs.JobStore;
import metastore.jobs.JobStoreException;
import metastore.jobs.MetastoreJobScheduler;
import metastore.records.RecordStore;

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
        return new MetastoreJobScheduler(setupJobStore(props));
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
    
}
