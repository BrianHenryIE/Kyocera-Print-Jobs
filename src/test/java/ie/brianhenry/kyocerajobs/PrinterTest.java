package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrinterTest {

	Printer printer;

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		printer = new Printer("87.35.237.21");

	}

	@Test
	public void parseJobsPageShortTest() throws IOException {

		String shortString = "JobKey[Index] = \"11\"; sdasdfsadf JobKey[Index] = \"12\"; asfdasdf asd JobKey[Index] = \"13\"; asdfsad fJobKey[Index] = \"14\"; asdfasdfasdfasdfsad fJobKey[Index] = \"15\"; sdasdfsadf JobKey[Index] = \"16\"; asfdasdf asd JobKey[Index] = \"17\"; asdfsad fJobKey[Index] = \"18\"; asdfasdfasdfasdfsad fJobKey[Index] = \"19\"; asdfasdfasdfJobKeyasdfasdfasdfJobKey[Index] = \"20\"; ";

		int[] shortNumbers = { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };

		int[] shortJobs = printer.parseJobsListHtml(shortString);

		Assert.assertArrayEquals(shortNumbers, shortJobs);

	}

	@Test
	public void parseJobsPageFullTest() throws IOException {

		String jobListHtml = IOUtils.toString(this.getClass().getResourceAsStream("jobs.html"), "UTF-8");

		int[] jobNumbers = { 2885, 2884, 2883, 2882, 2881, 2880, 2879, 2878, 2877, 2876 };

		int[] parsedJobs = printer.parseJobsListHtml(jobListHtml);

		Assert.assertArrayEquals(jobNumbers, parsedJobs);

	}

	@Test
	public void getPrinterNameFromTextTest() throws IOException {

		String startWlm = IOUtils.toString(this.getClass().getResourceAsStream("Start_Wlm.html"), "UTF-8");

		Printer p = new Printer("1.2.3.4");

		p.parseStartWlm(startWlm);

		assertEquals("MainPhotocopier", p.getPrinterName());

	}
}
