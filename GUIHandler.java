package yuconz23d.GUI;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import yuconz23d.ActionType;
import yuconz23d.Column;
import yuconz23d.Record;
import yuconz23d.RecordType;
import yuconz23d.Role;
import yuconz23d.YuconzApp;

/**
 * 
 * Provides the top-level handling of the GUI, this uses
 * a CardLayout to host a number of sub-GUIs as 'cards' 
 * which can be switched between. These sub-GUIs are 
 * simply JPanels constructed by their respective classes. 
 *
 */
public class GUIHandler {
	private YuconzApp app;
	private JFrame frame;
	private JPanel cards;
	private CardLayout cardLayout;
	private JPanel idForm;
	private JPanel passwordForm;
	private JPanel logoutWindow;
	private JPanel recordControlPanel;
	private JPanel personalDetailsCP; // CP = Control Panel
	private JPanel promotionCP;
	private JPanel probationCP;
	private JPanel terminationCP;
	private JPanel salaryIncreaseCP;
	private JPanel initialEmploymentDetailsCP;
	private JPanel annualReviewCP;

	/**
	 * Constructs the GUIHandler class by initialising it's reference
	 * to the YuconzApp class
	 */
	public GUIHandler() {
		this.app = new YuconzApp();
	}

	/**
	 * Run the actual GUI to start the program
	 * @param args arguments for the main function - currently unused
	 */
	public static void main(String[] args) {
		GUIHandler g = new GUIHandler();
		g.buildAndShow();
	}

