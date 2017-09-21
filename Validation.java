package yuconz23d;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

	/**
	 * Validates a given record to ensure it has correct value formats
	 * stored in the details LinkedHashMap. This is done by comparing it 
	 * with regular expressions which are specified per-column in the 
	 * RecordType Enumeration. 
	 * @param record The record to be validated
	 * @return List<String> - Empty if there were no errors, List containing all error messages if there are errors and null otherwise
	 */
	public List<String> validateRecord(Record record) {
		List<String> errors = new ArrayList<>();

		RecordType rt = record.getRecordType();

		LinkedHashMap<String, Object> recordValues = record.getRecordAsMap();

		for(Column currCol : rt.getColumns()) {
			if(!currCol.isHidden()) {
				Pattern pattern = currCol.getRegexPattern();
				Object currRecordValue = recordValues.get(currCol.getName());
				Matcher matcher = pattern.matcher(currRecordValue + "");
				if(!matcher.matches()) {
					errors.add(currCol.getErrorTip());
				}
			}

		}
		return errors;
	}
}
