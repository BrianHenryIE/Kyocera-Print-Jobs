package ie.brianhenry.kyocerajobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class Printer {

	String printerIp;
	String printerName;

	Printer(String printerIp) {
		this.printerIp = printerIp;
	}

	CloseableHttpClient client = CarelessHttpClient.client();
	
	private String printerModel;
	
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

	public JobDetail getJob(int jobNumber) throws ParseJobException, ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet("https://" + printerIp
				+ "/job/JobSts_PrnJobLog_PrnJob_WklyDtl.htm?arg1=3&arg2=1&arg3=" + jobNumber + "&arg4=1");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");

		return JobDetail.fromHtml(bodyAsString);

	}

	/**
	 * Returns the job details for a page of ten jobs.
	 * Index starts at 0
	 * There are only 10? pages. For more jobs, use getRecentJobs()
	 * 
	 * @param pageNumber
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseJobException
	 */
	public List<JobDetail> getRecentJobs(int pageNumber) throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> jobDetails = new ArrayList<JobDetail>();

		HttpGet httpGet = new HttpGet("https://" + printerIp + "/job/JobSts_PrnJobLog_PrnJobs.htm?arg1=3&arg2=3&arg3="
				+ pageNumber + "&arg7=1&arg7=1");

		CloseableHttpResponse response = client.execute(httpGet);

		String bodyAsString = EntityUtils.toString(response.getEntity(), "UTF-8");

		for (int j : parseJobsListHtml(bodyAsString))
			jobDetails.add(getJob(j));

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

	public String getPrinterModel() throws ClientProtocolException, IOException {

		if (printerModel == null)
			parseStartWlm();

		return printerModel;
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
