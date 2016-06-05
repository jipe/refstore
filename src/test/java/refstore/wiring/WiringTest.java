package refstore.wiring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class WiringTest {

	public static class get {
		@Test(expected = NullPointerException.class)
		public void failsOnNullId() {
			Wiring.get(null);
		}
		
		@Test
		public void returnsRegistryOfCorrectClass() {
			assertTrue(Wiring.get("myId") instanceof Wiring);
		}
		
		@Test
		public void returnsRegistryWithCorrectId() {
			Object id = new Object();
			assertEquals(id, Wiring.get(id).getId());
		}
		
		@Test
		public void reusesExistingRegistry() {
			Object id       = new Object();
			Wiring expected = Wiring.get(id);
			Wiring actual   = Wiring.get(id);
			assertEquals(expected, actual);
		}
	}
	
	public static class getDefault {
		@Test
		public void returnsRegistryWithNonNullId() {
			assertNotNull(Wiring.getDefault());
		}
		
		@Test
		public void reusesExistingRepository() {
			Wiring expected = Wiring.getDefault();
			Wiring actual   = Wiring.getDefault();
			assertEquals(expected, actual);
		}
	}
	
 
}
