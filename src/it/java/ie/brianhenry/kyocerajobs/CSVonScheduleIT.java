package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class CSVonScheduleIT {
	
	// Folder containing the test logs
	String folderPath = "./src/test/resources/testlogs/";

	String printerName = "TestLog";

	CSVonSchedule service = new CSVonSchedule("87.35.237.21", folderPath);

	Printer photocopier = new Printer("87.35.237.21");

	int latestJobNumber;

	@Before
	public void setup() throws ClientProtocolException, IOException, ParseJobException {
		latestJobNumber = photocopier.getRecentJobs(0).get(0).getJobNumber();
	}

	@Test
	public void getJobsSinceJobNumberTest() throws ClientProtocolException, IOException, ParseJobException {

		int jobNumber = latestJobNumber - 7;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		assertEquals(7, recentJobs.size());

	}

	@Test
	public void getJobsSinceJobNumberPage2Test() throws ClientProtocolException, IOException, ParseJobException {

		int jobNumber = latestJobNumber - 15;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		assertEquals(15, recentJobs.size());

	}

	@Test
	public void getJobsSinceJobNumberExceedTest() throws ClientProtocolException, IOException, ParseJobException {

		int jobNumber = latestJobNumber - 150;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		assertEquals(150, recentJobs.size());

	}

	@Ignore
	@Test
	public void getJobsSinceLastSavedJobTest() throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> recentJobs = service.getJobsSinceLastSavedJob(folderPath, printerName);

		for (JobDetail jd : recentJobs) {
			System.out.println(jd.getJobNumber());
		}

	}

	@Test
	public void saveNewJobsByDateTest() {

		// How to assert and cleanup files?!!

	}
}
