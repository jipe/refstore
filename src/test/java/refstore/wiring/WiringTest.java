package refstore.wiring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
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
	
	public static class getId {
		@Test
		public void returnsCorrectId() {
			Object id = new Object();
			Wiring wiring = Wiring.get(id);
			assertEquals(id, wiring.getId());
		}
	}
	
	public static class getWiring {
		@Before
		public void clearDefaultWiring() {
			Wiring.getDefault().clear();
		}
		
		@Test(expected = WiringException.class)
		public void throwsIfNotWired() {
			Wiring.getDefault().getWiring(WiredObject.class);
		}

		@Test
		public void returnsInstanceIfWiredAndRequestedWithSameId() {
			Wiring wiring = Wiring.getDefault();
			wiring.wire(WiredObject.class, new WiredObject());
			assertTrue(wiring.getWiring(WiredObject.class) instanceof WiredObject);
		}

		@Test(expected = WiringException.class)
		public void throwsIfWiredAndRequestedWithDifferentIds() {
			Wiring wiring = Wiring.getDefault();
			Object id1 = new Object();
			Object id2 = new Object();
			wiring.wire(WiredObject.class, id1, new WiredObject());
			wiring.getWiring(WiredObject.class, id2);
		}
		
		@Test
		public void returnsNewObjectWhenWiredByClass() {
			Wiring wiring = Wiring.getDefault();
			wiring.wire(WiredObject.class, WiredObject.class);
			assertNotEquals(wiring.getWiring(WiredObject.class), wiring.getWiring(WiredObject.class));
		}
		
		@Test
		public void returnsSameObjectWhenWiredByObject() {
			Wiring wiring = Wiring.getDefault();
			wiring.wire(WiredObject.class, new WiredObject());
			assertEquals(wiring.getWiring(WiredObject.class), wiring.getWiring(WiredObject.class));
		}
		
		@Test
		public void returnsFactoryBuiltObjectWhenWiredByFactory() {
			Wiring wiring = Wiring.getDefault();
			final WiredObject obj = new WiredObject();
			wiring.wire(WiredObject.class, new WiringFactory<WiredObject>() {
				@Override
				public WiredObject produce() {
					return obj;
				}
			});
			assertEquals(obj, wiring.getWiring(WiredObject.class));
		}
		
		@Test
		public void prefersWiredConstructorOverNoArgsConstructor() {
			Wiring wiring = Wiring.getDefault();
			WiredObject obj = new WiredObject();
			wiring.wire(WiredObject.class, obj);
			wiring.wire(WiredObjectWithWiredConstructor.class, WiredObjectWithWiredConstructor.class);
			assertEquals(obj, wiring.getWiring(WiredObjectWithWiredConstructor.class).getWiredObject());
		}
		
		@Test
		public void wiredConstructorArgsCanUseIds() {
			Wiring wiring = Wiring.getDefault();
			WiredObject obj = new WiredObject();
			wiring.wire(WiredObject.class, "id", obj);
			wiring.wire(WiredObjectWithIdWiredConstructorArgs.class, WiredObjectWithIdWiredConstructorArgs.class);
			assertEquals(obj, wiring.getWiring(WiredObjectWithIdWiredConstructorArgs.class).getObj());
		}
	}
}
