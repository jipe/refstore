package refstore.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private Properties defaults;
	private Properties props;

	public Configuration() {
		this(new Properties());
	}
	
	public Configuration(Properties defaults) {
		this.defaults = defaults;
		this.props = new Properties(defaults);
	}
	
	public Configuration(Configuration config, Properties defaults) {
		this(defaults);
		this.put(config);
	}
	
	public Set<Entry<String, String>> entrySet() {
		Map<String, String> map = new HashMap<>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return map.entrySet();
	}
	
	public String get(String key) {
		return (String) props.getProperty(key);
	}

	public Set<String> keySet() {
		Set<String> result = new HashSet<>();
		for (Object o : defaults.keySet()) {
			result.add((String) o);
		}
		return result;
	}
	
	public List<String> orderedKeys() {
		List<String> result = new ArrayList<>();
		result.addAll(keySet());
		Collections.sort(result);
		return result;
	}
	
	public Configuration put(String key, String value) {
		if (!defaults.containsKey(key)) throw new ConfigurationException("Illegal key name: no default available");
		
		log.info("Updating config with '{}': '{}'", key, value);
		props.put(key, value);
		return this;
	}
	
	public void setToDefault(String key) {
		props.remove(key);
	}
	
	public Configuration put(Configuration config) {
		for (Entry<String, String> entry : config.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public Configuration put(Properties props) {
		Enumeration<Object> keys = props.keys();
		while (keys.hasMoreElements()) {
			String key   = keys.nextElement().toString();
			String value = props.getProperty(key).toString();
			put(key, value);
		}
		return this;
	}

}
