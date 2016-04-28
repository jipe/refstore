package metastore.configuration;

import java.util.Properties;

public interface Configurable {

    void configure(Properties props) throws ConfigurationException;
    
}
