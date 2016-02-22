package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class PrinterIT {

	Printer printer;

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		printer = new Printer("87.35.237.21");

	}

	@Test
	public void loginTest() throws ClientProtocolException, IOException {
		
		String username = "Admin";
		String password = "Admin";
		CloseableHttpResponse response = printer.login(username, password);

		assertEquals(200, response.getStatusLine().getStatusCode());

	}

	@Test
	public void getJobTest() throws ClientProtocolException, IOException, ParseJobException {

		JobDetail j = printer.getJob(1635);

		assertEquals(1, j.getJobType());
		assertEquals("doc00163520151014145654", j.getJobName());
		// assertEquals("", j.getUserName());
		assertEquals("connectedto", j.getConnectedTo());
		assertEquals(LocalDateTime.parse("2015/10/14 14:56", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getAcceptedTime());
		assertEquals(LocalDateTime.parse("2015/10/14 14:57", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getEndTime());
		assertEquals(1, j.getOriginalPages());
		assertEquals(1, j.getCopies());
		assertEquals(1, j.getPrintedPages());

	}

	@Test
	public void getRecentJobsTest() throws ClientProtocolException, IOException, ParseJobException {
		int pageNumber = 1;
		List<JobDetail> jobs = printer.getRecentJobs(pageNumber);
		assertEquals(10, jobs.size());
	}

	@Test
	public void getPrinterNameLiveTest() throws IOException {

		Printer p = new Printer("87.35.237.21");

		assertEquals("MainPhotocopier", p.getPrinterName());

	}

}
