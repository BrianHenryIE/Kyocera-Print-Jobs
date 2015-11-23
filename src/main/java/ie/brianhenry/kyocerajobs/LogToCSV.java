package ie.brianhenry.kyocerajobs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

public class LogToCSV {

	public static void main(String[] args) throws ClientProtocolException, IOException {

		String printerIp = "87.35.237.21";

		String csvFile = "photocopierlogs.csv";

		Printer photocopier = new Printer(printerIp);

		// If file doesn't exist, create it

		CSVReader reader = new CSVReader(new FileReader(csvFile));
		List<String[]> myEntries = reader.readAll();
		reader.close();

		// Get last entry
		String[] lastLog = myEntries.get(myEntries.size() - 1);

		// Whatever index the job number will be
		int lastLoggedJob = Integer.parseInt(lastLog[2]);

		int pageNumber = 1;

		photocopier.getRecentJobs(pageNumber);

		CSVWriter writer = new CSVWriter(new FileWriter(csvFile), '\t');
		// feed in your array (or convert your data to an array)
		String[] entries = "first#second#third".split("#");
		writer.writeNext(entries);
		writer.close();

	}

	ColumnPositionMappingStrategy<JobDetail> strategy;

	{
		strategy = new ColumnPositionMappingStrategy<JobDetail>();
		strategy.setType(JobDetail.class);

		String[] columns = new String[] { "jobType", "jobName", "userName", "connectedTo", "acceptedTime", "endTime",
				"originalPages", "copies", "printedPages", "colorMode" };
		strategy.setColumnMapping(columns);
		
	}

	List<JobDetail> readCSV(InputStream inputStream) {

		CsvToBean<JobDetail> csvToBean = new CsvToBean<JobDetail>();

		// Map<String, String> columnMapping = new HashMap<String, String>();
		// columnMapping.put("jobType", "jobType");
		// columnMapping.put("jobName", "jobName");
		// columnMapping.put("userName", "userName");
		// columnMapping.put("connectedTo", "connectedTo");
		// columnMapping.put("acceptedTime", "acceptedTime");
		// columnMapping.put("endTime", "endTime");
		// columnMapping.put("originalPages", "originalPages");
		// columnMapping.put("copies", "copies");
		// columnMapping.put("printedPages", "printedPages");
		// columnMapping.put("colorMode", "colorMode");

		// HeaderColumnNameTranslateMappingStrategy<JobDetail> strategy = new
		// HeaderColumnNameTranslateMappingStrategy<JobDetail>();
		// //strategy.setType(JobDetail.class);
		// strategy.setColumnMapping(columnMapping);
		//

		CSVReader reader = new CSVReader(new InputStreamReader(inputStream), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
		
		List<JobDetail> list = csvToBean.parse(strategy, reader);

		return list;

	}


	public void write(List<JobDetail> jobs, String filename) throws IOException {

		BeanToCsv<JobDetail> b2c = new BeanToCsv<JobDetail>();

		FileWriter fw = new FileWriter(filename);

		CSVWriter writer = new CSVWriter(fw);

		b2c.write(strategy, writer, jobs);

		fw.flush();
		fw.close();

	}

}
