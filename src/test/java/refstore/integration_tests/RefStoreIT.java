package refstore.integration_tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
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
	public static void startContainers() {
		try {
			runAndWaitForScript("./start_containers.sh");
		} catch (Exception e) {
			fail("Exception while starting containers");
		}
	}
	
	@AfterClass
	public static void stopContainers() {
		try {
			runAndWaitForScript("./stop_containers.sh");
		} catch (Exception e) {
			fail("Exception while stopping containers");
		}
	}

	private static void runAndWaitForScript(String script) throws IOException, InterruptedException {
		Process exec = Runtime.getRuntime().exec(script);
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
