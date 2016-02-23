package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;
import ie.brianhenry.kyocerajobs.Printer.PrinterLoginException;

public class PrinterIT {

	Printer printer;

	int latestJobNumber;

	@Before
	public void setup() throws ClientProtocolException, IOException, ParseJobException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException {

		latestJobNumber = printer.getRecentJobs(0).get(0).getJobNumber();

		printer = new Printer("87.35.237.21");

	}

	@Test
	public void loginTest() throws ClientProtocolException, IOException, PrinterLoginException {

		String username = "Admin";
		String password = "Admin";

		printer.login(username, password);

		// assert no exception
		assertTrue(true);

	}

	@Test(expected = PrinterLoginException.class)
	public void loginTestFail() throws ClientProtocolException, IOException, PrinterLoginException {

		String username = "Admin";
		String password = "Brian";

		printer.login(username, password);

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

		assertEquals("MainPhotocopier", printer.getPrinterName());

	}

	@Test
	public void getJobsSinceJobNumberTest() throws ClientProtocolException, IOException, ParseJobException {

		int jobNumber = latestJobNumber - 7 > 0 ? latestJobNumber - 7 : 0;

		List<JobDetail> recentJobs = printer.getJobsSinceJobNumber(jobNumber);

		assertEquals(7, recentJobs.size());

	}

	@Test
	public void getJobsSinceJobNumberPage2Test() throws ClientProtocolException, IOException, ParseJobException {

		// TODO How to communicate if the test isn't relevant?
		if (latestJobNumber - 15 < 0)
			return;

		int jobNumber = latestJobNumber - 15;

		List<JobDetail> recentJobs = printer.getJobsSinceJobNumber(jobNumber);

		assertEquals(15, recentJobs.size());

	}

	@Ignore // extremely long running test
	@Test
	public void getJobsSinceJobNumberZero() throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> recentJobs = printer.getJobsSinceJobNumber(0);

		assertTrue(recentJobs.size() > 0);
	}

	// Failure parsing job number 5615
	// latest job 007214

	@Ignore // extremely long running test
	@Test
	public void getJobsSinceJobNumberExceedTest() throws ClientProtocolException, IOException, ParseJobException {

		// TODO How to communicate that the test isn't relevant?
		if (latestJobNumber - 1601 < 0)
			return;

		int jobNumber = latestJobNumber - 1601;

		List<JobDetail> recentJobs = printer.getJobsSinceJobNumber(jobNumber);

		for (JobDetail j : recentJobs) {
			System.out.println(j.getJobNumber());
		}

		// 1600 comes from running the test, but is it in config somewhere?
		// 1500 is set in Management Settings/History Settings
		assertEquals(1600, recentJobs.size());

	}
}
