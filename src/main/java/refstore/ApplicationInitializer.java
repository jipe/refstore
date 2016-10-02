package refstore;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConnectionFactory;

import refstore.database.JndiDataSource;
import refstore.jobs.JdbcJobStore;
import refstore.jobs.JobScheduler;
import refstore.jobs.RefStoreJobScheduler;
import refstore.messaging.Messenger;
import refstore.messaging.RabbitMqBasedMessenger;
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
		wiring.wire(Messenger.class, createMessenger(sce.getServletContext().getInitParameter("RABBITMQ_URL")));

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
			try {
				refStore.getMessenger().close();
			} catch (IOException e) {
				log.warn("Error while closing message queue", e);
			}
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
	
	private Messenger createMessenger(String rabbitMqUri) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri(rabbitMqUri);
			return new RabbitMqBasedMessenger(factory);
		} catch (Exception e) {
			throw new RuntimeException("Error creating message queue", e);
		}
	}

}
