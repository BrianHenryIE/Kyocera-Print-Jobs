package ie.brianhenry.kyocerajobs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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

	void saveNewJobsByDate(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> toSave = new ArrayList<JobDetail>();
		int getJobsFrom;

		String latestLog = LogFiles.getMostRecentLogFileName(folderPath, printerName);

		if (latestLog != null) {
			JobDetailCSV l2csv = new JobDetailCSV(latestLog);

			// We'll be resaving the latest csv as it may have new jobs
			toSave = l2csv.readCSV();

			getJobsFrom = LogFiles.getLastSavedJob(folderPath, printerName).getJobNumber();
		} else {
			getJobsFrom = 0;
		}

		List<JobDetail> newJobs = printer.getJobsSinceJobNumber(getJobsFrom);

		toSave.addAll(newJobs);

		LogFiles.sortJobsByNumber(toSave);

		TreeMap<LocalDate, List<JobDetail>> sorted = LogFiles.splitJobsByDate(toSave);

		for (LocalDate day : sorted.keySet()) {

			JobDetailCSV jd = new JobDetailCSV(csvFolder + "/" + printer.getPrinterName() + day.toString());
			jd.write(sorted.get(day));

		}

	}

}
