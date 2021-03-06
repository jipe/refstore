package refstore.indexing.solr;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import net.jcip.annotations.NotThreadSafe;
import refstore.indexing.Indexer;
import refstore.indexing.IndexerException;

@NotThreadSafe
public class SolrIndexer implements Indexer<SolrIndexDocument> {

	private final SolrClient solr;

	public SolrIndexer(SolrClient solr) {
		this.solr = solr;
	}

	@Override
	public void add(BlockingQueue<SolrIndexDocument> indexDocuments)
			throws IndexerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) throws IndexerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() throws IndexerException {
		try {
			solr.deleteByQuery("*:*");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
