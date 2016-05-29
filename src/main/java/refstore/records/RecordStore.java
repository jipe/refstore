package refstore.records;

import java.util.Collection;

public interface RecordStore {

	Record getRecord(String id);

	Collection<Record> getRecords(long groupId) throws RecordStoreException;

	void save(Collection<Record> records, long groupId) throws RecordStoreException;

}
