package refstore.harvesting.datastore;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import refstore.configuration.ConfigurationException;
import refstore.harvesting.Harvester;
import refstore.harvesting.HarvesterException;

public class DatastoreHarvester implements Harvester<String> {

	@Override
	public void harvest(BlockingQueue<String> records)
			throws HarvesterException {
	}

	@Override
	public String getName() {
		return "Datastore harvester";
	}

	@Override
	public String getDescription() {
		return "RabbitMQ-based Datastore harvester";
	}

	@Override
	public void configure(Properties props) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

}
