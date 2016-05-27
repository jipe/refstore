package metastore.indexing;

public class IndexField<T> {

	private final String name;
	private final T value;

	public IndexField(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}
}
