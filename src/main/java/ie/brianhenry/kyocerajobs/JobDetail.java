package ie.brianhenry.kyocerajobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

var JobDetail = Array();

JobDetail[0] = sJobType["1"];
JobDetail[1] = "doc00163520151014145654";
JobDetail[2] = "";
JobDetail[3] = connectedto;
JobDetail[4] = "2015/10/14 14:56";
JobDetail[5] = "2015/10/14 14:57";
JobDetail[6] = "1";
JobDetail[7] = "1"+"/"+"1";
JobDetail[8] = "1"+"/"+ Number("1")*Number("1");

var colormode = 3;
JobDetail[9] = sJobColorMode[colormode];
JobDetail[9] = sJobColorMode[3];
var JobNumber = "001635"



JobDetail[0] Job Type
JobDetail[1] Job Name
JobDetail[2] username
JobDetail[3] connectedto ??
JobDetail[4] accepted time
JobDetail[5] end time
JobDetail[6] original pages
JobDetail[7] copies
JobDetail[8] printed pages

var colormode = 3;
JobDetail[9] = sJobColorMode[colormode];
JobDetail[9] = sJobColorMode[3]; – Black & White

 */

public class JobDetail {

	private int jobNumber;
	private int jobType;
	private String jobName;
	private String userName;
	private String connectedTo;
	private LocalDateTime acceptedTime;
	private LocalDateTime endTime;
	private int originalPages;
	private int copies;
	private int printedPages;
	private String colorMode;

	public JobDetail() {
	}

	private static final DateTimeFormatter htmlDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

	static JobDetail fromHtml(String html) throws ParseJobException {

		String stringPattern = "JobDetail\\[0\\] = sJobType\\[\"(?<jobType>\\d+)\"\\];.*"
				+ "JobDetail\\[1\\] = \"(?<jobName>.+?)\";.*" + "JobDetail\\[2\\] = \"(?<userName>.*)\";.*"
				+ "JobDetail\\[3\\] = (?<connectedTo>.*?);.*" + "JobDetail\\[4\\] = \"(?<acceptedTime>.*)\";.*"
				+ "JobDetail\\[5\\] = \"(?<endTime>.*)\";.*" + "JobDetail\\[6\\] = \"(?<originalPages>\\d*)\";.*"
				+ "JobDetail\\[7\\] = \"(?<copies>\\d*)\"\\+\"/\"\\+\"\\d*\";.*"
				+ "JobDetail\\[8\\] = \"(?<printedPages>\\d*)\"\\+\"/\"\\+ Number\\(\"\\d*\"\\)\\*Number\\(\"\\d*\"\\);.*"
				+ ".*var colormode = (?<colorMode>\\d*);.*" + "var JobNumber = \"(?<jobNumber>\\d*)\"";

		Pattern pattern = Pattern.compile(".*" + stringPattern + ".*", Pattern.DOTALL);

		Matcher m = pattern.matcher(html);

		if (m.matches()) {
			JobDetail j = new JobDetail();

			j.jobType = Integer.parseInt(m.group("jobType"));
			j.jobName = m.group("jobName");
			j.userName = m.group("userName");
			j.connectedTo = m.group("connectedTo");
			j.acceptedTime = LocalDateTime.parse(m.group("acceptedTime"), htmlDateFormatter);
			j.endTime = LocalDateTime.parse(m.group("endTime"), htmlDateFormatter);
			j.originalPages = Integer.parseInt(m.group("originalPages"));
			// TODO currently just pulls the first number from "15/15"
			j.copies = Integer.parseInt(m.group("copies"));
			// TODO Again, just pulls the first number, which I guess is the number successfully printed of the number
			// attempted.
			j.printedPages = Integer.parseInt(m.group("printedPages"));
			j.colorMode = m.group("colorMode");
			j.jobNumber = Integer.parseInt(m.group("jobNumber"));

			return j;
		} else {
			throw new ParseJobException(html);
		}

	}

	public int getJobType() {
		return jobType;
	}

	public String getJobName() {
		return jobName;
	}

	public String getUserName() {
		return userName;
	}

	public String getConnectedTo() {
		return connectedTo;
	}

	public LocalDateTime getAcceptedTime() {
		return acceptedTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public int getOriginalPages() {
		return originalPages;
	}

	public int getCopies() {
		return copies;
	}

	public int getPrintedPages() {
		return printedPages;
	}

	public String getColorMode() {
		return colorMode;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setConnectedTo(String connectedTo) {
		this.connectedTo = connectedTo;
	}

	public void setAcceptedTime(LocalDateTime acceptedTime) {
		this.acceptedTime = acceptedTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setOriginalPages(int originalPages) {
		this.originalPages = originalPages;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}

	public void setPrintedPages(int printedPages) {
		this.printedPages = printedPages;
	}

	public void setColorMode(String colorMode) {
		this.colorMode = colorMode;
	}

	public int getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(int jobNumber) {
		this.jobNumber = jobNumber;
	}

	public static class ParseJobException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8091738948801165677L;

		private final String html;

		public ParseJobException(String html) {
			this.html = html;
		}

		public String getHtml() {
			return html;
		}
	}
}
