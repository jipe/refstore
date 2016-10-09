package refstore.configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

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
	public void load(Configuration configuration) throws ConfigurationException {
		try (Connection connection = ds.getConnection()) {
			Statement select = connection.createStatement();

			ResultSet rs = select.executeQuery(String.format("SELECT * FROM %s", tableName));
			while (rs.next()) {
				configuration.put(rs.getString("key"), rs.getString("value"));
			}
			
			rs.close();
			select.close();
			
		} catch (SQLException e) {
			log.error("Error loading configuration: '{}'", e.getMessage());
			throw new ConfigurationException(e);
		}
	}
	
	@Override
	public void save(Configuration configuration) {
		try (Connection connection = ds.getConnection()) {
			connection.setAutoCommit(false);
			try {
				Statement stmt = connection.createStatement();
				stmt.execute(String.format("DELETE FROM %s", tableName));
				stmt.close();
				
				String insertSql = String.format("INSERT INTO %s (key, value) VALUES (?, ?)", tableName);
				PreparedStatement insert = connection.prepareStatement(insertSql);
				
				for (Entry<String, String> entry : configuration.entrySet()) {
					insert.setString(1, entry.getKey());
					insert.setString(2, entry.getValue());
					if (insert.executeUpdate() == 0) {
						log.warn("Inserting config pair did not create any table rows: key = '{}', value = '{}'", entry.getKey(), entry.getValue());
					}
				}
				insert.close();
				connection.commit();
			} catch (SQLException e) {
				log.error("Error saving configuration to database, rolling back changes: '{}'", e.getMessage());
				connection.rollback();
			}
		} catch (SQLException e) {
			log.error("Error saving configuration to database: '{}'", e.getMessage());
		}
	}

	public void applyMigrations() throws IOException, SQLException {
		MiniMigrations.apply(ds, getClass().getResource("/sql/postgresql/configuration_store").getFile());
	}

}
