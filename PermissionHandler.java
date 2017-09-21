package yuconz23d;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to handle Permissions, including checking user's
 * permissions based on levels, user-based and individually
 * granted permissions
 *
 */
public class PermissionHandler {
	DatabaseHandler db;
	
	/**
	 * Constructs a new PermissionHandler, initialising the db field
	 */
	public PermissionHandler() {
		db = new DatabaseHandler();
	}
	
	
	/**
	 * Checks whether the given user has permission to do an action to
	 * a specific record
	 * @param userId String - The userId of the user who is attempting the action
	 * @param authRole Role - The selected authentication level of the current user
	 * @param action ActionType - The action that is being attempted
	 * @param targetRecord Record - The record that is being targeted by this action
	 * @return True if the user has permission to do this, false otherwise
	 */
	public boolean hasPermission(String userId, String staffNo, Role authRole, ActionType action, Record targetRecord) {
		ArrayList<String> targetPerms = generatePermissionStrings(userId, staffNo, authRole, action, targetRecord);
		
		// Check the PermissionsByRole table
		try {
			for(String perm : targetPerms) {
				if(db.checkPermissionByRole(authRole, perm)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
		// Check the PermissionsByDepartment
		try {
			int employeeDepartment = db.getDepartmentId(staffNo);
			for(String perm : targetPerms) {
				if (db.checkPermissionByDepartment(employeeDepartment, perm)) {
					return true;
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
		// Check the PermissionsByUser
		try {
			for(String perm : targetPerms) {
				if(db.checkPermissionByUser(userId, perm)) {
					return true;
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		// TODO: Check for PermissionsByRequest
		
		return false;
	}
	
	/**
	 * Generates all possible String representations of a permission for the
	 * record and the given action, for example this would include:
	 * "records.personaldetails.view.self" - View their own
	 * "records.personaldetails.view.*" - View all 
	 * "records.personaldetails.view.323024" - View specifically by employeeId
	 * This allows each of the possible strings to be searched for within the
	 * permission assignment tables in the database. 
	 * @param userId String - The userId which is attempting the action
	 * @param staffNo String - The staffNo of the employee who is attempting the action
	 * @param action ActionType enum - The specified action that is being done
	 * @param targetRecord Record - The specific record that is being created/modified/viewed
	 * @return ArrayList<String> containing all possible permission strings that could match this action
	 */
	public ArrayList<String> generatePermissionStrings(String userId, String staffNo, Role authRole, ActionType action, Record targetRecord) {
		ArrayList<String> results = new ArrayList<>();
		String recordName = "";
		switch(action) {
		case CREATE:
			String perm =  "records.";
			recordName = targetRecord.getRecordType().toString();
			recordName = recordName.toLowerCase();
			recordName = recordName.replace(" ", ""); // Remove spaces
			perm += recordName;
			perm += ".";
			perm += "create";
			results.add(perm);
			break;
		case VIEW:
		case MODIFY:
			recordName = targetRecord.getRecordType().toString();
			recordName = recordName.toLowerCase();
			recordName = recordName.replace(" ", ""); // Remove spaces
			String actionName = action.toString().toLowerCase();
			
			String basePerm = "records.";
			basePerm += recordName;
			basePerm += ".";
			basePerm += actionName;
			basePerm += ".";
			
			HashMap<String, Object> record = targetRecord.getRecordAsMap();
			if(record.get("employeeId") != null && record.get("employeeId").equals(staffNo)) {
				String selfPerm = basePerm + "self";
				results.add(selfPerm);
			}
			// Record doesn't pertain to themselves
			String allPerm = basePerm + "*";
			results.add(allPerm);

			String targetEmployeeId = (String) record.get("employeeId");
			if(targetEmployeeId != null) {
				String directPerm = basePerm + targetEmployeeId;
				results.add(directPerm);
			}
			break;
		case APPROVE:
			break;
		}
		return results;

	}
}
