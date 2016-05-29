package refstore.harvesting.oai;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import refstore.configuration.ConfigurationException;
import refstore.harvesting.Harvester;
import refstore.harvesting.HarvesterException;

public class OaiPmhHarvester implements Harvester<String> {

	@Override
	public void harvest(BlockingQueue<String> records)
			throws HarvesterException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "OAI-PMH harvester";
	}

	@Override
	public String getDescription() {
		return "OAI-PMH harvester";
	}

	@Override
	public void configure(Properties props) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

}
