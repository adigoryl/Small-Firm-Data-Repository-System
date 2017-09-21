package yuconz23d;

import java.sql.SQLException;
import java.util.List;

/**
 * Works as the main class for the program, as well
 * as a gateway for the GUI
 * @author John
 *
 */
public class YuconzApp {
	private static Authentication auth;
	private DatabaseHandler db;
	private PermissionHandler perms;
	private Validation val;
	
	/**
	 * Constructs the YuconzApp by initialising the auth class it uses
	 */
	public YuconzApp() {
		auth = new Authentication();
		db = new DatabaseHandler();
		perms = new PermissionHandler();
		val = new Validation();
	}
	
	/**
	 * Acts as an interface between the GUI and the Authentication class
	 * allowing for the GUI class to send a username through to the Authentication
	 * to be able to retrieve a list of available roles, for example {"Employee", "Director"}]
	 * @param username the username to be submitted
	 * @return String[] containing all available roles, null if the username wasn't found
	 */
	public String[] submitUsername(String username) {
		return auth.getAvailableRoles(username);
	}
	
	/**
	 * Submits a login attempt to the Authentication class and will either cause
	 * a successful login or a failed attempt. (true or false respectively)
	 * @param username The username to be submitted 
	 * @param password The password to be submitted
	 * @param authLevel The level of authentication that has been selected
	 * @return true if the login was succesful, false if it failed
	 */
	public boolean submitLogin(String username, String password, int authLevel) {
		return auth.checkLogin(username, password, authLevel);
	}
	
	/**
	 * Retrieves the currently logged in user
	 * @return String the username of the currently logged in user, null if no logged in user was found
	 */
	public String getCurrentUser() {
		return auth.getUser();
	}
	
	/**
	 * Retrieves the currently logged in User's related employeeID
	 * @return The employeeId of the current user
	 */
	public String getCurrentEmployee() {
		return auth.getStaffNo();
	}
	
	/**
	 * Retrieves the current session's selected role
	 * @return The role that was selected upon login, null if not logged in
	 */
	public Role getCurrentRole() {
		return auth.getAccessRole();
	}
	
	/**
	 * Logs the current user out of the system
	 */
	public void logout() {
		auth.logout();
	}
	
	/**
	 * Asks the PermissionHandler to check the permission for this user
	 * @param userId String - The userId of the user who is attempting the action
	 * @param authRole Role - The selected authentication level of the current user
	 * @param action ActionType - The action that is being attempted
	 * @param targetRecord Record - The record that is being targeted by this action
	 * @return True if the user has permission to do this, false otherwise
	 */
	public boolean checkPermission(String userId, String staffNo, Role authRole, ActionType action, Record targetRecord) {
		return perms.hasPermission(userId, staffNo, authRole, action, targetRecord);
	}
	
	/**
	 * Requests a Record or a List of Records from the database via
	 * the recordtype and related employeeId
	 * @param rType The type of Record (RecordType enum)
	 * @param employeeId The employeeID that the record belongs to
	 * @return The Record or List of Records that were found (List in cases where the employee has multiple promotions for example)
	 * @throws SQLException When an SQL Connection/transaction error occurs
	 */
	public Object viewRecord(RecordType rType, String employeeId) throws SQLException {
		return db.retrieveRecord(employeeId, rType);
	}
	
	/**
	 * Acts as a pass-through for the GUI to the database handler, allowing the
	 * request for a record to be updated. 
	 * @param targetRecord The Record that is being updated (new values inside)
	 * @return True upon success, false otherwise
	 * @throws SQLException When an SQL connection/transaction issue occurs
	 */
	public boolean updateRecord(Record targetRecord) throws SQLException {
		return db.updateRecord(targetRecord);
	}
	
	/**
	 * Acts as a passthrough for the GUI to the database handler, allowing the request
	 * for a record to be created and saved for the first time. 
	 * @param newRecord The new record being saved (values inside)
	 * @return True upon success, false otherwise
	 * @throws SQLException when an SQL connection/transaction error occurs
	 */
	public boolean createRecord(Record newRecord) throws SQLException {
		return db.createRecord(newRecord);
	}
	
	/**
	 * Acts as a passthrough for the GUI to access the validation class, allowing
	 * it to make a check on a record before sending it to the database. 
	 * @param recordToCheck The record to be checked
	 * @return List<String> - Empty when there are no errors, populated with errors when there are
	 */
	public List<String> validateRecord(Record recordToCheck) {
		return val.validateRecord(recordToCheck);
	}

}







