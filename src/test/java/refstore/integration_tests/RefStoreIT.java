package refstore.integration_tests;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Suite.class)
@SuiteClasses(
		JobSchedulerIT.class
)
public class RefStoreIT {

	private static final Logger log = LoggerFactory.getLogger(RefStoreIT.class);

	@BeforeClass
	public static void startContainers() {
		try (CloseableHttpClient http = HttpClientBuilder.create().build()){
			runAndWaitForScript("./start_containers.sh");
			HttpGet request = new HttpGet("http://localhost:8080");
			boolean up = false;
			int retries = 10;
			while (!up && retries > 0) {
				try (CloseableHttpResponse response = http.execute(request)) {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						throw new IOException("Controller unavailable");
					}
					up = true;
				} catch (IOException e) {
					log.info("Controller unavailable. Retrying in a second (retries left: {})...", retries);
					retries--;
					Thread.sleep(1000);
				}
			}
			if (!up) {
				throw new Exception("Controller failed to come up within 10 seconds");
			}
		} catch (Exception e) {
			fail(String.format("Exception while starting containers: '%s'", e.getMessage()));
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