	/**
	 * Builds the entire GUI including all cards
	 * which is essentially the entire app's GUI
	 * all in one layout. 
	 * 
	 * Note: This should not be called from the main
	 * thread!
	 */
	public void buildAndShow() {
		frame = new JFrame("Yuconz Authentication Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cards = new JPanel(new CardLayout());
		cardLayout = (CardLayout) cards.getLayout();

		cards.add("usernameForm", constructEmployeeIdForm());
		cards.add("passwordForm", constructEmployeePasswordForm());
		cards.add("recordControlPanel", constructRecordControlPanel());

		frame.add(cards);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Constructs the GUI for the employee Id 
	 * entrance form. Allowing it to be added
	 * to the cardLayout as a GUI that can be 
	 * switched to.
	 * 
	 * @return JPanel The JPanel that contains the entire Employee ID Form
	 */
	public JPanel constructEmployeeIdForm() {
		idForm = new JPanel();
		idForm.setLayout(new BoxLayout(idForm, BoxLayout.Y_AXIS));
		

		JLabel idPrompt = new JLabel("Enter Employee Id");
		Font labelFont = new Font("Serif", Font.BOLD, 22);
		idPrompt.setFont(labelFont);
		idPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton userSubmit = new JButton("Submit");

		JTextField idField = new JTextField(6);
		idField.setName("idField");
		idField.setMaximumSize(new Dimension(178, 46));
		Font textfieldFont = new Font("Sans-serif", Font.BOLD, 30);
		idField.setFont(textfieldFont);
		idField.setHorizontalAlignment(SwingConstants.CENTER);
		idField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					userSubmit.doClick();
					e.consume();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {
				
				Pattern pattern = Pattern.compile("[0-9a-zA-Z]");
				Matcher matcher = pattern.matcher(e.getKeyChar() + ""); // Prepare regex detection

				String currentText = idField.getText();
				String highlightedText = idField.getSelectedText();
				boolean fieldFull = currentText.length() >= 6;
				boolean fieldHighlighted = highlightedText != null;
				/* Checking highlighted text allows users to still type in the ID
				 * field if they have highlighted some characters to replace,
				 * especially when the field is marked as full and would have
				 * previously blocked them from new keypresses */

				if(!matcher.matches() || fieldFull && !fieldHighlighted) {
					e.consume();
				}
			}

		});

		//JButton userSubmit = new JButton("Submit"); - Moved
		userSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		userSubmit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String givenUserID = idField.getText().toLowerCase();
				if(givenUserID.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "You must enter a User ID to submit!");
				} else {
					String[] availableRoles = app.submitUsername(givenUserID);
					if(availableRoles == null) {
						JOptionPane.showMessageDialog(frame, "The User ID provided was invalid!");
					} 
					else if(availableRoles[0].equals("Suspended")) {
						JOptionPane.showMessageDialog(frame, "The user ID provided has been suspended from accessing the system.");
					} else {
						userSubmit.setText("Loading...");
						Component[] passwordComponents = passwordForm.getComponents();
						for(Component currentComp : passwordComponents) {
							if(currentComp.getName() != null) {
								if(currentComp.getName().equals("userIdLabel")) {
									JLabel userLabel = (JLabel)currentComp;
									userLabel.setText(givenUserID);
								}
								else if(currentComp.getName().equals("levelSelect")) {
									@SuppressWarnings("unchecked") // Suppressed since it's 100% going to be a JCB
									JComboBox<String> levelComboBox = (JComboBox<String>)currentComp;
									levelComboBox.removeAllItems();
									for(String role : availableRoles) {
										levelComboBox.addItem(role);
									}
								}
							}
						}
						cardLayout.show(cards, "passwordForm");
						userSubmit.setText("Submit");
					}
				}
			}
		});
		
		idForm.add(idPrompt);
		idForm.add(Box.createRigidArea(new Dimension(0,20)));
		idForm.add(idField);
		idForm.add(Box.createRigidArea(new Dimension(0,20)));
		idForm.add(userSubmit);
		idForm.add(Box.createRigidArea(new Dimension(0,20)));
		idForm.setBorder(new EmptyBorder(100,100,100,100));
		return idForm;
	}

	/**
	 * Constructs the JPanel for the GUI of the
	 * Employee's password form, which allows for
	 * password entry as well as access-level 
	 * selection based on their available roles.
	 * @return JPanel containing the employeePasswordForm (default values until updated)
	 */
	public JPanel constructEmployeePasswordForm() {
		passwordForm = new JPanel();
		passwordForm.setLayout(new BoxLayout(passwordForm, BoxLayout.Y_AXIS));
		Font idFont = new Font("Sans-serif", Font.PLAIN, 22);
		Font promptFont = new Font("Serif", Font.BOLD, 20);

		JLabel userId = new JLabel("######");
		userId.setName("userIdLabel");
		userId.setFont(idFont);
		userId.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel levelPrompt = new JLabel("Select access level");
		levelPrompt.setFont(promptFont);
		levelPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

		String[] roles = { "Employee" };
		JComboBox<String> levelSelect = new JComboBox<String>(roles);
		levelSelect.setMaximumSize(new Dimension(178, 30));
		levelSelect.setName("levelSelect");

		JLabel passwordPrompt = new JLabel("Enter your password");
		passwordPrompt.setFont(promptFont);
		passwordPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton passwordSubmit = new JButton("Login");

		JPasswordField passwordField = new JPasswordField(15);
		passwordField.setName("passwordField");
		passwordField.setMaximumSize(new Dimension(178, 30));
		passwordField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				passwordSubmit.doClick();
			}
		});

		//JButton passwordSubmit = new JButton("Login"); - Moved for ref.
		passwordSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordSubmit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String userID = userId.getText();
				char[] passwordAsArray = passwordField.getPassword();
				String password = String.valueOf(passwordAsArray);
				if(password.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "You must type a password!");
				} else {
					String selected = (String) levelSelect.getSelectedItem();
					int roleID = Role.idOf(selected);

					boolean loginSuccess = app.submitLogin(userID, password, roleID);
					if(!loginSuccess) {
						JOptionPane.showMessageDialog(frame, "The password given was invalid!");
					} else {
						Component[] sampleComponents = logoutWindow.getComponents();
						for(Component currentComp : sampleComponents) {
							if(currentComp.getName() != null) {
								if(currentComp.getName().equals("loggedInAs")) {
									JLabel loggedInAs = (JLabel)currentComp;
									loggedInAs.setText("You are logged in as " + userID + "(" + selected + " access level)");
								}
							}
						}
						cardLayout.show(cards, "recordControlPanel");
					}
				}
			}
		});


		passwordForm.add(userId);
		passwordForm.add(Box.createRigidArea(new Dimension(0,20)));
		passwordForm.add(levelPrompt);
		passwordForm.add(Box.createRigidArea(new Dimension(0,5)));
		passwordForm.add(levelSelect);
		passwordForm.add(Box.createRigidArea(new Dimension(0,20)));
		passwordForm.add(passwordPrompt);
		passwordForm.add(Box.createRigidArea(new Dimension(0,5)));
		passwordForm.add(passwordField);
		passwordForm.add(Box.createRigidArea(new Dimension(0,20)));
		passwordForm.add(passwordSubmit);
		passwordForm.setBorder(new EmptyBorder(100,100,100,100));

		return passwordForm;
	}
	
	/**
	 * Constructs the Record Control Panel element of the GUI
	 * This acts as the main window for all record creation/modification
	 * With individual panels acting as each different tab
	 * @return a JPanel containing the record control panel GUI
	 */
	public JPanel constructRecordControlPanel() {
		recordControlPanel = new JPanel();

		JTabbedPane controlPanelTabs = new JTabbedPane();
		controlPanelTabs.setPreferredSize(new Dimension(1000, 750));
		
		personalDetailsCP = constructCP(RecordType.PersonalDetails);
		initialEmploymentDetailsCP = constructCP(RecordType.InitialEmploymentDetails);
		promotionCP = constructCP(RecordType.Promotion);
		salaryIncreaseCP = constructCP(RecordType.SalaryIncrease);
		probationCP = constructCP(RecordType.Probation);
		terminationCP = constructCP(RecordType.Termination);
		annualReviewCP = constructCP(RecordType.AnnualReview);
		logoutWindow = constructLogoutWindow();
		controlPanelTabs.addTab("Personal Details", personalDetailsCP);
		controlPanelTabs.addTab("Initial Employment Details", initialEmploymentDetailsCP);
		controlPanelTabs.addTab("Promotions", promotionCP);
		controlPanelTabs.addTab("Salary Increase", salaryIncreaseCP);
		controlPanelTabs.addTab("Probations", probationCP);
		controlPanelTabs.addTab("Terminations", terminationCP);
		controlPanelTabs.addTab("Annual Reviews", annualReviewCP);
		controlPanelTabs.addTab("Logout", logoutWindow);
		
		recordControlPanel.add(controlPanelTabs);
		
		return recordControlPanel;
	}
	
	
	/**
	 * Constructs any Record's Control Panel based on the given RecordType
	 * This enables automatic generation of the GUI control panels seeing as
	 * singular definitions are very repetitive in their exhaustive form. 
	 * @param recordType The type of record to construct a control panel for
	 * @return The constructed Control Panel as a JPanel
	 */
	public JPanel constructCP(RecordType recordType) {
		LinkedHashMap<String, Component> inputFields = new LinkedHashMap<>(); // stores references to all input fields
		
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		String panelName = recordType.name();
		panelName = panelName.replace(" ", "");
		panelName += "Panel";
		resultPanel.setName(panelName);
		
		JPanel menu = new JPanel();
		menu.setMaximumSize(new Dimension(1000, 150));
		JPanel form = new JPanel();
		JPanel saveMenu = new JPanel();
		
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		form.setMaximumSize(new Dimension(800, recordType.getColumns().size() * 50));
		
		
		for(Column curr : recordType.getColumns()) {
			String inputName = curr.getName();
			inputName += "Input";
			
			JPanel row = new JPanel();
			row.setLayout(new GridLayout(1,2,10,10));
			
			JLabel prompt = new JLabel(curr.getDisplayText());
			prompt.setHorizontalAlignment(SwingConstants.RIGHT);

			switch(curr.getName()) {
			case "CV":
				JTextArea cvInput = new JTextArea();
				cvInput.setPreferredSize(new Dimension(prompt.getWidth(), 200));
				cvInput.setName(inputName);
				cvInput.setVisible(!curr.isHidden());
				prompt.setVisible(!curr.isHidden());
				cvInput.setEditable(false);
				row.add(prompt);
				row.add(cvInput);
				row.setName("row");
				inputFields.put(curr.getName(), cvInput);
				break;
			default:
				JTextField input = new JTextField();
				input.setText("...");
				input.setName(inputName);
				input.setVisible(!curr.isHidden());
				prompt.setVisible(!curr.isHidden());
				input.setEditable(false);
				row.add(prompt);
				row.add(input);
				row.setName("row");
				inputFields.put(curr.getName(), input);
				break;
			}
			form.add(row);
			form.add(Box.createRigidArea(new Dimension(10,10)));
		}
		
		JButton saveButton = new JButton("Save"); // Declared above since it's name is used to store create/modify flags
		
		JButton loadButton = new JButton("View");
		loadButton.setToolTipText("Load a " + recordType.name() + " record into view");
		loadButton.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String targetEmployee = (String) JOptionPane.showInputDialog(
						frame,
						"Please enter the ID of the Employee which owns \n"
						+ "the " + recordType.name() + " record you wish to view.",
						"Employee Selection",
						JOptionPane.PLAIN_MESSAGE,
						null,null,null);
				if(targetEmployee == null) {
					return;
				}
				LinkedHashMap<String, Object> placeHolder = new LinkedHashMap<>();
				placeHolder.put("employeeId", targetEmployee);
				Record permissionCheckPlaceholder = new Record(recordType, placeHolder);
				boolean hasViewPermission = app.checkPermission(
						app.getCurrentUser(),
						app.getCurrentEmployee(), 
						app.getCurrentRole(),
						ActionType.VIEW,
						permissionCheckPlaceholder);
				if(hasViewPermission) {
					Object viewResult = null;
					try {
						viewResult = app.viewRecord(recordType, targetEmployee);
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(frame, "An error occured when retrieving the record!");
						e1.printStackTrace();
					}
					
					if (viewResult instanceof List<?>) {
						List<Record> records = (List<Record>) viewResult;
						Object options[] = new Object[records.size()];
						for(int i=0; i < records.size(); i++) {
							LinkedHashMap<String, Object> columns = records.get(i).getRecordAsMap();
							String option = "";
							for(Entry<String, Object> entry : columns.entrySet()) {
								String currKey = entry.getKey();
								Object currVal = entry.getValue();
								option += currKey + ": ";
								option += currVal;
								option += "  |  ";
							}
							options[i] = option;
						}
						String choice = (String)JOptionPane.showInputDialog(
								frame,
								"Multiple records found!"
								+ "\n\n Please pick the one which you want to view:",
								"Record Selection",
								JOptionPane.PLAIN_MESSAGE,
								null,
								options,
								options[0]);
						
						int chosenIndex = 0;
						for(int i=0; i < records.size(); i++) {
							if(options[i].equals(choice)) {
								chosenIndex = i;
								viewResult = records.get(chosenIndex);
								break;
							}
						}
					}
					
					if(viewResult instanceof Record) {
						for(Component currField : inputFields.values()) {
							if(currField instanceof JTextField) {
								((JTextField) currField).setEditable(false);
							}
							else if(currField instanceof JTextArea) {
								((JTextArea) currField).setEditable(false);
							}
						}
						HashMap<String, Object> recordValues = ((Record) viewResult).getRecordAsMap();
						for(Entry<String, Object> curr : recordValues.entrySet()) {
							String currKey = curr.getKey();
							Object currValue = curr.getValue();
							Component currField = inputFields.get(currKey);
							if(currField instanceof JTextField) {
								((JTextField) currField).setText(currValue + "");
							}
							else if(currField instanceof JTextArea) {
								((JTextArea) currField).setText(currValue + "");
							}
						}
					} else {
						JOptionPane.showMessageDialog(frame, "No " + recordType.name() + " record(s) found for this employee ID");
					}
				} else {
					JOptionPane.showMessageDialog(frame, "You don't have permission to view this " + recordType.name() + " record");
				}
			}
			
		});
		
		
		
		JButton modifyButton = new JButton("Modify");
		modifyButton.setToolTipText("Modify the currently loaded record");
		modifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inputFields.get("employeeId") instanceof JTextField) {
					JTextField employeeField = (JTextField) inputFields.get("employeeId");
					String targetEmployee = employeeField.getText();
					if(targetEmployee == null || targetEmployee.equals("...") || targetEmployee.isEmpty()) {
						JOptionPane.showMessageDialog(frame, "You must load a record to modify first!");
					} else {
						LinkedHashMap<String, Object> placeHolder = new LinkedHashMap<>();
						placeHolder.put("employeeId", targetEmployee);
						Record permissionCheckPlaceholder = new Record(recordType, placeHolder);
						boolean hasModifyPermission = app.checkPermission(
								app.getCurrentUser(),
								app.getCurrentEmployee(), 
								app.getCurrentRole(),
								ActionType.MODIFY,
								permissionCheckPlaceholder);
						if(hasModifyPermission) {
							saveButton.setName("modify");
							int i = 0;
							for(Component currComp : inputFields.values()) {
								Column currColumn = recordType.getColumns().get(i);
								if(currComp instanceof JTextField) {
									((JTextField) currComp).setEditable(currColumn.isEditable());
								}
								else if(currComp instanceof JTextArea) {
									((JTextArea) currComp).setEditable(currColumn.isEditable());
								}
								i++;
							}
						} else {
							JOptionPane.showMessageDialog(frame, "You don't have permission to modify this employee's " + recordType.name() + " record(s)");
						}
					}
				}
				
			}
		});
		
		
		JButton createButton = new JButton("Create");
		createButton.setToolTipText("Begin the creation of a new " + recordType.name() + " record");
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Record permissionCheckPlaceholder = new Record(recordType, null);
				boolean hasCreatePermission = app.checkPermission(
						app.getCurrentUser(),
						app.getCurrentEmployee(),
						app.getCurrentRole(),
						ActionType.CREATE,
						permissionCheckPlaceholder);
				if(hasCreatePermission) {
					saveButton.setName("create"); // Flag the last action as create
					for(Component currComp : inputFields.values()) {
						if(currComp instanceof JTextField) {
							((JTextField) currComp).setText("");
							if(currComp.isVisible()) {
								((JTextField) currComp).setEditable(true);
							}
						}
						else if(currComp instanceof JTextArea) {
							((JTextArea) currComp).setText("");
							if(currComp.isVisible()) {
								((JTextArea) currComp).setEditable(true);
							}
						}
					}
				} else {
					JOptionPane.showMessageDialog(frame, "You don't have permission to create a " + recordType.name() + " record!");
				}
			}
		});
		
		
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkedHashMap<String, Object> recordMap = new LinkedHashMap<>();
				List<Column> recordColumns = recordType.getColumns();
				for(int i=0; i < recordColumns.size(); i++) {
					String columnName = recordColumns.get(i).getName();
					Object inputComponent = inputFields.get(columnName);
					Object columnValue = null;
					if(inputComponent instanceof JTextField) {
						columnValue = ((JTextField) inputComponent).getText();
					}
					else if(inputComponent instanceof JTextArea) {
						columnValue = ((JTextArea) inputComponent).getText();
					}
					if(!recordColumns.get(i).isHidden()) {
						recordMap.put(columnName, columnValue);
					}
				}
				Record saveRecord = new Record(recordType, recordMap);
				List<String> errors = app.validateRecord(saveRecord);
				
				if(errors == null) {
					JOptionPane.showMessageDialog(frame, "An error occured when validating the record");
				}
				else if(!errors.isEmpty()) {
					String errorOutput = "You must fix the following errors before saving: \n\n";
					for(String currentError : errors) {
						errorOutput += " - " + currentError + "\n";
					}
					JOptionPane.showMessageDialog(frame, errorOutput);
				}
				else { // Record is valid
					if(saveButton.getName().equals("create")) {
						try {
							app.createRecord(saveRecord);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(frame, "An error occured whilst attempting to create the record");
							e1.printStackTrace();
						}
					}
					else if(saveButton.getName().equals("modify")) {
						try {
							app.updateRecord(saveRecord);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(frame, "An error occured whilst attempting to update the record");
							e1.printStackTrace();
						}
					}
					
					for(Component currComp : inputFields.values()) {
						if(currComp instanceof JTextField) {
							if(currComp.isVisible()) {
								((JTextField) currComp).setEditable(false);
							}
						}
						else if(currComp instanceof JTextArea) {
							((JTextArea) currComp).setText("");
							if(currComp.isVisible()) {
								((JTextArea) currComp).setEditable(false);
							}
						}
					}
				}
			}
		});
		
		menu.add(loadButton);
		menu.add(Box.createRigidArea(new Dimension(30, 0)));
		menu.add(modifyButton);
		menu.add(Box.createRigidArea(new Dimension(30, 0)));
		menu.add(createButton);
		
		
		saveMenu.add(saveButton);
		
		resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		resultPanel.add(menu);
		resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		resultPanel.add(form);
		resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		resultPanel.add(saveMenu);
		resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		
		return resultPanel;
	}

	/**
	 * Constructs the JPanel of a sampleWindow which is used for demonstration purposes.
	 * This window is used as part of the authentication sample that Yuconz requested and
	 * is purely for the purpose of creating a placeholder for a real interface which
	 * would hold the logout button for the user. 
	 * @return the constructed JPanel of this sampleWindow
	 */
	public JPanel constructLogoutWindow() {
		logoutWindow = new JPanel();
		logoutWindow.setLayout(new BoxLayout(logoutWindow, BoxLayout.Y_AXIS));

		JTextArea sampleExplanation = new JTextArea("This window is built as part of the authentication demo only,"
				+ " it plays no purpose in our actual system but exists to"
				+ " demonstrate the logout functionality that would normally"
				+ " be displayed within a regular window of the application.");
		sampleExplanation.setWrapStyleWord(true);
		sampleExplanation.setLineWrap(true);
		sampleExplanation.setMaximumSize(new Dimension(178, 200));
		sampleExplanation.setEditable(false);
		sampleExplanation.setBackground(frame.getBackground());

		JLabel loggedInAs = new JLabel("You are logged in as ...");
		loggedInAs.setName("loggedInAs");
		loggedInAs.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton logoutButton = new JButton("Logout");
		logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.logout();
				rebuild();
				cardLayout.show(cards, "usernameForm");
				JOptionPane.showMessageDialog(frame, "You have been logged out succesfully");
			}
		});

		//sampleWindow.add(sampleExplanation);
		//passwordForm.add(Box.createRigidArea(new Dimension(0,25)));
		logoutWindow.add(loggedInAs);
		passwordForm.add(Box.createRigidArea(new Dimension(0,5)));
		logoutWindow.add(logoutButton);
		logoutWindow.setBorder(new EmptyBorder(50,50,50,50));
		return logoutWindow;
	}
	
	/**
	 * Rebuild the interface to clear fields in each form
	 */
	public void rebuild() {
		cards.removeAll();
		recordControlPanel = constructRecordControlPanel();
		cards.add("usernameForm", constructEmployeeIdForm());
		cards.add("passwordForm", constructEmployeePasswordForm());
		cards.add("recordControlPanel", recordControlPanel);
	}

}
