package ie.brianhenry.kyocerajobs;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class JobDetailTest {

	@Test
	public void testSmallHtml() throws Exception {

		String jobDetailHtml = IOUtils.toString(this.getClass().getResourceAsStream("jobdetail.html"), "UTF-8");

		JobDetail j = JobDetail.fromHtml(jobDetailHtml);

		assertEquals(j.getJobType(), 1);
		assertEquals(j.getJobName(), "doc00163520151014145654");
		assertEquals(j.getUserName(), "");
		assertEquals(j.getConnectedTo(), "connectedto");
		assertEquals(j.getAcceptedTime(),
				LocalDateTime.parse("2015/10/14 14:56", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
		assertEquals(j.getEndTime(),
				LocalDateTime.parse("2015/10/14 14:57", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
		assertEquals(j.getOriginalPages(), 1);
		assertEquals(j.getCopies(), 1);
		assertEquals(j.getPrintedPages(), 1);

	}
	

	@Test
	public void testLongHtml() throws Exception {

		String jobDetailHtml = IOUtils.toString(this.getClass().getResourceAsStream("fullpage.html"), "UTF-8");

		JobDetail j = JobDetail.fromHtml(jobDetailHtml);

		assertEquals(j.getJobType(), 1);
		assertEquals(j.getJobName(), "doc00163520151014145654");
		assertEquals(j.getUserName(), "");
		assertEquals(j.getConnectedTo(), "connectedto");
		assertEquals(j.getAcceptedTime(),
				LocalDateTime.parse("2015/10/14 14:56", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
		assertEquals(j.getEndTime(),
				LocalDateTime.parse("2015/10/14 14:57", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
		assertEquals(j.getOriginalPages(), 1);
		assertEquals(j.getCopies(), 1);
		assertEquals(j.getPrintedPages(), 1);

	}

}
