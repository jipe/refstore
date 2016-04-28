package metastore.records;

import java.util.Date;

public class RecordEvent {

    private final Record record;
    private final Type type;
    private final Date timestamp;

    public enum Type {
        DELETED, UPDATED
    }
    
    public RecordEvent(Record record, Type type, Date timestamp) {
        this.record    = record;
        this.type      = type;
        this.timestamp = timestamp;
    }
    
	public Record getRecord() {
		return record;
	}
	
	public boolean isRecordDeleted() {
		return Type.DELETED.equals(type);
	}
	
	public boolean isRecordUpdated() {
		return Type.UPDATED.equals(type);
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
}
