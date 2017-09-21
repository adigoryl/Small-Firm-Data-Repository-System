package yuconz23d;

import java.util.Arrays;
import java.util.List;

/**
 * Store an enumerated list of every type of Record available.
 * This also includes name-retrieval per record for consistent
 * naming across the system. Also, all columns of each record
 * are defined as a List of Column objects. This means that
 * every RecordType stores exactly the expected columns and
 * their appropriate display texts and validation strings for
 * use by the system. This keeps the system robust and flexible 
 * and prevents code redundancy by re-defining this elsewhere. 
 *
 */
public enum RecordType {
	PersonalDetails("Personal Details",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("forename", "Forename", "[A-Za-z]*", "Forename must only contain characters", false, true),
					new Column("surname", "Surname", "[a-zA-Z]*", "Surname must only contain characters", false, true),
					new Column("dateOfBirth", "Date Of Birth (yyyy-mm-dd)", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])", "Date of birth must be in the format yyyy-mm-dd", false, true),
					new Column("addressLine1", "House # and Street", "[a-zA-Z0-9 _]*", "House # and Street can only contain numbers and characters", false, true),
					new Column("addressTown", "Town", "[a-zA-Z]*", "Town name can only contain characters", false, true),
					new Column("addressCounty", "County", "[a-zA-Z]*", "County can only contain characters", false, true),
					new Column("addressPostcode", "Postcode", "[a-zA-Z_0-9]{3}[\" \"][a-zA-Z_0-9]{3}|[a-zA-Z_0-9]{3}[a-zA-Z_0-9]{3}", "Postcode must only contain numbers or characters", false, true),
					new Column("telNo", "Telephone #", "[0-9]{8,12}", "Telephone number must be between 8-12 numbers only", false, true),
					new Column("mobNo", "Mobile #", "[0-9]{8,12}", "Telephone number must be between 8-12 numbers only", false, true),
					new Column("emergencyContact", "Emergency Contact", "[a-zA-Z ]*", "Emergency contact can only contain characters", false, true),
					new Column("emergencyContactNo", "Emergency Contact #", "[0-9]{8,12}", "Telephone number must be between 8-12 numbers only", false, true)
				)),
	InitialEmploymentDetails("Initial Employment Details",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("initialDepartment", "Department ID", "[1-4]", "Must be a number between 1 - 4", false, true),
					new Column("initalRole", "Initial Role", "[1-3]", "Must be a number between 1 - 3", false, true),
					new Column("initalSalary", "Initial Salary", "[0-9.]*", "Must contain only numbers", false, true),
					new Column("CV", "CV", "[a-zA-Z0-9.,'\";!-]*", "CV Must contain only alphabetical, numerical or basic punctuation characters.", false, true),
					new Column("interviewNotes", "Interview Notes", "[a-zA-Z0-9 .,'\"();!$£-]*", "Interview Notes must contain only alphabetical, numerical or basic punctuation characters.", false, true),
					new Column("interviewer", "Interviewer", "[0-9]{6}", "Must be a 6 digit employee ID", false, true)
				)),
	AnnualReview("Annual Review",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("year", "Review Year", "[0-9]{4}", "Must be in the format yyyy", false, true),
					new Column("firstReviewer", "First Reviewer", "[a-zA-Z]", "Must contain only characters", false, true),
					new Column("secondReviewer", "Second Reviewer", "[a-zA-Z]", "Must contain only characters", false, true),
					new Column("section", "Section", "[a-zA-Z_0-9]", "Must contain only numbers and characters", false, true),
					new Column("jobTitle", "Job Title", "[a-zA-Z]", "Must contain only characters", false, true),
					new Column("pastObjectives", "Past Objectives", ".", "Must contain...", false, true),
					new Column("pastPerformanceSummary", "Past Performance Summary", ".", "Must contain...", false, true),
					new Column("futureObjectives", "Future Objectives", ".", "Must contain...", false, true),
					new Column("reviewerComments", "Reviewer Comments", ".", "Must contain...", false, true),
					new Column("revieweeSignature", "Reviewee Signature", ".", "Must contain...", false, true),
					new Column("revieweeSignatureDate", "Reviewee Signature Date (yyyy-mm-dd)", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("firstReviewerSignature", "First Reviewer Signature", ".", "Must contain...", false, true),
					new Column("firstReviewerSignatureDate", "Fist Reviewer Signature Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("secondReviewerSignature", "Second Reveiwer Signature", ".", "Must contain...", false, true),
					new Column("secondReviewerSignatureDate", "Second Reviewer Signature Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true)
				)),
	SalaryIncrease("Salary Increase",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("startDate", "Start Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("newSalary", "New Salary", "[0-9]", "Must contain only numbers", false, true),
					new Column("status", "Approval Status", "pending|approved|rejected", "Must be 'pending', 'approved', or 'rejected'", false, true)
				)),
	Promotion("Promotion",
			Arrays.asList(
					new Column("promotionId", "Promotion ID", "[0-9]*", "Promotion id must contain only numbers", true, false),
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("newRole", "New Role", "[1-3]", "New role ID must be between 1-3", false, true),
					new Column("newSalary", "New Salary", "[0-9.]*", "New Salary must contain only numbers or decimal points", false, true),
					new Column("startDate", "Start date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])", "Must be in the format yyyy-mm-dd", false, true)
				)),
	Probation("Probation",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, false),
					new Column("startDate", "Start Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("reviewDate", "Review Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("endDate", "End Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("reasons", "Reasons", ".", "Must contain...", false, true),
					new Column("status", "Approval Status", "pending|approved|rejected", "Must be 'pending', 'approved', or 'rejected'", false, true)
				)),
	Termination("Termination",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, true),
					new Column("endDate", "End Date", "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]", "Must be in the format yyyy-mm-dd", false, true),
					new Column("Reasons", "Reason(s)", ".", "Must contain...", false, true),
					new Column("status", "Approval Status", "pending|approved|rejected", "Must be 'pending', 'approved', or 'rejected'", false, true)
				)),
	Employee("Employee",
			Arrays.asList(
					new Column("employeeId", "Employee ID", "[0-9]{6}", "Employee ID must be 6 digits long", false, true),
					new Column("employeeLogin", "Employee Login", "[a-zA-Z_0-9]", "Must contain only characters and numbers", false, true),
					new Column("departmentId", "Deprartment ID", "[0-9]", "Must contain only numbers", false, true)
				));
	
	private String recordName;
	private List<Column> columns;
	
	/**
	 * Instantiate RecordTypes with their appropriate name
	 * and column objects to store column information
	 * @param recordName The name for the record
	 * @param recordColumns List<Column> an exhaustive list of all columns stored for this type of record
	 */
	private RecordType(String recordName, List<Column> recordColumns) {
		this.recordName = recordName;
		this.columns = recordColumns;
	}
	
	/**
	 * Return the String representation of this record's name
	 */
	public String toString() {
		return recordName;
	}
	
	/**
	 * Retrieves all of the columns and the information
	 * that is related to this record type
	 * @return A list of Column objects that this record type has
	 */
	public List<Column> getColumns() {
		return columns;
	}
}
