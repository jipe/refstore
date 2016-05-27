package metastore.harvesting;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import metastore.configuration.ConfigurationException;

public interface Harvester<T> {

	/**
	 * Fetch new records from a source
	 * 
	 * @param queue a queue for enqueuing harvested items
	 *  
	 * @throws HarvesterException
	 */
	void harvest(BlockingQueue<T> queue) throws HarvesterException;

	String getName();

	String getDescription();

	void configure(Properties props) throws ConfigurationException;

}
