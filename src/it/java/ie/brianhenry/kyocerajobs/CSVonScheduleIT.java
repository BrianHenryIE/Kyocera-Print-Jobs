package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class CSVonScheduleIT {

	// Folder containing the test logs
	String folderPath = "./src/it/resources/testlogs/empty";

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

		int jobNumber = latestJobNumber - 7 > 0 ? latestJobNumber - 7 : 0;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		assertEquals(7, recentJobs.size());

	}

	@Test
	public void getJobsSinceJobNumberZero() throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(0);

		assertTrue(recentJobs.size() > 0);
	}

	@Test
	public void getJobsSinceJobNumberPage2Test() throws ClientProtocolException, IOException, ParseJobException {

		// TODO How to communicate if the test isn't relevant?
		if(latestJobNumber - 15 <0)
			return;
		
		int jobNumber = latestJobNumber - 15;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		assertEquals(15, recentJobs.size());

	}
	
	// Failure parsing job number 5615
	// latest job    007214	

	@Ignore // extremely long running test
	@Test
	public void getJobsSinceJobNumberExceedTest() throws ClientProtocolException, IOException, ParseJobException {

		// TODO How to communicate that the test isn't relevant?
		if(latestJobNumber - 1601 < 0)
			return;
		
		int jobNumber = latestJobNumber - 1601;

		List<JobDetail> recentJobs = service.getJobsSinceJobNumber(jobNumber);

		for (JobDetail j : recentJobs) {
			System.out.println(j.getJobNumber());
		}

		// 1600 comes from running the test, but is it in config somewhere?
		// 1500 is set in Management Settings/History Settings
		assertEquals(1600, recentJobs.size());

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

	// teardown
	// delete files from the folder which should be empty
	// delete last ten and use them in the other folder?
}
