package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class LogToCSVTest {

	LogToCSV l2csv = new LogToCSV();

	// jobType,jobName,userName,connectedTo,acceptedTime,endTime,originalPages,copies,printedPages,colorMode;

	@Test
	public void readCsvTest() {

		String filename = "readtest.csv";

		List<JobDetail> joblog = l2csv.readCSV(this.getClass().getResourceAsStream(filename));
			
		assertEquals(3, joblog.size());

		assertEquals("doc0", joblog.get(0).getJobName());
		assertEquals("doc1", joblog.get(1).getJobName());
		assertEquals("doc2", joblog.get(2).getJobName());
	}

	@Test
	public void writeBeanToFileTest() throws IOException {

		String filename = "./src/test/resources/logfile.csv";

		List<JobDetail> jobs = new ArrayList<JobDetail>();

		String jobDetailHtml = IOUtils.toString(this.getClass().getResourceAsStream("jobdetail.html"), "UTF-8");

		JobDetail job1 = JobDetail.fromHtml(jobDetailHtml);
		JobDetail job2 = JobDetail.fromHtml(jobDetailHtml);
		JobDetail job3 = JobDetail.fromHtml(jobDetailHtml);

		jobs.add(job1);
		jobs.add(job2);
		jobs.add(job3);

		l2csv.write(jobs, filename);

	}
}
