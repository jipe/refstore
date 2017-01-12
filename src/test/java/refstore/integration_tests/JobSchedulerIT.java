package refstore.integration_tests;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JobSchedulerIT {

	CloseableHttpClient http;
	
	@Before
	public void setUpHttpClient() {
		http = HttpClientBuilder.create().build();
	}
	
	@After
	public void tearDownHttpClient() throws Exception {
		http.close();
	}
	
	@Test
	public void test() throws Exception {
//		HttpGet request = new HttpGet("http://localhost:8080");
//		CloseableHttpResponse response = http.execute(request);
//		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
//		response.close();
	}
	
}
