package metastore.records;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import metastore.database.migrations.MiniMigrations;

public class JdbcRecordStore implements RecordStore {

	private final DataSource ds;

	public JdbcRecordStore(DataSource ds) {
		this.ds = ds;
	}

	public void applyMigrations() throws IOException, SQLException {
		MiniMigrations.apply(ds, getClass().getResource("/sql/postgresql/record_store").getFile());
	}

	@Override
	public Record getRecord(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Record> getRecords(long groupId) throws RecordStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Collection<Record> records, long groupId) throws RecordStoreException {
		// TODO Auto-generated method stub

	}

}
