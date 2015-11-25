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

		assertEquals(1, j.getJobType());
		assertEquals("doc00163520151014145654", j.getJobName());
		assertEquals("", j.getUserName());
		assertEquals("connectedto", j.getConnectedTo());
		assertEquals(LocalDateTime.parse("2015/10/14 14:56", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getAcceptedTime());
		assertEquals(LocalDateTime.parse("2015/10/14 14:57", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getEndTime());
		assertEquals(1, j.getOriginalPages());
		assertEquals(1, j.getCopies());
		assertEquals(1, j.getPrintedPages());

	}

	@Test
	public void testLongHtml() throws Exception {

		String jobDetailHtml = IOUtils.toString(this.getClass().getResourceAsStream("fullpage.html"), "UTF-8");

		JobDetail j = JobDetail.fromHtml(jobDetailHtml);

		assertEquals(1, j.getJobType());
		assertEquals("doc00163520151014145654", j.getJobName());
		assertEquals("", j.getUserName());
		assertEquals("connectedto", j.getConnectedTo());
		assertEquals(LocalDateTime.parse("2015/10/14 14:56", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getAcceptedTime());
		assertEquals(LocalDateTime.parse("2015/10/14 14:57", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				j.getEndTime());
		assertEquals(1, j.getOriginalPages());
		assertEquals(1, j.getCopies());
		assertEquals(1, j.getPrintedPages());

	}

}
