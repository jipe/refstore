package refstore.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesBackedConfigurationStore implements ConfigurationStore {

	private static final Logger log = LoggerFactory.getLogger(PropertiesBackedConfigurationStore.class);
	
	private final File file;
	
	public PropertiesBackedConfigurationStore(File file) {
		this.file = file;
	}
	
	@Override
	public void load(Configuration configuration) {
		try {
			Properties props = new Properties();
			if (file.canRead() && file.isFile()) {
				props.load(new FileReader(file));
			}
			for (Entry<Object, Object> entry : props.entrySet()) {
				configuration.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} catch (IOException e) {
			log.error("Error loading configuration from '{}': '{}'", file.getName(), e.getMessage());
			throw new ConfigurationException(e);
		}
	}

	@Override
	public void save(Configuration configuration) {
		Properties props = new Properties();
		for (Entry<String, String> entry : configuration.entrySet()) {
			props.put(entry.getKey(), entry.getValue());
		}
		try (FileWriter writer = new FileWriter(file)) {
			props.store(writer, "RefStore configuration file");
		} catch (IOException e) {
			log.error("Error saving configuration to '{}': '{}'", file.getName(), e.getMessage());
		}
	}

}
