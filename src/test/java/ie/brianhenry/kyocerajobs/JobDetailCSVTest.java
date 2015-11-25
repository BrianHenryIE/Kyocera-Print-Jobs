package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class JobDetailCSVTest {

	@Test
	public void readCsvTest() throws FileNotFoundException {

		String filename = "readtest.csv";

		URL filepath = this.getClass().getResource(filename);
		
		JobDetailCSV l2csv = new JobDetailCSV(filepath.toString().substring(5));

		List<JobDetail> joblog = l2csv.readCSV();

		assertEquals(3, joblog.size());

		assertEquals(1635, joblog.get(0).getJobNumber());
		assertEquals(1635, joblog.get(1).getJobNumber());
		assertEquals(1635, joblog.get(2).getJobNumber());
	}

	@Test
	public void writeBeanToFileTest() throws IOException {

		String filename = "./src/test/resources/logfile.csv";

		JobDetailCSV l2csv = new JobDetailCSV(filename);

		List<JobDetail> jobs = new ArrayList<JobDetail>();

		String jobDetailHtml = IOUtils.toString(this.getClass().getResourceAsStream("jobdetail.html"), "UTF-8");

		JobDetail job1 = JobDetail.fromHtml(jobDetailHtml);
		JobDetail job2 = JobDetail.fromHtml(jobDetailHtml);
		JobDetail job3 = JobDetail.fromHtml(jobDetailHtml);

		jobs.add(job1);
		jobs.add(job2);
		jobs.add(job3);

		l2csv.write(jobs);

		// TODO An assert and cleanup
	}
}
