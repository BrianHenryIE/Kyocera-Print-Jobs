package ie.brianhenry.kyocerajobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import ie.brianhenry.kyocerajobs.JobDetail.ParseJobException;

/**
 * Designed to be run as a scheduled task on Windows, poll Kyocera Command Center for recent print jobs and save them to
 * a csv log file
 * 
 * @author brianhenry
 *
 */
public class CSVonSchedule {

	private String printerIp;
	String csvFolder;

	private Printer printer;

	public CSVonSchedule(String printerIp, String csvFolder) {
		this.printerIp = printerIp;
		this.csvFolder = csvFolder;
		printer = new Printer(printerIp);
	}

	public void go() throws ClientProtocolException, IOException, ParseJobException {
		saveNewJobsByDate(csvFolder, printer.getPrinterName());
	}

	public static void main(String[] args) throws ClientProtocolException, IOException, ParseJobException {

		String printerIp = "87.35.237.21";

		String csvFolder = "/Users/brianhenry/Sites/kyocerajobs";

		CSVonSchedule service = new CSVonSchedule(printerIp, csvFolder);

		service.go();
	}



	public List<JobDetail> getJobsSinceLastSavedJob(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		JobDetail lastSavedJob = LogFiles.getLastSavedJob(folderPath, printerName);

		int lastSavedJobNumber = lastSavedJob == null ? 0 : lastSavedJob.getJobNumber();

		return printer.getJobsSinceJobNumber(lastSavedJobNumber);
	}

	void saveNewJobsByDate(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> toSave = new ArrayList<JobDetail>();
		int getJobsFrom;
		try {
			String latestLog = LogFiles.getMostRecentLogFileName(folderPath, printerName);

			JobDetailCSV l2csv = new JobDetailCSV(latestLog);

			toSave = l2csv.readCSV();

			getJobsFrom = LogFiles.getLastSavedJob(folderPath, printerName).getJobNumber();
		} catch (FileNotFoundException e) {
			getJobsFrom = 0;
		}

		List<JobDetail> newJobs = printer.getJobsSinceJobNumber(getJobsFrom);

		toSave.addAll(newJobs);

		LocalDate savingDate = null;
		JobDetailCSV dateCSV = null;
		List<JobDetail> saveNow = null;

	}

}
