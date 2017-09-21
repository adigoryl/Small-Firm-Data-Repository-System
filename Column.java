package yuconz23d;

import java.util.regex.Pattern;

/**
 * This class is used to define properties about columns
 * within the database. This includes the column names,
 * column 'display' names (i.e the names they should have
 * in the GUI) and the Regular Expressions that can be 
 * used to validate each column. 
 * 
 * This is utilised by the RecordType enum which specifies
 * every column of a record so that different areas of the
 * code can reference names, display names and validate 
 * appropriately per column per record. 
 *
 */
public class Column {
	private String name;
	private String displayText;
	private String regex;
	private String errorTip;
	private boolean hidden;
	private boolean editable;
	
	/**
	 * Constructs a representation of each column in a table
	 * of the database
	 * @param name The name of the column in the Database
	 * @param displayText The text to be displayed within the GUI for this column
	 * @param regex A String representing the Regular Expression which validates this column's value
	 * @param hidden True if this column should be hidden from user entry, false otherwise
	 * @param errorTip A message to display when this column is incorrectly filled out (e.g must be 6 numerical digits long)
	 * @param editable True if this column's value can be edited, false otherwise
	 */
	public Column(String name, String displayText, String regex, String errorTip, boolean hidden, boolean editable) {
		this.name = name;
		this.displayText = displayText;
		this.regex = regex;
		this.errorTip = errorTip;
		this.hidden = hidden;
		this.editable = editable;
	}

	/**
	 * Retrieves the name for this column
	 * @return the name of this Column
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the displayText for this Column
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * @return the regex which can be used to validate this Column
	 */
	public String getRegexString() {
		return regex;
	}
	
	/**
	 * Convenience method to return a Pattern object for this column's
	 * regular expression string for validation purposes
	 * @return Pattern - the regex pattern for this column
	 */
	public Pattern getRegexPattern() {
		return Pattern.compile(regex);
	}
	
	/**
	 * @return true when this column should be hidden from user view, false otherwise
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * @return true when this column should be editable, false otherwise
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * @return an appropriate error message to display when the given value is invalid
	 */
	public String getErrorTip() {
		return errorTip;
	}
}
