package ie.brianhenry.kyocerajobs;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Ignore;
import org.junit.Test;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

public class CSVonScheduleIT {

	// Folder containing the test logs
	String folderPath = "./src/it/resources/testlogs/empty";

	String printerName = "TestLog";

	CSVonSchedule service = new CSVonSchedule("87.35.237.21", folderPath);

	Printer photocopier = new Printer("87.35.237.21");


	@Ignore
	@Test
	public void getJobsSinceLastSavedJobTest() throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> recentJobs = service.getJobsSinceLastSavedJob(folderPath, printerName);

		for (JobDetail jd : recentJobs) 
			System.out.println(jd.getJobNumber());
	
	}

	@Test
	public void saveNewJobsByDateTest() {

		// How to assert and cleanup files?!!

	}

	// teardown
	// delete files from the folder which should be empty
	// delete last ten and use them in the other folder?
}
