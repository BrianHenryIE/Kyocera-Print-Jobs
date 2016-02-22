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
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;
import ie.brianhenry.kyocerajobs.Printer.PrinterLoginException;

public class PrinterIT {

	Printer printer;

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

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
	
	@Test(expected=PrinterLoginException.class)
	public void loginTestFail() throws ClientProtocolException, IOException, PrinterLoginException {
		
		String username = "Admin";
		String password = "Brian";
				
		printer.login(username, password);
		
		// SYS_AUTH_ERROR_AUTH_REJECTED
		
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
