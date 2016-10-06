package refstore.configuration;

import java.util.Properties;

public interface ConfigurationStore {

	Configuration load(Properties defaults);
	
	void save(Configuration configuration);
	
}
