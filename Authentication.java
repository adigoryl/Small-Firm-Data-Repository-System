package yuconz23d;

import java.sql.SQLException;

/**
 * This class is responsible for the Authentication
 * of user Logins to the system. This includes checking
 * logins, creating, retrieving and removing sessions
 * as appropriate.
 * @author John
 *
 */
public class Authentication {
	private String user;
	private String staffNo;
	private Role selectedRole;
	private DatabaseHandler db;
	
	/**
	 * Initialises the class, user+staffNo set to null until a login occurs and
	 * db stores a reference to the database handler
	 */
	public Authentication() {
		user = null;
		staffNo = null;
		selectedRole = null;
		db = new DatabaseHandler();
	}
	
	/**
	 * A basic getter method to retrieve the current userID
	 * @return String the current user's login name, null if no one is logged in
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * A basic getter method to retrieve the current user's staffNo
	 * @return Current user's staffNo, null if no one is logged in
	 */
	public String getStaffNo() {
		return staffNo;
	}
	
	/**
	 * Return the role that was selected upon login authentication
	 * @return Enumerated Roles value for the given authentication level, null if not logged in
	 */
	public Role getAccessRole() {
		return this.selectedRole;
	}
	
	/**
	 * Checks whether there is a user currently authenticated
	 * @return true when a user is authenticated, false otherwise
	 */
	public boolean isAuthenticated() {
		return user != null;
	}
	
	/**
	 * Retrieves all available roles for a given userID
	 * @param userID which user's available roles will be found and returned
	 * @return String array of all available roles, null if the userID is not found or an SQL error occurred
	 */
	public String[] getAvailableRoles(String userID) {
		if(userID.isEmpty()) { return null; }
		Integer highestRole = null;
		try {
			highestRole = db.getHighestRoleByLogin(userID);
		} catch(SQLException e) {
			System.err.println("SQL connection error occurred: " + e.getMessage());
		} catch (IllegalArgumentException e1) {
			return null;
		}
		
		if(highestRole == null) {
			return null;
		}
		else if(highestRole == 0) {
			String[] resultArray = {"Suspended"};
			return resultArray;
		}
		else if(highestRole == 1) {
			String[] resultArray = {"Employee"};
			return resultArray;
		} 
		else {
			String highestRoleName = Role.nameOf(highestRole);
			String[] resultArray = {highestRoleName, "Employee"};
			return resultArray;
		}
	}
	
	/**
	 * Checks the given login details and verifies if they are correct,
	 * when correct the user field of this class is updated to contain
	 * this new userID value
	 * @param userID The userID to attempt to login as
	 * @param password The password that the user submitted for this attempt
	 * @param authLevel the authentication level that was selected
	 * @return true when the login was a success, false when it failed
	 */
	public boolean checkLogin(String userID, String password, int authLevel) {
		if(password.isEmpty()) { return false; }
		String correctPassword = null;
		try {
			correctPassword = db.getPassword(userID);
		} catch (SQLException e) {
			System.err.println("SQL connection error occurred: " + e.getMessage());
			e.printStackTrace();
		}
		if(correctPassword != null) {
			if(password.equals(correctPassword)) {
				try {
					db.addSession(userID, authLevel);
					this.user = userID;
					this.selectedRole = Role.valueOf(authLevel);
					this.staffNo = db.getEmployeeIdByLogin(userID);
					return true;
				} catch (SQLException e) {
					System.err.println("SQL connection error occurred: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * Logs the current user out of the system by destroying their session
	 * and forgetting their session information
	 */
	public void logout() {
		try {
			db.destroySession(user);
		} catch (SQLException e) {
			System.err.println("SQL connection error occurred: " + e.getMessage());
		}
		user = null;
	}
	
}
