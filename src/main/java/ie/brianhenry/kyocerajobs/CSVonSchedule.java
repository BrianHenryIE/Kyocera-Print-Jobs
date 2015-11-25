package ie.brianhenry.kyocerajobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class CSVonSchedule {

	private String printerIp;
	private String csvFolder;

	public CSVonSchedule(String printerIp, String csvFolder) {
		this.printerIp = printerIp;
		this.csvFolder = csvFolder;
	}

	public static void main(String[] args) {

		String printerIp = "87.35.237.21";

		String csvFolder = "photocopierlogs.csv";

		CSVonSchedule service = new CSVonSchedule(printerIp, csvFolder);

		service.saveNewJobsByDate();

	}

	private void saveNewJobsByDate() {

		Printer photocopier = new Printer(printerIp);

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

	public JobDetail getLastSavedJob(String folderPath, String printerName) throws FileNotFoundException {
		
		String filename = getMostRecentLogFileName(folderPath, printerName);
		JobDetailCSV file = new JobDetailCSV(folderPath + filename);
		
		List<JobDetail> jobs = file.readCSV();
		
		return jobs.get(jobs.size()-1);
	}

}
