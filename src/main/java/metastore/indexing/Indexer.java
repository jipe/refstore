package metastore.indexing;

import java.util.concurrent.BlockingQueue;

public interface Indexer<T extends IndexDocument> {

	void add(BlockingQueue<T> indexDocuments) throws IndexerException;

	void delete(String id) throws IndexerException;

	void clear() throws IndexerException;

}
