package refstore.configuration;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private final Map<String, String> map = new HashMap<>();
	private final ConfigurationStore store;
	
	public Configuration(ConfigurationStore store) {
		this.store = store;
	}
	
	public Set<Entry<String, String>> entrySet() {
		return map.entrySet();
	}
	
	public String get(String key) {
		return map.get(key);
	}
	
	public void put(String key, String value) {
		log.info("Updating config with '{}': '{}'", key, value);
		map.put(key, value);
	}
	
	public void put(Configuration config) {
		for (Entry<String, String> entry : config.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public void put(Properties props) {
		Enumeration<Object> keys = props.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			String value = props.getProperty(key).toString();
			put(key, value);
		}
	}

	public void load(Properties defaults) {
		put(store.load(defaults));
	}
	
	public void save() {
		store.save(this);
	}
}
