package yuconz23d;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Used to represent all database records in a
 * uniform format that can be accessed by methods 
 * without always needing to query the DB for it
 *
 */
public class Record {
	private RecordType recordType;
	private LinkedHashMap<String, Object> recordValues;
	
	/**
	 * Instantiate an instance of Record, given it's type and
	 * the column fields from the database as a hashmap
	 * @param recordType The type of record that this is, specified by RecordType enum
	 * @param recordDetails A LinkedHashMap containing the values of this record
	 */
	public Record(RecordType recordType, LinkedHashMap<String, Object> recordValues){
		this.recordType = recordType;
		this.recordValues = recordValues;
	}
	
	/**
	 * Getter method to retrieve the type of record that 
	 * this instance of Record is
	 * @return RecordType enum of which type of record this is
	 */
	public RecordType getRecordType() {
		return recordType;
	}
	
	/**
	 * Returns a LinkedHashMap of Sting:Object mappings for
	 * all columns of the given record
	 * @return LinkedHashMap<String, Object> containing all columns of the record as <ColumName, ColumnValue>
	 */
	public LinkedHashMap<String, Object> getRecordAsMap() {
		return recordValues;
	}
	
	/**
	 * Allows the Record to have a single value updated easily
	 * @param columnName The name of the column which is being given a new value
	 * @param newValue The value to put into this column
	 * @return True when the columnName given is found and updated, false otherwise
	 */
	public boolean updateValue(String columnName, Object newValue) {
		if(recordValues.containsKey(columnName)) {
			recordValues.put(columnName, newValue);
			return true;
		}
		return false;
	}
	
	/**
	 * Update all of the values within the Record. This method
	 * is a shortcut instead of many singular updateValue() calls,
	 * it allows you to update the entire record map at once by
	 * providing a new map, as long as the keySet's match correctly
	 * @param newValues LinkedHashMap<String, Object> containing all of the new values as <ColumnName, value>
	 * @return True if the keySets matched and the values were updated, false otherwise
	 */
	public boolean updateValues(LinkedHashMap<String, Object> newValues) {
		Set<String> newValuesKeys = newValues.keySet();
		Set<String> recordKeys = recordValues.keySet();
		if(newValuesKeys.equals(recordKeys)) {
			this.recordValues = newValues;
			return true;
		}
		return false;
	}
}






