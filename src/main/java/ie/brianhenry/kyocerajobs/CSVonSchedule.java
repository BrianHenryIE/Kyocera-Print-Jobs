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

	public List<String> getLogFiles(String folderPath, String printerName) {

		List<String> logFiles = new ArrayList<String>();

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

		if (allLogFiles.size() == 0)
			return null;

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

		if (filename == null)
			return null;

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

		int latestJob = printer.getRecentJobs(0).get(0).getJobNumber();

		List<JobDetail> newJobs = new ArrayList<JobDetail>();
		int getJob = latestJob;
		try {
			do {
				newJobs.add(printer.getJob(getJob));
				getJob--;
			} while (getJob > jobNumber);
		} catch (ParseJobException e) {
			// TODO
			System.out.println("Failure parsing job number " + getJob);
		}

		return newJobs;
	}

	public List<JobDetail> getJobsSinceLastSavedJob(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		JobDetail lastSavedJob = getLastSavedJob(folderPath, printerName);

		int lastSavedJobNumber = lastSavedJob == null ? 0 : lastSavedJob.getJobNumber();

		return getJobsSinceJobNumber(lastSavedJobNumber);
	}

	void saveNewJobsByDate(String folderPath, String printerName)
			throws ClientProtocolException, IOException, ParseJobException {

		List<JobDetail> toSave = new ArrayList<JobDetail>();
		int getJobsFrom;
		try {
			String latestLog = getMostRecentLogFileName(folderPath, printerName);

			JobDetailCSV l2csv = new JobDetailCSV(latestLog);

			toSave = l2csv.readCSV();

			getJobsFrom = getLastSavedJob(folderPath, printerName).getJobNumber();
		} catch (FileNotFoundException e) {
			getJobsFrom = 0;
		}

		List<JobDetail> newJobs = getJobsSinceJobNumber(getJobsFrom);

		toSave.addAll(newJobs);

		// Sort by date
		Collections.sort(toSave, new Comparator<JobDetail>() {
			@Override
			public int compare(final JobDetail lhs, JobDetail rhs) {
				if (lhs.getAcceptedTime().isBefore(rhs.getAcceptedTime()))
					return 1;
				else if (lhs.getAcceptedTime().isAfter(rhs.getAcceptedTime()))
					return -1;
				else
					return 0;
			}
		});

		LocalDate savingDate = null;
		JobDetailCSV dateCSV = null;
		List<JobDetail> saveNow = null;

		// balls to this. just use a treeset
		for (JobDetail j : toSave) {

			// when we move to a new date
			// or the first
			if (savingDate == null || !j.getAcceptedTime().toLocalDate().equals(savingDate)) {

				// If we're on to a new date and have data, save it
				if (dateCSV != null && saveNow != null)
					dateCSV.write(saveNow);

				// Then clear things read to go around again
				saveNow = new ArrayList<JobDetail>();
				dateCSV = new JobDetailCSV(j.getAcceptedTime().toLocalDate().toString());
			}

		}

	}

}
