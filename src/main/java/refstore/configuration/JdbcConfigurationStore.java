package refstore.configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.database.migrations.MiniMigrations;

public class JdbcConfigurationStore implements ConfigurationStore {

	private static final Logger log = LoggerFactory.getLogger(JdbcConfigurationStore.class);
	
	private final DataSource ds;
	private final String tableName;
	
	public JdbcConfigurationStore(DataSource ds) {
		this(ds, "configuration");
	}
	
	public JdbcConfigurationStore(DataSource ds, String tableName) {
		this.ds = ds;
		this.tableName = tableName;
	}
	
	@Override
	public Configuration load(Properties defaults) throws ConfigurationException {
		try (Connection connection = ds.getConnection()) {
			Configuration config = new Configuration(this);
			config.put(defaults);
			
			Statement select = connection.createStatement();

			ResultSet rs = select.executeQuery(String.format("SELECT * FROM %s", tableName));
			while (rs.next()) {
				config.put(rs.getString("key"), rs.getString("value"));
			}
			
			rs.close();
			select.close();
			
			return config;
		} catch (SQLException e) {
			log.error("Error loading configuration: '{}'", e.getMessage());
			throw new ConfigurationException(e);
		}
	}
	
	@Override
	public void save(Configuration configuration) {
		try (Connection connection = ds.getConnection()) {
			String updateSql = String.format("UPDATE %s SET value = ? WHERE key = ?", tableName);
			String insertSql = String.format("INSERT INTO %s (key, value) VALUES (?, ?)", tableName);
			PreparedStatement update = connection.prepareStatement(updateSql);
			PreparedStatement insert = connection.prepareStatement(insertSql);
			
			for (Entry<String, String> entry : configuration.entrySet()) {
				update.setString(1, entry.getValue());
				update.setString(2, entry.getKey());
				if (update.executeUpdate() == 0) {
					insert.setString(1, entry.getKey());
					insert.setString(2, entry.getValue());
					if (insert.executeUpdate() == 0) {
						log.warn("Inserting config pair did not create any table rows: key = {}, value = {}", entry.getKey(), entry.getValue());
					}
				}
			}
			update.close();
			insert.close();
		} catch (SQLException e) {
			log.error("Error saving configuration to database: '{}'", e.getMessage());
		}
	}

	public void applyMigrations() throws IOException, SQLException {
		MiniMigrations.apply(ds, getClass().getResource("/sql/postgresql/configuration_store").getFile());
	}

}
