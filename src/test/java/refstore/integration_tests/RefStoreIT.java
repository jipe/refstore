package refstore.integration_tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
	public static void startContainers() throws Exception {
		Process exec = Runtime.getRuntime().exec("./start_containers.sh");
		BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		exec.waitFor();
		if (exec.exitValue() != 0) {
			fail(String.format("Error starting containers: return code is %d", exec.exitValue()));
		}
	}
	
	@AfterClass
	public static void stopContainers() throws Exception {
		Process exec = Runtime.getRuntime().exec("./stop_containers.sh");
		BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		exec.waitFor();
		if (exec.exitValue() != 0) {
			fail(String.format("Error stopping containers: return code is %d", exec.exitValue()));
		}
	}

}
