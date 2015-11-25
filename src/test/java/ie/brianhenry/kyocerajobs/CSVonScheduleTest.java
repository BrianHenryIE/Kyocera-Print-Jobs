package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CSVonScheduleTest {

	// Folder containing the test logs
	String folderPath = "./src/test/resources/testlogs/";

	String printerName = "TestLog";

	CSVonSchedule service = new CSVonSchedule("", folderPath);

	@Test
	public void getLogFilesTest() {

		// We'll have a log file folder with a list of logs
		// MainPhotocopier2015-11-24.csv

		List<String> logFiles = service.getLogFiles(folderPath, printerName);

		assertEquals(3, logFiles.size());
		
	}

	@Test
	public void getMostRecentLogFileName() {

		String mostRecent = service.getMostRecentLogFileName(folderPath, printerName);

		assertEquals("TestLog2015-11-25.csv", mostRecent);
	}

	@Test
	public void getLastSavedJobTest() {

	}

	@Test
	public void getJobsSinceLastSavedJobTest() {

	}

	@Test
	public void saveNewJobsByDateTest() {

	}

}
