package metastore.wiring;

public interface WiringFactory<T> {

	T produce();
	
}
