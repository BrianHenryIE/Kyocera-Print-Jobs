package ie.brianhenry.kyocerajobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
	private String csvFolder;

	private Printer photocopier;

	public CSVonSchedule(String printerIp, String csvFolder) {
		this.printerIp = printerIp;
		this.csvFolder = csvFolder;
		photocopier = new Printer(printerIp);
	}

	public static void main(String[] args) {

		String printerIp = "87.35.237.21";

		String csvFolder = "photocopierlogs.csv";

		CSVonSchedule service = new CSVonSchedule(printerIp, csvFolder);

		service.saveNewJobsByDate();

	}

	public List<String> getLogFiles(String folderPath, String printerName) {

		List<String> logFiles = new ArrayList<String>();

		// File f = new File("C:\\");
		// ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));

		File f = new File(folderPath);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

		for (String name : names)
			if (name.startsWith(printerName))
				logFiles.add(name);

		return logFiles;
	}

	private static final DateTimeFormatter filenameDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public String getMostRecentLogFileName(String folderPath, String printerName) {

		List<String> allLogFiles = getLogFiles(folderPath, printerName);

		TreeMap<LocalDate, String> orderedLogFiles = new TreeMap<LocalDate, String>();

		for (String name : allLogFiles)
			orderedLogFiles.put(LocalDate.parse(name.substring(printerName.length(), printerName.length() + 10),
					filenameDateFormatter), name);

		return orderedLogFiles.lastEntry().getValue();
	}

	/**
	 * Reads the log file directory and finds the most recent log file
	 * 
	 * @param folderPath
	 * @param printerName
	 * @return
	 * @throws FileNotFoundException
	 */
	public JobDetail getLastSavedJob(String folderPath, String printerName) throws FileNotFoundException {

		String filename = getMostRecentLogFileName(folderPath, printerName);
		JobDetailCSV file = new JobDetailCSV(folderPath + filename);

		List<JobDetail> jobs = file.readCSV();
		

		return jobs.get(jobs.size() - 1);
	}

	/**
	 * Returns the details for all jobs since the number specified
	 * 
	 * Command Centre only stores the last 100 jobs
	 * 
	 * @param jobNumber
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseJobException
	 */
	public List<JobDetail> getJobsSinceJobNumber(int jobNumber)
			throws ClientProtocolException, IOException, ParseJobException {

		int latestJob = photocopier.getRecentJobs(0).get(0).getJobNumber();

		List<JobDetail> newJobs = new ArrayList<JobDetail>();
		int getJob = latestJob;
		do {
			newJobs.add(photocopier.getJob(getJob));
			getJob--;
		} while (getJob > jobNumber);

		return newJobs;
	}

	public List<JobDetail> getJobsSinceLastSavedJob(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		JobDetail lastSavedJob = getLastSavedJob(folderPath, printerName);

		int lastSavedJobNumber = lastSavedJob.getJobNumber();

		return getJobsSinceJobNumber(lastSavedJobNumber);
	}

}
