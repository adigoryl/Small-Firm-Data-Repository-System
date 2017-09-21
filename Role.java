package yuconz23d;

import java.util.HashMap;

/**
 * The Roles enum is used to convert between role levels as
 * integers and the associated String names for each role
 *
 */
public enum Role {
	EMPLOYEE(1, "Employee"),
	MANAGER(2, "Manager"),
	DIRECTOR(3, "Director");
	
	private int roleID;
	private String roleName;
	private static HashMap<Integer, Role> roleMap = new HashMap<>();
	
	/**
	 * Constructs an Enum Roles with a given ID and Name
	 * @param roleID int -the role's ID
	 * @param roleName String - the role's string based name
	 */
	private Role(int roleID, String roleName) {
		this.roleID = roleID;
		this.roleName = roleName;
	}
	
	// This block prepares a map of role ids to role names for easy retrieval
	static {
		for(Role roleEnum : Role.values()) {
			roleMap.put(roleEnum.roleID, roleEnum);
		}
	}
	
	/**
	 * Returns the related Enum value of this given roleID
	 * @param roleID a role ID to retrieve the value for
	 * @return the related Enum type for this ID
	 */
	public static Role valueOf(int roleID) {
		return roleMap.get(roleID);
	}
	
	/**
	 * Returns a given role as an appropriate string name
	 * @param roleID a role ID to retrieve the string for
	 * @return A string of the given roleID, null if not found
	 */
	public static String nameOf(int roleID) {
		return roleMap.get(roleID).roleName;
	}
	
	/**
	 * Returns the ID of a given roleName
	 * @param roleNameToFind the roleName to lookup
	 * @return the ID relating to the given role, 0 if not found
	 */
	public static int idOf(String roleNameToFind) {
		for(HashMap.Entry<Integer,Role> entry : roleMap.entrySet()) {
			Integer key = entry.getKey();
			String name = entry.getValue().roleName;
			if(roleNameToFind.equals(name)) {
				return key;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the roleID of THIS instance of a Role
	 * @return the ID of the role this method was called on
	 */
	public int id() {
		return roleID;
	}
	
}
