package ie.brianhenry.kyocerajobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class LogFiles {

	public static List<String> getLogFiles(String folderPath, String printerName) {

		List<String> logFiles = new ArrayList<String>();

		File f = new File(folderPath);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

		for (String name : names)
			if (name.startsWith(printerName))
				logFiles.add(name);

		return logFiles;
	}

	static final DateTimeFormatter filenameDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static String getMostRecentLogFileName(String folderPath, String printerName) {

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
	public static JobDetail getLastSavedJob(String folderPath, String printerName) throws FileNotFoundException {

		String filename = getMostRecentLogFileName(folderPath, printerName);

		if (filename == null)
			return null;

		List<JobDetail> jobs = getJobsFromFile(folderPath + filename);

		return jobs.get(jobs.size() - 1);
	}

	public static List<JobDetail> getJobsFromFile(String path) throws FileNotFoundException {

		JobDetailCSV file = new JobDetailCSV(path);
		List<JobDetail> jobs = file.readCSV();

		return jobs;
	}

	public static TreeMap<LocalDate, List<JobDetail>> splitJobsByDate(List<JobDetail> jobs) {

		TreeMap<LocalDate, List<JobDetail>> treeMap = new TreeMap<LocalDate, List<JobDetail>>();

		for (JobDetail job : jobs) {
			if (treeMap.get(job.getAcceptedTime().toLocalDate()) == null)
				treeMap.put(job.getAcceptedTime().toLocalDate(), new ArrayList<JobDetail>());

			treeMap.get(job.getAcceptedTime().toLocalDate()).add(job);
		}

		return treeMap;
	}

	public static List<JobDetail> sortJobsByNumber(List<JobDetail> jobs) {

		// Sort by date
		Collections.sort(jobs, new Comparator<JobDetail>() {
			@Override
			public int compare(final JobDetail lhs, JobDetail rhs) {
				if (lhs.getJobNumber() < rhs.getJobNumber())
					return 1;
				else if (lhs.getJobNumber() > rhs.getJobNumber())
					return -1;
				else
					return 0;
			}
		});

		return jobs;
	}

}
