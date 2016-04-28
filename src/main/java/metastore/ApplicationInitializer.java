package metastore;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import metastore.database.JndiDataSource;
import metastore.jobs.JdbcJobStore;
import metastore.jobs.JobScheduler;
import metastore.jobs.MetastoreJobScheduler;
import metastore.records.RecordStore;
import metastore.records.ShardedJdbcRecordStore;
import metastore.services.WiringBackedServiceLocator;
import metastore.wiring.Wiring;

public class ApplicationInitializer implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context");
		Metastore metastore = null;

		Wiring wiring = Wiring.getDefault();
		wiring.wire(JobScheduler.class, new MetastoreJobScheduler(createJobStore()));
		wiring.wire(RecordStore.class, createRecordStore());

		metastore = new Metastore(new WiringBackedServiceLocator(wiring));
		metastore.getJobScheduler().start();

		ServletContext context = sce.getServletContext();
		context.setAttribute("contextPath", context.getContextPath());
		context.setAttribute("metastore", metastore);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Metastore metastore = (Metastore) sce.getServletContext().getAttribute("metastore");
		if (metastore != null) {
			metastore.getJobScheduler().shutDown();
		}
	}

	private ShardedJdbcRecordStore createRecordStore() {
		ShardedJdbcRecordStore recordStore = new ShardedJdbcRecordStore();
		
		int shardNumber = 1;
		JndiDataSource shard;
		while ((shard = JndiDataSource.find(String.format("jdbc/metastore-recordstore-shard%d", shardNumber))) != null) {
			recordStore.addShard(shard);
			shardNumber++;
		}
		
		try {
			recordStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error migrating recordstore datasources", e);
		}
		
		return recordStore;
	}

	private JdbcJobStore createJobStore() {
		JdbcJobStore jobStore = null;
		
		try {
			jobStore = new JdbcJobStore(new JndiDataSource("jdbc/metastore-jobstore"));
			jobStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error getting and migrating jobstore datasource", e);
		}
		
		return jobStore;
	}
}
