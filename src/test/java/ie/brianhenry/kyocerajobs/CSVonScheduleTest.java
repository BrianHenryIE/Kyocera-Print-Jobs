package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

public class CSVonScheduleTest {

	// Folder containing the test logs
	String folderPath = "./src/test/resources/testlogs/";

	String printerName = "TestLog";

	CSVonSchedule service = new CSVonSchedule("87.35.237.21", folderPath);

	Printer photocopier = new Printer("87.35.237.21");

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

	@Test(expected = FileNotFoundException.class)
	public void getMostRecentLogFileNameFileNotFound() throws FileNotFoundException {

		service.getMostRecentLogFileName(folderPath + "empty/", printerName);
	}

	@Test
	public void getLastSavedJobTest() throws FileNotFoundException {

		JobDetail lastJob = service.getLastSavedJob(folderPath, printerName);

		assertEquals(1635, lastJob.getJobNumber());
	}

}
