package ie.brianhenry.kyocerajobs;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Printer {

	String printerIp;
	String printerName;

	Printer(String printerIp) {
		this.printerIp = printerIp;
	}

	CloseableHttpClient client;
	private String printerModel;

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

	public CloseableHttpResponse login(String username, String password) throws ClientProtocolException, IOException {

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

		HttpGet httpGet = new HttpGet("https://" + printerIp
				+ "/job/JobSts_PrnJobLog_PrnJob_WklyDtl.htm?arg1=3&arg2=1&arg3=" + jobNumber + "&arg4=1");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");

		return JobDetail.fromHtml(bodyAsString);

	}

	public JobDetail[] getRecentJobs(int pageNumber) throws ClientProtocolException, IOException {

		JobDetail[] jobDetails = new JobDetail[10];

		HttpGet httpGet = new HttpGet("https://" + printerIp + "/job/JobSts_PrnJobLog_PrnJobs.htm?arg1=3&arg2=3&arg3="
				+ pageNumber + "&arg7=1&arg7=1");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");

		int jdIndex = 0;
		for (int j :

		parseJobsListHtml(bodyAsString)) {
			jobDetails[jdIndex] = getJob(j);
			jdIndex++;
		}

		return jobDetails;
	}

	int[] parseJobsListHtml(String jobListHtml) {

		int[] jobs = new int[10];

		String stringPattern = "JobKey\\[Index\\] = \"(\\d*)\";";

		Pattern pattern = Pattern.compile(stringPattern, Pattern.DOTALL);

		Matcher m = pattern.matcher(jobListHtml);

		int jobsIndex = 0;
		while (m.find()) {
			jobs[jobsIndex] = Integer.parseInt(m.group(1));
			jobsIndex++;
		}

		return jobs;

	}

	public String getPrinterName() throws ClientProtocolException, IOException {

		if (printerName == null)
			parseStartWlm();
		
		return printerName;
	}

	private void parseStartWlm() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet("https://" + printerIp + "/startwlm/Start_Wlm.htm");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");
		parseStartWlm(bodyAsString);
	}

	public void parseStartWlm(String html) {
		
		String stringPattern = "HeaderStatusPC\\(\"(?<printerModel>.*?)\",\"(?<printerName>.*?)\",";

		Pattern pattern = Pattern.compile(".*" + stringPattern + ".*", Pattern.DOTALL);

		Matcher m = pattern.matcher(html);

		if (m.matches()) {

			printerModel = m.group("printerModel");
			printerName = m.group("printerName");
			
		} else {
			// throw exception
			System.out.println("no match: parseStartWlm");

		}

	}

}
