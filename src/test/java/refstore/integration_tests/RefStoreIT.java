package refstore.integration_tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
		JobSchedulerIT.class
)
public class RefStoreIT {

	@BeforeClass
	public static void setupIT() {
		// TODO: Start docker containers
	}
	
	@AfterClass
	public static void tearDownIT() {
		// TODO: Stop docker containers
	}

}
