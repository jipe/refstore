package metastore.indexing.solr;

import org.apache.solr.common.SolrInputDocument;

import metastore.indexing.IndexDocument;
import metastore.indexing.IndexField;

public class SolrIndexDocument implements IndexDocument {

	private SolrInputDocument document;
	
	public SolrInputDocument getDocument() {
		return document;
	}
	
	@Override
	public <T> void add(IndexField<T> field) {
	    document.addField(field.getName(), field.getValue());
	}
}
