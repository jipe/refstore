package refstore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConnectionFactory;

import refstore.configuration.Configuration;
import refstore.configuration.ConfigurationException;
import refstore.configuration.ConfigurationStore;
import refstore.configuration.JdbcConfigurationStore;
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

public class ApplicationContextListener implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context");
		RefStore refStore = null;

		ConfigurationStore configurationStore = createConfigurationStore();
		
		Wiring wiring = Wiring.getDefault();
		wiring.wire(ConfigurationStore.class, configurationStore);
		wiring.wire(Configuration.class, loadConfiguration(configurationStore));
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
			log.info("Shutting down job scheduler");
			refStore.getJobScheduler().shutDown();
			try {
				log.info("Closing messenger");
				refStore.getMessenger().close();
			} catch (IOException e) {
				log.warn("Error while closing messenger", e);
			}
		}
	}

	private ShardedJdbcRecordStore createRecordStore() {
		ShardedJdbcRecordStore recordStore = new ShardedJdbcRecordStore();

		int shardNumber = 1;
		JndiDataSource shard;
		while ((shard = JndiDataSource.find(String.format("jdbc/refstore.recordStore.shard%d", shardNumber))) != null) {
			recordStore.addShard(shard);
			shardNumber++;
		}

		try {
			recordStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error migrating record store datasources", e);
		}

		return recordStore;
	}

	private JdbcJobStore createJobStore() {
		JdbcJobStore jobStore = null;

		try {
			jobStore = new JdbcJobStore(new JndiDataSource("jdbc/refstore.jobStore"));
			jobStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error getting and migrating job store datasource", e);
		}

		return jobStore;
	}

	private JdbcConfigurationStore createConfigurationStore() {
		JdbcConfigurationStore configurationStore = null;

		try {
			configurationStore = new JdbcConfigurationStore(new JndiDataSource("jdbc/refstore.configurationStore"));
			configurationStore.applyMigrations();
		} catch (Exception e) {
			throw new RuntimeException("Error getting and migrating configuration store datasource", e);
		}

		return configurationStore;
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

	private Configuration loadConfiguration(ConfigurationStore store) {
		Properties defaults = new Properties();
		try (InputStream configDefaults = getClass().getResourceAsStream("/configuration.properties")) {
			defaults.load(configDefaults);
			Configuration configuration = new Configuration(defaults);
			store.load(configuration);
			return configuration;
		} catch (IOException e) {
			log.error("Error loading default configuration: '{}'", e.getMessage());
			throw new ConfigurationException();
		}
	}
}
