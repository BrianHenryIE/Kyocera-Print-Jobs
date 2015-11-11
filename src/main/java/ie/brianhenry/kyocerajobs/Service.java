package ie.brianhenry.kyocerajobs;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

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
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class Service {

	String printerIp = "87.35.237.21";
	String username = "Admin";
	String password = "Admin";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	CloseableHttpClient client;

	{
		// Set up HttpClient to ignore SSL warnings
		TrustStrategy trustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) {
				return true;
			}
		};

		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
				new NoopHostnameVerifier());
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory)
				.build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		client = HttpClients.custom().setSSLContext(sslContext).setConnectionManager(connectionManager).build();
	}

	public CloseableHttpResponse login() throws ClientProtocolException, IOException {

		// Login!
		HttpPost httpPost = new HttpPost("https://" + printerIp + "/startwlm/login.cgi");

		// Set headers
		httpPost.setHeader("Referer", "https://" + printerIp + "/startwlm/Start_Wlm.htm"); // *

		// Set form data
		// It didn't work without the empty ones
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("okhtmfile", "/startwlm/Start_Wlm.htm"));
		params.add(new BasicNameValuePair("func", "authLogin"));
		params.add(new BasicNameValuePair("arg03_LoginType", "_mode_off"));
		params.add(new BasicNameValuePair("arg04_LoginFrom", "_wlm_login"));
		params.add(new BasicNameValuePair("arg05_AccountId", ""));
		params.add(new BasicNameValuePair("arg06_DomainName", ""));
		params.add(new BasicNameValuePair("arg01_UserName", username));
		params.add(new BasicNameValuePair("arg02_Password", password));
		params.add(new BasicNameValuePair("Login", "Login"));

		httpPost.setEntity(new UrlEncodedFormEntity(params));

		return client.execute(httpPost);

	}

	public JobDetail getJob(int jobNumber) throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet(
				"https://"+printerIp+"/job/JobSts_PrnJobLog_PrnJob_WklyDtl.htm?arg1=3&arg2=1&arg3="+jobNumber+"&arg4=1");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");

		return JobDetail.fromHtml(bodyAsString);

	}

}
