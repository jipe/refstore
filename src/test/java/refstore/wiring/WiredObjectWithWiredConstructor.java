package refstore.wiring;

public class WiredObjectWithWiredConstructor {

	WiredObject wiredObject;
	
	public WiredObjectWithWiredConstructor() {}
	
	public WiredObjectWithWiredConstructor(@Wired WiredObject wiredObject) {
		this.wiredObject = wiredObject;
	}
	
	public WiredObject getWiredObject() {
		return wiredObject;
	}
}
