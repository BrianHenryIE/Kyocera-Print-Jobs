package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class CSVonScheduleTest {

	// Folder containing the test logs
	String folderPath = "./src/test/resources/testlogs/";

	String printerName = "TestLog";

	CSVonSchedule service = new CSVonSchedule("87.35.237.21", folderPath);

	Printer photocopier = new Printer("87.35.237.21");

	int latestJobNumber;
	
	@Before
	public void setup() throws ClientProtocolException, IOException, ParseJobException{
		latestJobNumber = photocopier.getRecentJobs(0).get(0).getJobNumber();
	}
	
	
	@Test
	public void getLogFilesTest() {

		// We'll have a log file folder with a list of logs
		// MainPhotocopier2015-11-24.csv

		List<String> logFiles = service.getLogFiles(folderPath, printerName);

		assertEquals(3, logFiles.size());
		
	}

	@Test
	public void getMostRecentLogFileName() throws FileNotFoundException {

		String mostRecent = service.getMostRecentLogFileName(folderPath, printerName);

		assertEquals("TestLog2015-11-25.csv", mostRecent);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void getMostRecentLogFileNameFileNotFound() throws FileNotFoundException {

		service.getMostRecentLogFileName(folderPath+"empty/", printerName);
	}

	@Test
	public void getLastSavedJobTest() throws FileNotFoundException {

		JobDetail lastJob = service.getLastSavedJob(folderPath, printerName);
		
		assertEquals(1635, lastJob.getJobNumber());
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
		
		for(JobDetail jd : recentJobs){
			System.out.println(jd.getJobNumber());
		}
		
	}

	@Test
	public void saveNewJobsByDateTest() {

		// How to assert and cleanup files?!!
		
	}
	
	// TODO Teardown

}
