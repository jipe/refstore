package refstore.configuration;

public interface ConfigurationStore {

	void load(Configuration configuration);
	
	void save(Configuration configuration);
	
}
