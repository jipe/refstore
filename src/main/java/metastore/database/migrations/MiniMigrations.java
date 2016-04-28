package metastore.database.migrations;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiniMigrations {

	private static final Logger log = LoggerFactory.getLogger(MiniMigrations.class);
	
	public static void apply(DataSource ds, String... directoryNames) throws IOException, SQLException {
	    File[] files = new File[directoryNames.length];
	    for (int i = 0; i < directoryNames.length; i++) {
	        files[i] = new File(directoryNames[i]);
	    }
	    apply(ds, files);
	}
	
	public static void apply(DataSource ds, File... directories) throws IOException, SQLException {
		try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
			try {
			    log.info("Applying migrations");
	            apply(connection, directories);
			    connection.commit();
			} catch (SQLException e) {
			    log.error("Error applying migrations", e);
			    connection.rollback();
			}
		}
	}
	
	private static void apply(Connection connection, File... directories) throws IOException, SQLException {
        Statement migration = connection.createStatement();
        try {
        	migration.execute("select 1 from schema_migrations");
        } catch (SQLException e) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        	migration.execute("create table schema_migrations (name character varying(255) unique not null)");
        }

        PreparedStatement addMigration = connection.prepareStatement("insert into schema_migrations (name) values (?)");
        PreparedStatement checkForMigration = connection.prepareStatement("select 1 from schema_migrations where name = ?");
        
        for (File directory : directories) {
        	if (directory.isDirectory() && directory.canRead()) {
        		List<String> fileNames = Arrays.asList(directory.list());
        		Collections.sort(fileNames);
        		for (String fileName : fileNames) {
        			File file = new File(directory, fileName);
        			if (file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith(".sql")) {
        				checkForMigration.setString(1, fileName);
        				ResultSet rs = checkForMigration.executeQuery();
        				if (!rs.next()) {
        					log.info("Applying migration: " + fileName);
        					String sql = FileUtils.readFileToString(file);
        					log.debug("Migration SQL:\n{}", sql);
        					migration.execute(sql);
        					addMigration.setString(1, fileName);
        					addMigration.execute();
        				}
        				rs.close();
        			} else {
        			    log.info("Unable to read file '{}', or it was deemed irrelevant", file.getAbsolutePath());
        			}
        		}
        	} else {
        	    log.info("Unable to read directory '{}'", directory.getAbsolutePath());
        	}
        }
	}
}
