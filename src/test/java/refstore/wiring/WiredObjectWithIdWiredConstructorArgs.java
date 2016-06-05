package refstore.wiring;

public class WiredObjectWithIdWiredConstructorArgs {

	private WiredObject obj;
	
	public WiredObjectWithIdWiredConstructorArgs(@Wired("id") WiredObject obj) {
		this.obj = obj;
	}
	
	public WiredObject getObj() {
		return obj;
	}
}
