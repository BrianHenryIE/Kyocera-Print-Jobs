package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;

public class ServiceTest {

	@Test
	public void connectTest() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			ClientProtocolException, IOException {

		// https://87.35.237.21/startwlm/login.cgi

		// Form data
		// arg01_UserName:Admin
		// arg02_Password:Admin

		String printerIp = "87.35.237.21";
		String username = "Admin";
		String password = "Admin";

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
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory)
				.build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		CloseableHttpClient client = HttpClients.custom().setSSLContext(sslContext)
				.setConnectionManager(connectionManager).build();

		// HttpGet httpGet = new HttpGet("https://" + printerIp);
		//
		// CloseableHttpResponse response = client.execute(httpGet);
		// for (Header h : response.getAllHeaders())
		// System.out.println(h.getName() + " : " + h.getValue());
		//
		//

		HttpPost httpPost = new HttpPost("https://" + printerIp + "/startwlm/login.cgi");

		
		
		// Set headers

		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.8");
		httpPost.setHeader("Cache-Control", "max-age=0");
		httpPost.setHeader("Connection", "keep-alive");
		// httpPost.setHeader("Content-Length", "2583");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader("Cookie", "gsScrollPos=; rtl=0");
		httpPost.setHeader("DNT", "1");
		httpPost.setHeader("Host", "87.35.237.21");
		httpPost.setHeader("Origin", "https://87.35.237.21");
		httpPost.setHeader("Referer", "https://87.35.237.21/startwlm/Start_Wlm.htm"); // *
		httpPost.setHeader("Upgrade-Insecure-Requests", "1");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/46.0.2490.80 Safari/537.36");

		// Set form data
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("arg01_UserName", username));
		// params.add(new BasicNameValuePair("arg02_Password", password));

		params.add(new BasicNameValuePair("failhtmfile", "/startwlm/Start_Wlm.htm"));
		params.add(new BasicNameValuePair("okhtmfile", "/startwlm/Start_Wlm.htm"));
		params.add(new BasicNameValuePair("func", "authLogin"));
		params.add(new BasicNameValuePair("arg03_LoginType", "_mode_off"));
		params.add(new BasicNameValuePair("arg04_LoginFrom", "_wlm_login"));
		params.add(new BasicNameValuePair("language", "../wlmeng/index.htm"));
		params.add(new BasicNameValuePair("hiddRefreshDevice", "../startwlm/Hme_DvcSts.htm"));
		params.add(new BasicNameValuePair("hiddRefreshPanelUsed", "../startwlm/Hme_PnlUsg.htm"));
		params.add(new BasicNameValuePair("hiddRefreshPaperid", "../startwlm/Hme_Paper.htm"));
		params.add(new BasicNameValuePair("hiddRefreshTonerid", "../startwlm/Hme_StplPnch.htm"));
		params.add(new BasicNameValuePair("hiddRefreshStapleid", "../startwlm/Hme_Toner.htm"));
		params.add(new BasicNameValuePair("hiddnBackNavIndx", "1"));

		params.add(new BasicNameValuePair("hiddRefreshDevice", "../startwlm/Hme_DvcSts.htm"));
		params.add(new BasicNameValuePair("hiddRefreshPanelUsed", "../startwlm/Hme_PnlUsg.htm"));
		params.add(new BasicNameValuePair("hiddRefreshPaperid", "../startwlm/Hme_Paper.htm"));
		params.add(new BasicNameValuePair("hiddRefreshTonerid", "../startwlm/Hme_StplPnch.htm"));
		params.add(new BasicNameValuePair("hiddRefreshStapleid", "../startwlm/Hme_Toner.htm"));
		params.add(new BasicNameValuePair("hiddnBackNavIndx", "0"));

		params.add(new BasicNameValuePair("arg01_UserName", "Admin"));
		params.add(new BasicNameValuePair("arg02_Password", "Admin"));
		params.add(new BasicNameValuePair("Login", "Login"));
		params.add(new BasicNameValuePair("hndHeight", "0"));

		httpPost.setEntity(new UrlEncodedFormEntity(params));

		CloseableHttpResponse response = client.execute(httpPost);
		for (Header h : response.getAllHeaders())
			System.out.println(h.getName() + " : " + h.getValue());

		System.out.println("statusCode: " + response.getStatusLine().getStatusCode());

		System.out.println(response.getStatusLine().getProtocolVersion());
		System.out.println(response.getStatusLine().getReasonPhrase());
		System.out.println(response.toString());

		assertEquals(response.getStatusLine().getStatusCode(), 200);
		client.close();

	}

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
