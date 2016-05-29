package refstore.indexing;

public interface IndexDocument {

	<T> void add(IndexField<T> field);

}
