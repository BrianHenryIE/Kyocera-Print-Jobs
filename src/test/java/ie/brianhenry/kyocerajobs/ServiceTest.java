package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Before;
import org.junit.Test;

public class ServiceTest {

	Service service;

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		service = new Service();

	}

	@Test
	public void loginTest() throws ClientProtocolException, IOException {

		CloseableHttpResponse response = service.login();

		assertEquals(200, response.getStatusLine().getStatusCode());

	}

	@Test
	public void getJobTest() throws ClientProtocolException, IOException {

		JobDetail j = service.getJob(1635);

		assertEquals("doc00163520151014145654", j.getJobName());

	}

	// TODO finish, move to own class + use!
	// Based on https://gist.github.com/helmbold/c7808b17bcf5a5d009cf
	abstract class CarelessHttpClient extends CloseableHttpClient {

		public CarelessHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

			TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) {
					return true;
				}
			};

			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
					new NoopHostnameVerifier());
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslSocketFactory).build();

			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			HttpClients.custom().setSSLContext(sslContext).setConnectionManager(connectionManager).build();

		}

	}

}
