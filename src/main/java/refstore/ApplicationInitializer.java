package refstore;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.database.JndiDataSource;
import refstore.jobs.JdbcJobStore;
import refstore.jobs.JobScheduler;
import refstore.jobs.RefStoreJobScheduler;
import refstore.records.RecordStore;
import refstore.records.ShardedJdbcRecordStore;
import refstore.services.WiringBackedServiceLocator;
import refstore.wiring.Wiring;

public class ApplicationInitializer implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context");
		RefStore refStore = null;

		Wiring wiring = Wiring.getDefault();
		wiring.wire(JobScheduler.class, new RefStoreJobScheduler(createJobStore()));
		wiring.wire(RecordStore.class, createRecordStore());

		refStore = new RefStore(new WiringBackedServiceLocator(wiring));
		refStore.getJobScheduler().start();

		ServletContext context = sce.getServletContext();
		context.setAttribute("contextPath", context.getContextPath());
		context.setAttribute("refStore", refStore);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		RefStore refStore = (RefStore) sce.getServletContext().getAttribute("refStore");
		if (refStore != null) {
			refStore.getJobScheduler().shutDown();
		}
	}

	private ShardedJdbcRecordStore createRecordStore() {
		ShardedJdbcRecordStore recordStore = new ShardedJdbcRecordStore();

		int shardNumber = 1;
		JndiDataSource shard;
		while ((shard = JndiDataSource.find(String.format("jdbc/refstore-recordstore-shard%d", shardNumber))) != null) {
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
			jobStore = new JdbcJobStore(new JndiDataSource("jdbc/refstore-jobstore"));
			jobStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error getting and migrating jobstore datasource", e);
		}

		return jobStore;
	}
}
