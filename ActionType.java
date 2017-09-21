package yuconz23d;

/**
 * Store an enumerated list of every type of Action available
 * and provide the string names for them for consistency
 */
public enum ActionType {
	CREATE("Create"),
	VIEW("View"),
	MODIFY("Modify"),
	APPROVE("Approve");
	
	private String name;
	
	/**
	 * Instantiate the ActionType's with their text-represnetation
	 * @param name The text representative name for this action
	 */
	private ActionType(String name) { 
		this.name = name;
	}
	
	/**
	 * Overides the Enum.toString() method to return the
	 * text representation of the action
	 */
	public String toString() {
		return name;
	}
	
}
