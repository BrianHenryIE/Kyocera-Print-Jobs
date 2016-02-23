package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;

public class LogFilesTest {

	String folderPath = "./src/test/resources/testlogs/";

	String printerName = "TestLog";

	@Test
	public void getLogFilesTest() {

		// We'll have a log file folder with a list of logs
		// MainPhotocopier2015-11-24.csv

		List<String> logFiles = LogFiles.getLogFiles(folderPath, printerName);

		assertEquals(3, logFiles.size());

	}

	@Test
	public void getMostRecentLogFileName() {

		String mostRecent = LogFiles.getMostRecentLogFileName(folderPath, printerName);

		assertEquals("TestLog2015-11-25.csv", mostRecent);
	}

	@Test
	public void getMostRecentLogFileNameFileNotFound() {

		String mostRecent = LogFiles.getMostRecentLogFileName(folderPath + "empty/", printerName);

		assertNull(mostRecent);
	}

	@Test
	public void getLastSavedJobTest() throws FileNotFoundException {

		JobDetail lastJob = LogFiles.getLastSavedJob(folderPath, printerName);

		assertEquals(1635, lastJob.getJobNumber());
	}

	@Test
	public void getJobsFromFileTest() throws FileNotFoundException {
		List<JobDetail> jobs = LogFiles.getJobsFromFile(folderPath + "../threedaysjobs.csv");

		assertEquals(9, jobs.size());
	}

	@Test
	public void splitJobsByDateTest() throws FileNotFoundException {
		List<JobDetail> jobs = LogFiles.getJobsFromFile(folderPath + "../threedaysjobs.csv");

		TreeMap<LocalDate, List<JobDetail>> sortedJobs = LogFiles.splitJobsByDate(jobs);

		assertEquals(3, sortedJobs.size());

		assertEquals(4, sortedJobs.get(LocalDate.parse("2015-11-23", LogFiles.filenameDateFormatter)).size());
		assertEquals(3, sortedJobs.get(LocalDate.parse("2015-11-24", LogFiles.filenameDateFormatter)).size());
		assertEquals(2, sortedJobs.get(LocalDate.parse("2015-11-25", LogFiles.filenameDateFormatter)).size());

	}

}
