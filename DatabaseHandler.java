package yuconz23d;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * The DatabaseHandler acts as an intermediate layer between the database(s) and
 * any other class which needs access to the stored data.
 */
public class DatabaseHandler {

	public DatabaseHandler() {
	}

	/**
	 * Creates a connection to the SQLite Authentication database as a sample
	 * system before Yuconz will allow us to connect to their current system
	 * implementation.
	 * 
	 * @return Returns a Connection object with a connection to the Auth DB
	 * @throws SQLException
	 *             When an SQL error occurs during the connection
	 */
	public Connection connectToAuthDB() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:sqlite:YuconzAuth.db");
		return conn;
	}

	/**
	 * Creates a connection to the SQLite HR database
	 * @return A Connection object with a connection the HR DB
	 * @throws SQLException When an SQL error occurs during the connection
	 */
	public Connection connectToHRDB() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:sqlite:YuconzHR.db");
		return conn;
	}

	/**
	 * Retrieves a password from the Authentication DB for the given UserID
	 * 
	 * @param UserID
	 *            String representing the Users ID
	 * @return String the specified user's password, null when the user isn't
	 *         found
	 * @throws SQLException
	 *             When an SQL error occurs during connection/statement
	 */
	public String getPassword(String userID) throws SQLException {
		Connection conn = connectToAuthDB();
		PreparedStatement pStatement = conn.prepareStatement("SELECT * FROM Users WHERE userID = ?");
		pStatement.setString(1, userID);
		ResultSet results = pStatement.executeQuery();
		String password = results.getString("password");
		conn.close();
		return password;
	}

	/**
	 * Register and add a new session to the Authentication Database
	 * 
	 * @param userId
	 *            The userID that this session belongs to
	 * @throws SQLException
	 *             when a connection/DB update error occurs
	 */
	public void addSession(String userId, int authLevel) throws SQLException {
		Connection conn = connectToAuthDB();
		String sqlQuery = "INSERT INTO Sessions(userId, authLevel) VALUES (?, ?)";
		PreparedStatement pStatement = conn.prepareStatement(sqlQuery);
		pStatement.setString(1, userId);
		pStatement.setInt(2, authLevel);
		pStatement.execute();
		conn.close();
	}

	/**
	 * Retrieves the details of a session from the Authentication Database
	 * 
	 * @param userId
	 *            The userId that the session belongs to
	 * @return HashMap<String, String> containing a mapping of all columns in
	 *         the form: <Column, value>
	 * @throws SQLException
	 *             When a connection or query failure occurs
	 */
	public HashMap<String, String> getSessionDetails(String userID) throws SQLException {
		HashMap<String, String> resultMap = new HashMap<>();

		Connection conn = connectToAuthDB();
		PreparedStatement pStatement = conn.prepareStatement("SELECT * FROM Sessions WHERE UserID = ?");
		pStatement.setString(1, userID);
		ResultSet results = pStatement.executeQuery();
		resultMap.put("sessionID", results.getString("sessionID"));
		resultMap.put("userID", results.getString("userID"));
		resultMap.put("authLevel", results.getInt("authLevel") + "");
		conn.close();
		return resultMap;
	}

	/**
	 * Delete the session information relating to this user's session, in other
	 * words: logout.
	 * 
	 * @param userId
	 *            The user that is destroying their session
	 * @throws SQLException
	 *             when a connection/query error occurs
	 */
	public void destroySession(String userID) throws SQLException {
		Connection conn = connectToAuthDB();
		String sqlQuery = "DELETE FROM Sessions WHERE userID = ?";
		PreparedStatement pStatement = conn.prepareStatement(sqlQuery);
		pStatement.setString(1, userID);
		pStatement.execute();
		conn.close();
	}

	/**
	 * Delete the session information relating to this sessionId, in other
	 * words: logout
	 * 
	 * @param sessionId
	 *            The ID of the session to be destroyed
	 * @throws SQLException
	 *             when a connection/query error occurs
	 */
	public void destroySession(int sessionID) throws SQLException {
		Connection conn = connectToAuthDB();
		String sqlQuery = "DELETE FROM Sessions WHERE sessionID = ?";
		PreparedStatement pStatement = conn.prepareStatement(sqlQuery);
		pStatement.setInt(1, sessionID);
		pStatement.execute();
		conn.close();
	}

	/**
	 * Retrieves the highest possible role a given user can have. This is the
	 * highest job role they have within the company. This is found from
	 * the HR database by searching for either the most recent promotion
	 * record which contains a new role or their initial employment details
	 * 
	 * (Shortcut method to find it for userIDs instead of employeeIds,
	 * however this method includes a systemAccess check whilst employeeId does not
	 * this is because it is safe to assume that a test by employeeId will be
	 * post-login unless it goes through this shortcut)
	 * 
	 * @param userID the ID of the user whose role is being requested
	 * @return an integer representing the role's access level,
	 * 			null when there's an error,
	 * 			0 when the account is suspended from logging in
	 * @throws SQLException when a connection/query error occurs
	 * @throws IllegalArgumentException when an invalid userID is provided
	 */
	public Integer getHighestRoleByLogin(String userID) throws SQLException, IllegalArgumentException {
		// Check if the userId has system access
		Connection authConn = connectToAuthDB();
		String accessQuery = "SELECT * FROM Users where userID = ?";
		PreparedStatement accessStatement = authConn.prepareStatement(accessQuery);
		accessStatement.setString(1, userID);
		ResultSet accessResults = accessStatement.executeQuery();
		if(!accessResults.next()) {
			authConn.close();
			throw new IllegalArgumentException("Invalid username supplied");
		}

		boolean hasAccess = accessResults.getInt("hasSystemAccess") == 1 ? true : false;
		if (!hasAccess) {
			authConn.close();
			return 0;
		}
		authConn.close();

		// Since they do have system access, find their roles via employee ID
		Connection conn = connectToHRDB();
		String staffNoQuery = "Select * From Employee where employeeLogin = ?";
		PreparedStatement pStatement = conn.prepareStatement(staffNoQuery);
		pStatement.setString(1, userID);
		ResultSet results = pStatement.executeQuery();
		String employeeId = null;
		try {
			employeeId = results.getString("employeeId");
			return getHighestRoleByEmployee(employeeId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			conn.close();
		}
	}

	/**
	 * Retrieves the highest possible role a given user can have. This is the
	 * highest job role they have within the company. This is found from
	 * the HR database by searching for either the most recent promotion
	 * record which contains a new role or their initial employment details
	 * @param employeeId Takes the employee's 6 digit Id to search the database
	 * @return An integer representing the highest role the user has
	 * @throws SQLException 
	 */
	public Integer getHighestRoleByEmployee(String employeeId) throws SQLException {
		Connection conn = connectToHRDB();
		// Select only the most recent promotion record for this employee
		String promotionsQuery = "SELECT * FROM Promotion" 
				+ " WHERE employeeId = ?"
				+ " ORDER BY startDate DESC"
				+ " LIMIT 1";
		PreparedStatement promoStatement = conn.prepareStatement(promotionsQuery);
		promoStatement.setString(1, employeeId);
		ResultSet promoResults = promoStatement.executeQuery();

		if(promoResults.next()) { // the query has results
			Integer mostRecentRole = promoResults.getInt("newRole");
			if(mostRecentRole.equals(3)) {
				try {
					String employeeDepartment = getDepartment(employeeId);
					if(employeeDepartment.equalsIgnoreCase("Human Resources")) {
						return 4;
					} else {
						return 3;
					}
				} catch(SQLException e) {
					System.err.println("sql error:" + e.getMessage());
					System.err.println("Deparment assumed to be non-hr");
					return 3;
				} finally {
					conn.close();
				}
			}
		}
		// No promotion records found then..
		// Select their initial role from their InitialEmploymentDetails
		String IEDQuery = "SELECT initialRole"
				+ " FROM InitialEmploymentDetails"
				+ " WHERE employeeId = ?;";

		PreparedStatement IEDStatement = conn.prepareStatement(IEDQuery);
		IEDStatement.setString(1, employeeId);
		ResultSet IEDResults = IEDStatement.executeQuery();
		Integer initialRole = IEDResults.getInt("initialRole");
		conn.close();
		return initialRole;

	}

	/**
	 * Retrieves the given employee's department of work as a String
	 * @param employeeId The employeeId to search for
	 * @return Their department of work or null if it couldn't be retrieved
	 * @throws SQLException When an SQL error occurs during connection/retrieval
	 */
	public String getDepartment(String employeeId) throws SQLException {
		Connection conn = connectToHRDB();
		// Select the department name of this employee
		String deptQuery = "SELECT d.departmentName FROM Employee e JOIN"
				+ " Department d ON e.departmentId AND d.departmentId"
				+ " WHERE e.departmentId = d.departmentId"
				+ " AND e.employeeId = ?";
		PreparedStatement deptStatement = conn.prepareStatement(deptQuery);
		deptStatement.setString(1, employeeId);
		ResultSet deptResult = deptStatement.executeQuery();
		String departmentName = deptResult.getString("departmentName");
		conn.close();
		return departmentName;
	}

	/**
	 * Retrieves the given employee's department of work as an Id
	 * @param employeeId
	 * @return The employee's department ID, 0 if not found
	 * @throws SQLException When an SQL connection/transaction error occurs
	 */
	public int getDepartmentId(String employeeId) throws SQLException {
		Connection conn = connectToHRDB();
		// Select the department name of this employee
		String deptQuery = "SELECT * FROM Employee e JOIN"
				+ " Department d ON e.departmentId AND d.departmentId"
				+ " WHERE e.departmentId = d.departmentId"
				+ " AND e.employeeId = ?";
		PreparedStatement deptStatement = conn.prepareStatement(deptQuery);
		deptStatement.setString(1, employeeId);
		ResultSet deptResult = deptStatement.executeQuery();
		int departmentId = 0;
		departmentId = deptResult.getInt("departmentId");

		conn.close();
		return departmentId;
	}

	/**
	 * Returns the employeeId of the user based on their login information
	 * @param userId The user's login 
	 * @return The employee string, null if not found
	 * @throws SQLException When an SQL connection/transaction error occurs
	 */
	public String getEmployeeIdByLogin(String userId) throws SQLException {
		Connection conn = connectToHRDB();
		String employeeIdQuery = "SELECT * FROM Employee WHERE employeeLogin = ?";
		PreparedStatement employeeIdStatement = conn.prepareStatement(employeeIdQuery);
		employeeIdStatement.setString(1, userId);

		ResultSet employeeIdResult = employeeIdStatement.executeQuery();
		if(employeeIdResult.next()) {
			String employeeId = employeeIdResult.getString("employeeId");
			conn.close();
			return employeeId;
		} else {
			conn.close();
			return null;
		}
	}
	
	/**
	 * Retrieves a record or multiple by employeeId and RecordType
	 * This method returns either a Record object or a List of Record
	 * objects depending on whether the given RecordType can (or does)
	 * have duplicate values or not. I.e an employee can only have
	 * one personal details record so it just returns Record, but if
	 * an employee has multipe promotions it would return a List<Record>
	 * of all of their promotions, allowing the interface to display the
	 * list and let the user select the appropriate record. 
	 * @param employeeId The employeeId whose records to search for
	 * @param recordType The type of record(s) to be found
	 * @return Record when a single record is found, List<Record> for multiple, null otherwise
	 * @throws SQLException When an SQL connection/transactio error occurs
	 */
	public Object retrieveRecord(String employeeId, RecordType recordType) throws SQLException {
		Connection conn = connectToHRDB();
		String targetTable = recordType.name();
		targetTable = targetTable.replace(" ", ""); // Remove spaces, makes it into an SQL-ready table name
		
		String retrieveQry = "SELECT * FROM " + targetTable + " WHERE employeeId=?";
		
		PreparedStatement retrieveStmnt = conn.prepareStatement(retrieveQry);
		retrieveStmnt.setString(1, employeeId);
		
		ResultSet results = retrieveStmnt.executeQuery();
		ArrayList<LinkedHashMap<String, Object>> rows = queryToList(results);
		List<Record> records = new ArrayList<Record>();
		for(LinkedHashMap<String,Object> row : rows) {
			Record current = new Record(recordType, row);
			records.add(current);
		}
		
		try {
			if(records.size() == 1) {
				return records.get(0);
			}
			else if(records.size() > 1) {
				return records;
			} else {
				return null;
			}
		} finally {
			conn.close();
		}
	}


	/**
	 * Used to create a new record after it has been constructed in the GUI
	 * Note: This method assumes the newRecord has been previously validated
	 * by the App Controler before submission!
	 * @param newRecord Record object to be inserted as a new record
	 * @return true if the method was a success, false if it fails
	 * @throws SQLException When an SQL connection/transaction error occurs
	 */
	public boolean createRecord(Record newRecord) throws SQLException {
		Connection conn = connectToHRDB();

		String tableName = newRecord.getRecordType().toString();
		tableName = tableName.replace(" ", ""); // Remove spaces to make it match the DB table names

		List<Column> recordColumns = newRecord.getRecordType().getColumns();

		HashMap<String, Object> recordValues = newRecord.getRecordAsMap();


		if(recordValues != null) {
			String columnNames = "(";
			Iterator<Column> it = recordColumns.iterator();

			while(it.hasNext()) {
				Column currentColumn = it.next();
				if(!currentColumn.isHidden()) {
					columnNames += currentColumn.getName();
					if(it.hasNext()) {
						columnNames += ", ";
					}
				}
			}
			columnNames += ")";
			// Remove trailing commas if they were added when it.hasNext() was true but the next value's isHidden is true
			columnNames = columnNames.replace(", )", ")");

			String valuePlaceholders = "VALUES (";
			for(int i=0; i < recordValues.size(); i++) {
				if(i == 0) {
					valuePlaceholders += "?";
				} else {
					valuePlaceholders += ", ?";
				}
			}
			valuePlaceholders += ");";

			String createQry = "INSERT INTO ";
			createQry += tableName;
			createQry += " ";
			createQry += columnNames;
			createQry += " ";
			createQry += valuePlaceholders;

			PreparedStatement createPStmnt = conn.prepareStatement(createQry);
			int objIndex = 1;
			for(Object currentValue : recordValues.values()) {
				createPStmnt.setObject(objIndex, currentValue);
				createQry = createQry.replaceFirst("[?]", currentValue + "");
				objIndex++;
			}
			boolean qrySuccess = createPStmnt.execute();
			conn.close();
			return qrySuccess;
		}
		conn.close();
		return false;
	}

	/**
	 * Updates any type of record within the database with the given Record's
	 * values. 
	 * Note: This method assumes the newRecord has been previously validated
	 * by the App Controler before submission!
	 * @param newRecord
	 * @return True when the query was succesful, false otherwise
	 * @throws SQLException when an SQL connection/transaction error occurs
	 */
	public boolean updateRecord(Record newRecord) throws SQLException {
		Connection conn = connectToHRDB();

		String targetTable = newRecord.getRecordType().name();
		targetTable = targetTable.replace(" ", "");

		List<Column> recordColumns = newRecord.getRecordType().getColumns();
		LinkedHashMap<String, Object> recordValues = newRecord.getRecordAsMap();

		if(recordValues != null) {
			String setValues = "SET ";
			Iterator<Column> it = recordColumns.iterator();
			while(it.hasNext()) {
				Column currCol = it.next();
				if(!currCol.isHidden() && currCol.isEditable()) { // Column is displayed and editable
					String columnName = currCol.getName();
					setValues += columnName + "=?";
					if(it.hasNext()) {
						setValues += ", ";
					}
				}
			}
			// Remove trailing commas, in the un-implemented case where a final value could be hidden
		
			String whereClause = "";
			switch(newRecord.getRecordType()){
			case Employee:
			case PersonalDetails:
			case InitialEmploymentDetails:
			case Termination:
				whereClause += "WHERE employeeId=?";
				break;
			case Probation:
				whereClause += "WHERE employeeId=? AND startDate=?";
				break;
			case Promotion:
				whereClause += "WHERE promotionId=?";
				break;
			case SalaryIncrease:
				whereClause += "WHERE employeeId=? AND startDate=?";
				break;
			case AnnualReview:
				whereClause += "WHERE employeeId=? and reviewDate=?";  // TODO: Review database definition of performanceReview
				break;
			}
			
			String updateQry = "UPDATE ";
			updateQry += targetTable;
			updateQry += " ";
			updateQry += setValues;
			updateQry += " ";
			updateQry += whereClause;
			
			PreparedStatement updateStmnt = conn.prepareStatement(updateQry);
			it = recordColumns.iterator();
			int objIndex = 1;
			while(it.hasNext()) {
				Column currCol = it.next();
				if(!currCol.isHidden() && currCol.isEditable()) {
					Object currValue = recordValues.get(currCol.getName());
					updateStmnt.setObject(objIndex, currValue);
					objIndex++;
				}
			}
			
			
			int lastPlaceHolderIndex = 0;
			lastPlaceHolderIndex = updateQry.length() - updateQry.replace("?", "").length();
			// Gives an accessible index for the below switch statement to access where clause's placeholder
			switch(newRecord.getRecordType()) {
			case Employee:
			case PersonalDetails:
			case InitialEmploymentDetails:
			case Termination:
				Object employeeId = recordValues.get("employeeId");
				updateStmnt.setObject(lastPlaceHolderIndex, employeeId);
				break;
			case Probation:
			case SalaryIncrease:
				employeeId = recordValues.get("employeeId");
				Object startDate = recordValues.get("startDate");
				updateStmnt.setObject(lastPlaceHolderIndex -1, employeeId);
				updateStmnt.setObject(lastPlaceHolderIndex, startDate);
				break;
			case Promotion:
				Object promoId = recordValues.get("promotionId");
				updateStmnt.setObject(lastPlaceHolderIndex, promoId);
				break;
			case AnnualReview:
				employeeId = recordValues.get("employeeID");
				Object reviewDate = recordValues.get("reviewDate"); // TODO Review DB definition of performance review
				updateStmnt.setObject(lastPlaceHolderIndex-1, employeeId);
				updateStmnt.setObject(lastPlaceHolderIndex, reviewDate);
				break;
			}
			
			boolean qrySuccess = updateStmnt.execute();
			conn.close();
			return qrySuccess;
		}
		conn.close();
		return false;
	}



	/**
	 * Creates a HashMap of the results from a SQL query. This method should be used for queries which only return
	 * one result. For multiple results createList should be used. This method exists at the moment because of the strange
	 * way HashMaps are handled when stored in an ArrayList (will fix).
	 * @param results the ResultSet of the query
	 * @return 
	 * @throws SQLException
	 */
	public LinkedHashMap<String, Object> queryToHashMap(ResultSet results) throws SQLException {
		ResultSetMetaData metaData = results.getMetaData();
		int columns = metaData.getColumnCount();
		LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

		for (int i = 1; i <= columns; i++) {
			resultMap.put(metaData.getColumnName(i) + "", results.getObject(i));
		}

		return resultMap;
	}

	/**
	 * Creates an ArrayList containing HashMaps of all the results from an SQL query.
	 * Queries which return only one result should use createHashmap
	 * @param results the ResultSet of the query
	 * @return ArrayList an ArrayList containing HashMaps for each row of results from the SQL query
	 * @throws SQLException When an SQL error occurs during transaction(s)
	 */
	public ArrayList<LinkedHashMap<String, Object>> queryToList(ResultSet results) throws SQLException {
		ResultSetMetaData metaData = results.getMetaData();
		int columns = metaData.getColumnCount();
		ArrayList<LinkedHashMap<String,Object>> list = new ArrayList<LinkedHashMap<String,Object>>();

		while (results.next()) {
			LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(columns);
			for (int i = 1; i <= columns; i++) {
				resultMap.put(metaData.getColumnName(i) + "", results.getObject(i));
			}
			list.add(resultMap);
		}

		return list;
	}


	/**
	 * Checks if a permission exists for the given role level
	 * @authRole The role level that should be searched
	 * @permission String - The permission to check for
	 * @return true if the permission is found, false otherwise
	 * @throws SQLException 
	 */
	public boolean checkPermissionByRole(Role authRole, String permission) throws SQLException {
		Connection conn = connectToAuthDB();
		String roleQuery = "SELECT * FROM PermissionsByRole WHERE permName = ? AND roleLevel = ?";
		PreparedStatement rolePermStatement = conn.prepareStatement(roleQuery);
		rolePermStatement.setString(1, permission);
		rolePermStatement.setInt(2, authRole.id());
		ResultSet rolePermResults = rolePermStatement.executeQuery();

		try { // Used purely to 100% close out the connection on each code path
			if(rolePermResults.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			conn.close();
		}
	}

	/**
	 * Checks if a permission exists for a given department
	 * This allows departments to be granted to an entire department,
	 * for example the HR department needs grants for all of their roles
	 * @param departmentId The department that needs to be checked for the permission
	 * @param permission The permission that is being checked for
	 * @return True if the permission is found, false otherwise
	 * @throws SQLException When an SQL connection/transaction error occurs
	 */
	public boolean checkPermissionByDepartment(int departmentId, String permission) throws SQLException {
		Connection conn = connectToAuthDB();
		String deptQuery = "SELECT * FROM PermissionsByDepartment WHERE permName = ? AND departmentId = ?";
		PreparedStatement deptPermStatement = conn.prepareStatement(deptQuery);
		deptPermStatement.setString(1, permission);
		deptPermStatement.setInt(2, departmentId);
		ResultSet deptPermResults = deptPermStatement.executeQuery();

		try { // Used purely to 100% close out the connection on each code path
			if(deptPermResults.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			conn.close();
		}
	}

	/**
	 * Checks if a permission exists for a given user
	 * This enables individual users to have special permissions
	 * @param userId The user which should be checked for the permission
	 * @param permission The permission which should be looked for
	 * @return true if the permission is found for the user, false otherwise
	 * @throws SQLException When an SQL connection/transaction error occurs
	 */
	public boolean checkPermissionByUser(String userId, String permission) throws SQLException {
		Connection conn = connectToAuthDB();
		String userQuery = "SELECT * FROM PermissionsByUser WHERE permName = ? AND userId = ?";
		PreparedStatement userPermStatement = conn.prepareStatement(userQuery);
		userPermStatement.setString(1, permission);
		userPermStatement.setString(2, userId);
		ResultSet userPermResults = userPermStatement.executeQuery();

		try { // Used purely to 100% close out the connection on each code path
			if(userPermResults.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			conn.close();
		}
	}
}
