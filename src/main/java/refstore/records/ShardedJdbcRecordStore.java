package refstore.records;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

public class ShardedJdbcRecordStore implements RecordStore {

	private final List<JdbcRecordStore> recordStores = new ArrayList<>();

	public void addShard(DataSource dataSource) {
		this.recordStores.add(new JdbcRecordStore(dataSource));
	}

	public int countShards() {
		return recordStores.size();
	}

	public void applyMigrations() throws IOException, SQLException {
		applyMigrations(false);
	}
	
	public void applyMigrations(boolean async) throws IOException, SQLException {
		if (async) {
			// TODO: Implement asynchronous migrations
		} else {
			for (JdbcRecordStore recordStore : recordStores) {
				recordStore.applyMigrations();
			}
		}
	}

	@Override
	public Record getRecord(String id) {
		return getRecord(id, false);
	}
	
	public Record getRecord(String id, boolean async) {
		if (async) {
			// TODO: Implement asynchronous record retrieval
			return null;
		} else {
			Record result = null;
			for (RecordStore recordStore : recordStores) {
				result = recordStore.getRecord(id);
			}
			return result;
		}
	}

	@Override
	public Collection<Record> getRecords(long groupId) throws RecordStoreException {
		return getShard(String.valueOf(groupId)).getRecords(groupId);
	}

	@Override
	public void save(Collection<Record> records, long groupId) throws RecordStoreException {
		for (Record record : records) {
		}
	}

	private JdbcRecordStore getShard(String value) {
		return recordStores.get(value.hashCode() % recordStores.size());
	}
}
