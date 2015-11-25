package ie.brianhenry.kyocerajobs;

import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

public class JobDetailCSV {

	String filename;
	
	ColumnPositionMappingStrategy<JobDetail> positionStrategy = new ColumnPositionMappingStrategy<JobDetail>();
	HeaderColumnNameTranslateMappingStrategy<JobDetail> headerStrategy = new HeaderColumnNameTranslateMappingStrategy<JobDetail>();

	{

		positionStrategy.setType(JobDetail.class);

		String[] columns = new String[] { "jobNumber", "jobType", "jobName", "userName", "connectedTo", "acceptedTime", "endTime",
				"originalPages", "copies", "printedPages", "colorMode" };
		positionStrategy.setColumnMapping(columns);

		Map<String, String> columnMapping = new HashMap<String, String>();
		columnMapping.put("jobNumber", "jobNumber");
		columnMapping.put("jobType", "jobType");
		columnMapping.put("jobName", "jobName");
		columnMapping.put("userName", "userName");
		columnMapping.put("connectedTo", "connectedTo");
		columnMapping.put("acceptedTime", "acceptedTime");
		columnMapping.put("endTime", "endTime");
		columnMapping.put("originalPages", "originalPages");
		columnMapping.put("copies", "copies");
		columnMapping.put("printedPages", "printedPages");
		columnMapping.put("colorMode", "colorMode");

		headerStrategy.setType(JobDetail.class);
		headerStrategy.setColumnMapping(columnMapping);

	}

	private static final DateTimeFormatter csvDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	public JobDetailCSV(String csvFile) {
		this.filename = csvFile;
	}

	List<JobDetail> readCSV() throws FileNotFoundException {
	    
		InputStream inputStream = new FileInputStream(filename);
		
		// Override to parse dates properly
		CsvToBean<JobDetail> csvToBean = new CsvToBean<JobDetail>() {

			@Override
			protected Object convertValue(String value, PropertyDescriptor prop)
					throws InstantiationException, IllegalAccessException {

				if (prop.getName().equals("acceptedTime") || prop.getName().equals("endTime"))
					return LocalDateTime.parse(value, csvDateFormatter);

				return super.convertValue(value, prop);
			}
		};

		CSVReader reader = new CSVReader(new InputStreamReader(inputStream), CSVParser.DEFAULT_SEPARATOR,
				CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

		List<JobDetail> list = csvToBean.parse(positionStrategy, reader);

		return list;

	}

	public void write(List<JobDetail> jobs) throws IOException {

		BeanToCsv<JobDetail> b2c = new BeanToCsv<JobDetail>();

		FileWriter fw = new FileWriter(filename);

		CSVWriter writer = new CSVWriter(fw);

		b2c.write(positionStrategy, writer, jobs);

		fw.flush();
		fw.close();

	}

}
