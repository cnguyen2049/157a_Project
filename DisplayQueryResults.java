/**
 * Car Service Reservation System.
 * GUI that implements a car reservation for admin to modify connected
 * database or user to rent/return/cancel reservations as well as look up or
 * modify their information.
 * Author Deitel & Associates, Inc. Copyright information below.
 * Modified By Alex Ma, Erik Macias, and Chris Nguyen. 
 * CS 157A - Introduction to Database.
 * Section 1 9am - 10:30am
 * November 26, 2013
 * Database Project.
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;

public class DisplayQueryResults extends JFrame implements ActionListener, ItemListener
{
	// JDBC driver, database URL, username and password
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://localhost/car_rental";
	static final String USERNAME = "root";
	static final String PASSWORD = "";

	// Default query retrieves all cars from the database, entice the user.
	static final String DEFAULT_QUERY = "SELECT Make, Model, Year, Color, " +
			"Price FROM Car, Car_Spec WHERE " +
			"Car.LicensePlate = Car_spec.LicensePlate;";

	static final int DOWN_PAYMENT = 100;	// Down payment made by user.
	
	private ResultSetTableModel tableModel;	//Displays results.

	private JPanel adminPanel;	// Content for Administrator.
	private JPanel userPanel;	// Content for Users.
	private JPanel defaultPanel;	// Default content.
	private JPanel signInPanel;	// Sign in panel.
	private JPanel registerPanel;	// Collect data from new users.
	private JPanel tablePanel;	// Hold data table.
	private JPanel optionPanel;	// Hold panel for various car options.

	private JPanel contentPanel;	// Holds JButtons.
	private JPanel buttonPanel;	// Card Layout Panel for buttons.
	private JPanel optionsPanel;	// Hold car selection options.
	private String panelNames[] = new String[9];	// Holds the panel names.

	private JPanel textPanel;	// Holds JLabels.  

	private JLabel formLabel;	// Holds registration form.

	private JLabel adminColorLabel = new JLabel("Color: ");
	private JLabel adminYearLabel = new JLabel("Year: ");
	private JLabel adminMPGLabel = new JLabel("MPG: ");
	private JLabel admin2_4DoorLabel = new JLabel("2/4 Door: ");
	private JLabel adminArchiveLabel = new JLabel("Choose Archiving Table.");

	private JLabel userAccidentLabel = new JLabel("Report Accident?");

	// Buttons for administrator tasks. 
	private JButton adminRemoveCarButton;
	private JButton adminAddCarButton;
	private JButton adminViewCarsButton;
	private JButton adminCarAddSubmitButton;
	private JButton adminCarRemoveSubmitButton;
	private JButton adminCarRemoveCancelButton;
	private JButton adminCarCancelButton;
	private JButton adminCarColorButton;
	private JButton adminCarYearButton;
	private JButton adminCarMPGButton;
	private JButton adminCar2_4Button;
	private JButton adminArchiveButton;

	// Used for archiving.
	private JComboBox tables;	// Archiving tables.
	private JTextField lastModifiedDate;	// Used with stored archiving procedure.

	// Buttons for user tasks.
	private JButton userRentButton;
	private JButton userReturnButton;
	private JButton userReturnThisCarButton;
	private JButton userViewButton;
	private JButton userCustomerInfoButton;
	private JButton userAccountButton;
	private JButton cancelReservationButton;

	// Customer Rent car option buttons. 
	private JButton userColorButton;
	private JButton userMPGButton;
	private JButton userDefaultButton;
	private JButton userTwoFourDoorButton;
	private JButton userCurrentMilesButton;
	private JButton userPriceButton;
	private JButton userRatingButton;
	private JButton rentACarButton;
	private JButton userRequestCarButton;

	// Buttons for default tasks. 
	private JButton defaultRentButton;
	private JButton defaultRegisterButton;	// Used to take user to
	private JButton defaultSubmitButton;	// registration screen. Part of 
	private JButton defaultCancelButton;	// banner.

	// Sign-in screen buttons.
	private JButton loginButton;
	private JButton cancelButton;

	// Button to register (part of regiserPanel, from registerScreen() method).
	private JButton registerButton;	// Used to take user to register screen.
	// Part of the "lower" panels screen.
	// Sign In/Out toggle button.
	private JToggleButton signInButton;

	// Dimensions for button spacing.
	Dimension minSize = new Dimension(10, 10);
	Dimension prefSize = new Dimension(10, 10);
	Dimension maxSize = new Dimension(Short.MAX_VALUE, 50);

	// Car information fields.
	private JTextField carLicensePlateField;
	private JTextField adminAddCarLicensePlateField;
	private JTextField carModelField;
	private JTextField carMakeField;
	private JTextField carYearField;

	// Car_Spec information.
	private JTextField carMPGField;
	private JTextField car2_4DoorField;
	private JTextField carColorField;
	private JTextField carCurrentMilesField;
	private JTextField carPriceField;

	// List of tables archived in database.
	String[] tables_arr = {"Customer", "Rental", "Returned"};
	// List of different available car colors.
	String[] colors_arr = addToArray("select distinct color from car_spec");
	// List of different available car MPGs.
	String[] MPG_arr = addToArray("select distinct mpg from car_spec");
	// List of different values of different car's current mileage.
	String[] currentMiles_arr = addToArray("select distinct currentmiles from car_spec");
	// List of 2 or 4 Door option.
	String[] twoFour_arr = {"2", "4"};
	// List of car's License Plates matching user specifications.
	String[] licensePlates_arr;
	// List of different amount of days to rent (1-31).
	int rentSpan = 31;
	String[] daysRent_arr = new String[rentSpan];
	// List of different car years (2014 - 1990).
	int yearSpan = 25;
	String[] year_arr = new String[yearSpan];
	// List of different car MPG values (5-49).
	int mpgSpan = 45;
	String[] MPGList_arr = new String[mpgSpan];

	// Customer return
	JPanel accidentPanel = new JPanel();
	JTextField accidentCommentField = new JTextField(10);
	String accidentComment;
	JButton YesButton = new JButton("Yes");
	JButton NoButton = new JButton("No");
	int accident; // if 1, accident = true. if 0, accident = false.

	// Give Car a rating.
	JRadioButton buttonOne = new JRadioButton("1");
	JRadioButton buttonTwo = new JRadioButton("2");
	JRadioButton buttonThree = new JRadioButton("3");
	JRadioButton buttonFour = new JRadioButton("4");
	JRadioButton buttonFive = new JRadioButton("5");

	// Extend the due date.
	JButton ExtendButton = new JButton("Extend DueDate");
	int ratingSelected;

	// Options for admin to select when inserting new cars.
	private JComboBox twoFourList;
	private JComboBox colorsList;
	private JComboBox adminYearList;
	private JComboBox adminMPGList;
	private JComboBox priceList;
	private JComboBox MPGList;
	private JComboBox currentMilesList;

	// Customer information fields.
	private JTextField fullNameField;
	private JTextField driversLicenseField;
	private JTextField creditCardField;
	private JTextField pickUpDateField;
	private JTextField ageField;
	private JTextField userNameField;

	// Password.
	private JPasswordField loginField;

	// Car information.
	private String carLicensePlate = null;
	private String carMake = null;
	private String carModel = null;
	private String carYear = null;
	private String carMPG = null;
	private String car2_4Door = null;
	private String carColor = null;
	private String carCurrentMiles = null;
	private String carPrice = null;

	// Customer information. 
	private String fullName = null;
	private String driversLicense = null;
	private String creditCard = null;
	private String pickUpDate = null;
	private String age = null;
	private String dateRented = "CURDATE()";
	private String dueDate = null;
	private String daysRented = null;
	private String updatedAt = "NOW()";
	private boolean rentTo = false;
	private boolean isNew = true;
	private boolean rented = false;

	private String query = "Select licenseplate from car_spec natural join car" +
			" where CAR.LicensePlate NOT IN " +
			"(Select LicensePlate From Rental)";

	private JPanel mainGUI; // Panel to hold entire GUI.

	// create ResultSetTableModel and GUI
	public DisplayQueryResults() 
	{   
		super( "ACE Car Rental Service" );

		/** JPanel to hold all content. */
		mainGUI = new JPanel();
		mainGUI.setBackground(Color.red);
		mainGUI.setLayout(new BorderLayout(10,10));

		/** Holds the content at the top of the page. */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(10, 10));
		topPanel.setPreferredSize(new Dimension(mainGUI.getX(), 110));
		mainGUI.add(topPanel, BorderLayout.NORTH);

		// Holds the buttons on the top of the page.
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		topPanel.add(buttons, BorderLayout.NORTH);

		// Sign in/out button.
		signInButton = new JToggleButton("Sign In");
		signInButton.setMinimumSize(new Dimension(90, 30));
		signInButton.setPreferredSize(new Dimension(90, 30));
		signInButton.addItemListener(this);
		buttons.add(signInButton);

		buttonPanel = new JPanel(new CardLayout(0, 
				0));
		topPanel.add(buttonPanel, BorderLayout.SOUTH);

		contentPanel = new JPanel(new CardLayout(mainGUI.getX(), 
				mainGUI.getY()-topPanel.getY()));
		mainGUI.add(contentPanel, BorderLayout.CENTER);

		panelNames[0] = "Default Buttons";
		panelNames[1] = "Admin Buttons";
		panelNames[2] = "User Buttons";
		panelNames[3] = "Top Sign in Screen";
		panelNames[4] = "Table Screen";
		panelNames[5] = "Register Screen";
		panelNames[6] = "Admin Add Car Screen";
		panelNames[7] = "Admin Remove Car Screen";
		panelNames[8] = "Bottom Sign in Screen";


		buttonPanel.add(defaultBanner(), panelNames[0]);
		buttonPanel.add(adminBanner(), panelNames[1]);
		buttonPanel.add(userBanner(), panelNames[2]); 
		buttonPanel.add(signInBanner(), panelNames[3]);

		contentPanel.add(tableScreen(), panelNames[4]);
		contentPanel.add(registerScreen(), panelNames[5]);
		contentPanel.add(adminAddCarScreen(), panelNames[6]);
		contentPanel.add(adminRemoveCarScreen(), panelNames[7]);
		contentPanel.add(signInScreen(), panelNames[8]);

		// List of different lengths to rent for (1-31).
		for (int i = 0; i < rentSpan; i++)
		{
			daysRent_arr[i] = Integer.toString(i + 1);
		}
		// List of different car years (1985 - 2014).
		int startYear = 2014;
		for (int i = 0; i < yearSpan; i++)
		{
			year_arr[i] = Integer.toString(startYear - i);
		}

		// List of different car MPG values (5-49).
		int startMPG = 5;
		for (int i = 0; i < mpgSpan; i++)
		{
			MPGList_arr[i] = Integer.toString(startMPG + i);
		}

		add(mainGUI);
		setSize(800, 600);
		setVisible(true);

		// dispose of window when user quits application (this overrides
		// the default of HIDE_ON_CLOSE)
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		// ensure database connection is closed when user quits application
		addWindowListener(

				new WindowAdapter() 
				{
					// disconnect from database and exit when window has closed
					public void windowClosed( WindowEvent event )
					{
						tableModel.disconnectFromDatabase();
						System.exit( 0 );
					} // end method windowClosed
				} // end WindowAdapter inner class
				); // end call to addWindowListener
	} // end DisplayQueryResults constructor

	/**
	 * Creates and returns a JPanel used as a banner to hold the buttons
	 * available to the Admin for various tasks.
	 * @return adminGUI. A JPanel holding admin buttons.
	 */
	public JPanel adminBanner()
	{
		// Holds all content for the default screen.
		JPanel adminGUI = new JPanel();
		adminGUI.setLayout(new BorderLayout(0, 0));

		/** Top JPanel to hold all content on top of screen. */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		adminGUI.add(topPanel, BorderLayout.PAGE_START);

		/** Text panel to hold JLabels. */
		textPanel = new JPanel();
		textPanel.setSize(400, 200);
		topPanel.add(textPanel, BorderLayout.PAGE_START);

		// Welcoming label.
		JLabel welcomeLabel = new JLabel("Welcome Admin.");
		welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		welcomeLabel.setLocation(0,0);
		welcomeLabel.setSize(300, 30);
		welcomeLabel.setHorizontalAlignment(0);
		textPanel.add(welcomeLabel);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		topPanel.add(buttonPanel, BorderLayout.EAST);

		// Archive a function.
		adminArchiveButton = new JButton("Archive");
		adminArchiveButton.setMinimumSize(new Dimension(100, 30));
		adminArchiveButton.setPreferredSize(new Dimension(120, 30));
		adminArchiveButton.addActionListener(this);
		buttonPanel.add(adminArchiveButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Add a car to the data base button.
		adminAddCarButton = new JButton("Add a Car");
		adminAddCarButton.setMinimumSize(new Dimension(100, 30));
		adminAddCarButton.setPreferredSize(new Dimension(120, 30));
		adminAddCarButton.addActionListener(this);
		buttonPanel.add(adminAddCarButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Remove a car from the data base button.
		adminRemoveCarButton = new JButton("Remove a Car");
		adminRemoveCarButton.setMinimumSize(new Dimension(100, 30));
		adminRemoveCarButton.setPreferredSize(new Dimension(120, 30));
		adminRemoveCarButton.addActionListener(this);
		buttonPanel.add(adminRemoveCarButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Customer Information Button.
		adminViewCarsButton = new JButton("View Car Information");
		adminViewCarsButton.setMinimumSize(new Dimension(150, 30));
		adminViewCarsButton.setPreferredSize(new Dimension(160, 30));
		adminViewCarsButton.addActionListener(this);
		buttonPanel.add(adminViewCarsButton);

		adminGUI.setOpaque(true);	
		return adminGUI;		
	}

	/**
	 * Creates and returns a JPanel used as a banner to hold the buttons
	 * available to the default (initial not signed in) user for various tasks.
	 * @return defaultGUI. A JPanel holding default buttons.
	 */
	public JPanel defaultBanner()
	{
		// Holds all content for the default screen.
		JPanel defaultGUI = new JPanel();
		defaultGUI.setLayout(new BorderLayout(0, 0));

		/** Top JPanel to hold all content on top of screen. */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		defaultGUI.add(topPanel, BorderLayout.PAGE_START);

		/** Text panel to hold JLabels. */
		textPanel = new JPanel();
		topPanel.add(textPanel, BorderLayout.PAGE_START);

		// Welcoming label.
		JLabel welcomeLabel = new JLabel("Rent A Car, Experience A Journey.");
		welcomeLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 16));
		welcomeLabel.setLocation(0,0);
		welcomeLabel.setSize(300, 30);
		welcomeLabel.setHorizontalAlignment(0);
		textPanel.add(welcomeLabel);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		topPanel.add(buttonPanel, BorderLayout.CENTER);

		// Button to rent a car.
		defaultRentButton = new JButton("Rent a Car");
		defaultRentButton.setMinimumSize(new Dimension(90, 30));
		defaultRentButton.setPreferredSize(new Dimension(100, 30));
		defaultRentButton.addActionListener(this);
		buttonPanel.add(defaultRentButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Button to rent a car.
		defaultRegisterButton = new JButton("Register");
		defaultRegisterButton.setMinimumSize(new Dimension(90, 30));
		defaultRegisterButton.setPreferredSize(new Dimension(100, 30));
		defaultRegisterButton.addActionListener(this);
		buttonPanel.add(defaultRegisterButton);

		defaultGUI.setOpaque(true);
		return defaultGUI;
	}

	/**
	 * Creates and returns a JPanel used as a banner to hold the the title of
	 * login screen.
	 * @return signInGUI. A JPanel holding sign in title.
	 */
	public JPanel signInBanner()
	{
		JPanel signInGUI = new JPanel();
		signInGUI.setLayout(new BorderLayout(10, 10));

		JLabel titleLabel = new JLabel("Login Screen");
		titleLabel.setPreferredSize(new Dimension(290, 30));
		titleLabel.setHorizontalAlignment(0);
		signInGUI.add(titleLabel, BorderLayout.PAGE_START);

		return signInGUI;
	}

	/**
	 * Creates and returns a JPanel used as a banner to hold the buttons
	 * available to the signed in user for various tasks.
	 * @return userGUI. A JPanel holding user relative buttons.
	 */
	public JPanel userBanner()
	{
		// Holds all content for the default screen.
		JPanel userGUI = new JPanel();
		userGUI.setLayout(new BorderLayout());

		/** Top JPanel to hold all content on top of screen. */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		userGUI.add(topPanel, BorderLayout.NORTH);

		/** Text panel to hold JLabels. */
		textPanel = new JPanel();
		topPanel.add(textPanel, BorderLayout.NORTH);

		// Welcoming label.
		JLabel welcomeLabel = new JLabel("Rent A Car, Experience A Journey.");
		welcomeLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 16));
		welcomeLabel.setLocation(0,0);
		welcomeLabel.setSize(300, 30);
		welcomeLabel.setHorizontalAlignment(0);
		textPanel.add(welcomeLabel);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		topPanel.add(buttonPanel, BorderLayout.EAST);

		// Button to view available cars.
		userViewButton = new JButton("View Cars");
		userViewButton.setMinimumSize(new Dimension(80, 30));
		userViewButton.setPreferredSize(new Dimension(92, 30));
		userViewButton.addActionListener(this);
		buttonPanel.add(userViewButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Button to rent a car.
		userRentButton = new JButton("Rent a Car");
		userRentButton.setMinimumSize(new Dimension(90, 30));
		userRentButton.setPreferredSize(new Dimension(93, 30));
		userRentButton.addActionListener(this);
		buttonPanel.add(userRentButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Button to return a car.
		userReturnButton = new JButton("Return a Car");
		userReturnButton.setMinimumSize(new Dimension(100, 30));
		userReturnButton.setPreferredSize(new Dimension(105, 30));
		userReturnButton.addActionListener(this);
		buttonPanel.add(userReturnButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		cancelReservationButton = new JButton("Cancel Rev");
		cancelReservationButton.setMinimumSize(new Dimension(120, 30));
		cancelReservationButton.setPreferredSize(new Dimension(120, 30));
		cancelReservationButton.addActionListener(this);
		buttonPanel.add(cancelReservationButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Button to return a car.
		userCustomerInfoButton = new JButton("Your Information");
		userCustomerInfoButton.setMinimumSize(new Dimension(120, 30));
		userCustomerInfoButton.setPreferredSize(new Dimension(128, 30));
		userCustomerInfoButton.addActionListener(this);
		buttonPanel.add(userCustomerInfoButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Button to return a car.
		userAccountButton = new JButton("Account Information");
		userAccountButton.setMinimumSize(new Dimension(120, 30));
		userAccountButton.setPreferredSize(new Dimension(150, 30));
		userAccountButton.addActionListener(this);
		buttonPanel.add(userAccountButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		userGUI.setOpaque(true);
		return userGUI;		
	}

	/**
	 * Creates and returns a JPanel used as a screen that the admin will see
	 * when he/she wants to archive a table.
	 * @return archiveGUI. A JPanel holding archiving information.
	 */
	public JPanel adminArchiveScreen()
	{
		JPanel archiveGUI = new JPanel();
		archiveGUI.setLayout(new GridLayout(0, 1));

		lastModifiedDate = new JTextField(10);

		tables = new JComboBox(tables_arr);
		tables.setSelectedIndex(0);

		archiveGUI.add(adminArchiveLabel);
		archiveGUI.add(tables);
		archiveGUI.add(new JLabel("Please enter reference archive date" +
				"\nEnter in YYYY-MM-DD format"));
		archiveGUI.add(lastModifiedDate);	

		return archiveGUI;
	}

	/**
	 * Creates and returns a JPanel used as a screen that the admin will see
	 * when he/she wants to add a car to the database.
	 * @return adminAddGUI. A JPanel holding adding information.
	 */
	public JPanel adminAddCarScreen()
	{
		JPanel adminAddGUI = new JPanel();
		adminAddGUI.setLayout(new BorderLayout(10, 10));

		// Title Label.
		JLabel adminAdd = new JLabel("ADD a Car to the Data Base.");
		adminAdd.setFont(new Font("Dialog", Font.BOLD, 14));
		adminAdd.setPreferredSize(new Dimension(registerPanel.getX(), 30));
		adminAdd.setHorizontalAlignment(0);
		adminAddGUI.add(adminAdd, BorderLayout.NORTH);

		/** Following three JPanels are for formating purposes. */
		// Right space.
		JPanel rightSpace = new JPanel();
		rightSpace.setPreferredSize(new Dimension(50, 30));
		adminAddGUI.add(rightSpace, BorderLayout.WEST);

		// Left space.
		JPanel leftSpace = new JPanel();
		leftSpace.setPreferredSize(new Dimension(registerPanel.getX() + 10, 30));
		adminAddGUI.add(leftSpace, BorderLayout.EAST);

		/** Panel to hold all TextFields. */
		JPanel addCarFormPanel = new JPanel();
		addCarFormPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		adminAddGUI.add(addCarFormPanel, BorderLayout.CENTER);

		// Car information.
		adminAddCarLicensePlateField = new JTextField(8);
		carModelField = new JTextField(20);
		carMakeField = new JTextField(20);
		carYearField = new JTextField(4);

		// Car_Spec information.
		carMPGField = new JTextField(2);
		car2_4DoorField = new JTextField(2);
		carColorField = new JTextField(10);
		carCurrentMilesField = new JTextField(6);
		carPriceField = new JTextField(4);

		// License Plate Panel.
		JPanel lpPanel = new JPanel();
		lpPanel.setSize(100, 30);
		lpPanel.add(new JLabel("License Plate: "));
		lpPanel.add(adminAddCarLicensePlateField);
		addCarFormPanel.add(lpPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Make Panel.
		JPanel makePanel = new JPanel();
		makePanel.setSize(100, 30);
		makePanel.add(new JLabel("Car Make: "));
		makePanel.add(carMakeField);
		addCarFormPanel.add(makePanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Model Panel.
		JPanel modelPanel = new JPanel();
		modelPanel.setSize(100, 30);
		modelPanel.add(new JLabel("Car Model: "));
		modelPanel.add(carModelField);
		addCarFormPanel.add(modelPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Year Panel.
		JPanel yearPanel = new JPanel();
		yearPanel.setSize(100, 30);
		yearPanel.add(adminYearLabel);

		// Select Year Button.
		adminCarYearButton = new JButton("Year");
		adminCarYearButton.addActionListener(this);
		yearPanel.add(adminCarYearButton);
		addCarFormPanel.add(yearPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car MPG Panel.
		JPanel mpgPanel = new JPanel();
		mpgPanel.setSize(100, 30);
		mpgPanel.add(adminMPGLabel);

		// Select MPG Button.
		adminCarMPGButton = new JButton("MPG");
		adminCarMPGButton.addActionListener(this);
		mpgPanel.add(adminCarMPGButton);
		addCarFormPanel.add(mpgPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car 2 or 4 Door Panel.
		JPanel doorPanel = new JPanel();
		doorPanel.setSize(100, 30);
		doorPanel.add(admin2_4DoorLabel);

		// Select 2 or 4 Door Button.
		adminCar2_4Button = new JButton("2/4 Door");
		adminCar2_4Button.addActionListener(this);
		doorPanel.add(adminCar2_4Button);
		addCarFormPanel.add(doorPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Color Panel.
		JPanel colorPanel = new JPanel();
		colorPanel.setSize(100, 30);
		colorPanel.add(adminColorLabel);

		// Select Color Button.
		adminCarColorButton = new JButton("Color");
		adminCarColorButton.addActionListener(this);
		colorPanel.add(adminCarColorButton);
		addCarFormPanel.add(colorPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Miles Panel.
		JPanel milesPanel = new JPanel();
		milesPanel.setSize(100, 30);
		milesPanel.add(new JLabel("Current Miles: "));
		milesPanel.add(carCurrentMilesField);
		addCarFormPanel.add(milesPanel);
		addCarFormPanel.add(Box.createHorizontalBox());

		// Car Price Panel.
		JPanel pricePanel = new JPanel();
		pricePanel.setSize(100, 30);
		pricePanel.add(new JLabel("Price: "));
		pricePanel.add(carPriceField);
		addCarFormPanel.add(pricePanel);
		addCarFormPanel.add(Box.createHorizontalBox());
		addCarFormPanel.add(Box.createHorizontalStrut(200));

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(300, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		addCarFormPanel.add(buttonPanel);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, new Dimension(100, 30), maxSize));

		// Add a car to the data base button.
		adminCarAddSubmitButton = new JButton("Add Car");
		adminCarAddSubmitButton.setMinimumSize(new Dimension(100, 30));
		adminCarAddSubmitButton.setPreferredSize(new Dimension(120, 30));
		adminCarAddSubmitButton.addActionListener(this);
		buttonPanel.add(adminCarAddSubmitButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Add a car to the data base button.
		adminCarCancelButton = new JButton("Cancel");
		adminCarCancelButton.setMinimumSize(new Dimension(100, 30));
		adminCarCancelButton.setPreferredSize(new Dimension(120, 30));
		adminCarCancelButton.addActionListener(this);
		buttonPanel.add(adminCarCancelButton);

		return adminAddGUI;
	}

	/**
	 * Creates and returns a JPanel used as a screen that the admin will see
	 * when he/she wants to remove a car to the database.
	 * @return adminRemoveGUI. A JPanel holding removal information.
	 */
	public JPanel adminRemoveCarScreen()
	{
		JPanel adminRemoveGUI = new JPanel();
		adminRemoveGUI.setLayout(new BorderLayout(10, 10));

		// Title Label.
		JLabel adminRemove = new JLabel("REMOVE a Car to the Data Base.");
		adminRemove.setFont(new Font("Dialog", Font.BOLD, 14));
		adminRemove.setPreferredSize(new Dimension(registerPanel.getX(), 30));
		adminRemove.setHorizontalAlignment(0);
		adminRemoveGUI.add(adminRemove, BorderLayout.NORTH);

		/** Panel to hold all TextFields. */
		JPanel removeCar = new JPanel();
		removeCar.setLayout(new FlowLayout(FlowLayout.CENTER));
		adminRemoveGUI.add(removeCar, BorderLayout.CENTER);

		// Car information.
		carLicensePlateField = new JTextField(8);

		removeCar.add(new JLabel("Enter Licenes Plate: "));
		removeCar.add(carLicensePlateField);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(300, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		removeCar.add(buttonPanel);

		// Remove a car to the data base button.
		adminCarRemoveSubmitButton = new JButton("Delete");
		adminCarRemoveSubmitButton.setMinimumSize(new Dimension(100, 30));
		adminCarRemoveSubmitButton.setPreferredSize(new Dimension(120, 30));
		adminCarRemoveSubmitButton.addActionListener(this);
		removeCar.add(adminCarRemoveSubmitButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Cancel a Remove a car from the data base button.
		adminCarRemoveCancelButton = new JButton("Cancel");
		adminCarRemoveCancelButton.setMinimumSize(new Dimension(100, 30));
		adminCarRemoveCancelButton.setPreferredSize(new Dimension(120, 30));
		adminCarRemoveCancelButton.addActionListener(this);
		removeCar.add(adminCarRemoveCancelButton);	

		return adminRemoveGUI;
	}

	/**
	 * Creates and returns a JPanel used as a side screen that the user will see
	 * when he/she wants to select a car to rent from the database.
	 * @return opGUI. A JPanel holding various options to select for the car.
	 */
	public JPanel optionsScreen()
	{
		JPanel opGUI = new JPanel();
		opGUI.setBackground(Color.orange);
		opGUI.setLayout(new BorderLayout(10, 10));

		JLabel opLabel = new JLabel("Select options");
		opLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		opLabel.setPreferredSize(new Dimension(120, 25));
		opLabel.setHorizontalAlignment(0);
		opGUI.add(opLabel, BorderLayout.NORTH);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(200, 40);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		opGUI.add(buttonPanel, BorderLayout.CENTER);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View Default list of cars.
		userDefaultButton = new JButton("DEFAULT");
		userDefaultButton.setPreferredSize(new Dimension(100, 30));
		userDefaultButton.addActionListener(this);
		buttonPanel.add(userDefaultButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on color.
		userColorButton = new JButton("Color");
		userColorButton.setPreferredSize(new Dimension(100, 30));
		userColorButton.addActionListener(this);
		buttonPanel.add(userColorButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on MPG.
		userMPGButton = new JButton("MPG");
		userMPGButton.setPreferredSize(new Dimension(100, 30));
		userMPGButton.addActionListener(this);
		buttonPanel.add(userMPGButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on 2 or 4 Doors.
		userTwoFourDoorButton = new JButton("2/4 Door");
		userTwoFourDoorButton.setPreferredSize(new Dimension(100, 30));
		userTwoFourDoorButton.addActionListener(this);
		buttonPanel.add(userTwoFourDoorButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on Current Miles.
		userCurrentMilesButton = new JButton("Current Miles");
		userCurrentMilesButton.setPreferredSize(new Dimension(100, 30));
		userCurrentMilesButton.addActionListener(this);
		buttonPanel.add(userCurrentMilesButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on Price.
		userPriceButton = new JButton("Price");
		userPriceButton.setPreferredSize(new Dimension(100, 30));
		userPriceButton.addActionListener(this);
		buttonPanel.add(userPriceButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// View list of cars based on rating.
		userRatingButton = new JButton("Rating");
		userRatingButton.setPreferredSize(new Dimension(100, 30));
		userRatingButton.addActionListener(this);
		buttonPanel.add(userRatingButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		// Rent A Car
		rentACarButton = new JButton("Rent Car");
		rentACarButton.setPreferredSize(new Dimension(100, 30));
		rentACarButton.addActionListener(this);
		buttonPanel.add(rentACarButton);

		// Space between buttons.
		buttonPanel.add(Box.createVerticalStrut(10));

		userRequestCarButton = new JButton("Request A Car");
		userRequestCarButton.setPreferredSize(new Dimension(100, 30));
		userRequestCarButton.addActionListener(this);
		buttonPanel.add(userRequestCarButton);

		return opGUI;
	}

	/**
	 * Checks if user inputed data is in correct format.
	 * @return true if in correct format, false otherwise.
	 */
	public boolean isValid(String input, int name){
		if(name==0){
			return input.matches("[a-zA-Z0-9]*");
		}
		if(name==1){
			return !(input.matches("[.*\\d.]*"));
		}
		if(name==2){
			return input.matches("[0-9]*");
		}
		return false;
	}

	/**
	 * Checks if age is correctly inputed by user.
	 * @return true if age in correct format, false otherwise.
	 */
	public int parseAge(String age) 
	{
		if(!isValid(age,2))
		{
			JOptionPane.showMessageDialog(null, "Invalid Digits","invalid",JOptionPane.WARNING_MESSAGE);
			return 0;
		}
		if (!age.isEmpty() && isValid(age, 2))
			return Integer.parseInt(age);
		else
			return 0;
	}

	/**
	 * Creates and returns an array with information from the database based
	 * on a specific query.
	 * @return list. A String array as a list of times retrieved from the query.
	 */
	public String[] addToArray(String query) 
	{
		ArrayList<String> optionList = new ArrayList<String>();

		try 
		{
			Class.forName(JDBC_DRIVER).newInstance();
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME,
					PASSWORD);
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery(query);

			while (res.next()) 
			{
				optionList.add(res.getString(1));
			}

			conn.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		Collections.sort(optionList);
		String[] list = optionList.toArray(new String[optionList.size()]);

		return list;
	}

	/**
	 * Creates and returns a JPanel used as a screen that the user will see
	 * when he/she wants to register with the database.
	 * @return registerPanel. A JPanel holding the information needed to register.
	 */
	public JPanel registerScreen()
	{
		// Main Panel.
		registerPanel = new JPanel();
		registerPanel.setLayout(new BorderLayout(30, 30));

		// Title Label.
		formLabel = new JLabel("Registration Form");
		formLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		formLabel.setPreferredSize(new Dimension(registerPanel.getX(), 30));
		formLabel.setHorizontalAlignment(0);
		registerPanel.add(formLabel, BorderLayout.NORTH);

		/** Following three JPanels are for formating purposes. */
		// Right space.
		JPanel rightSpace = new JPanel();
		rightSpace.setPreferredSize(new Dimension(50, 30));
		registerPanel.add(rightSpace, BorderLayout.WEST);

		// Left space.
		JPanel leftSpace = new JPanel();
		leftSpace.setPreferredSize(new Dimension(registerPanel.getX() + 10, 30));
		registerPanel.add(leftSpace, BorderLayout.EAST);

		// South space.
		JPanel bottomSpace = new JPanel();
		bottomSpace.setPreferredSize(new Dimension(registerPanel.getX(), 100));
		registerPanel.add(bottomSpace, BorderLayout.SOUTH);

		// Customer information fields.
		fullNameField = new JTextField(10);
		driversLicenseField = new JTextField(7);
		creditCardField = new JTextField(16);
		ageField = new JTextField(2); 

		/** Panel to hold all TextFields. */
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		registerPanel.add(formPanel, BorderLayout.CENTER);		

		// Full Name Panel.
		JPanel fNPanel = new JPanel();
		fNPanel.setSize(100, 30);
		fNPanel.add(new JLabel("Full Name: "));
		fNPanel.add(fullNameField);
		formPanel.add(fNPanel);
		formPanel.add(Box.createHorizontalBox());

		// Driver's License Panel.
		JPanel dLPanel = new JPanel();
		dLPanel.setSize(100, 30);
		dLPanel.add(new JLabel("Drivers License: "));
		dLPanel.add(driversLicenseField);
		formPanel.add(dLPanel);
		formPanel.add(Box.createHorizontalBox());

		// Credit Card Panel.
		JPanel cCPanel = new JPanel();
		cCPanel.setSize(100, 30);
		cCPanel.add(new JLabel("Credit Card: "));
		cCPanel.add(creditCardField);
		formPanel.add(cCPanel);
		formPanel.add(Box.createHorizontalBox());

		// Age Panel.
		JPanel agePanel = new JPanel();
		agePanel.setSize(100, 30);
		agePanel.add(new JLabel("Age: "));
		agePanel.add(ageField);
		formPanel.add(agePanel);
		formPanel.add(Box.createHorizontalBox());
		formPanel.add(Box.createHorizontalStrut(300));

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(300, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		formPanel.add(buttonPanel);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, new Dimension(80, 30), maxSize));

		// Add a car to the data base button.
		defaultSubmitButton = new JButton("Submit");
		defaultSubmitButton.setMinimumSize(new Dimension(100, 30));
		defaultSubmitButton.setPreferredSize(new Dimension(120, 30));
		defaultSubmitButton.addActionListener(this);
		buttonPanel.add(defaultSubmitButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Add a car to the data base button.
		defaultCancelButton = new JButton("Cancel");
		defaultCancelButton.setMinimumSize(new Dimension(100, 30));
		defaultCancelButton.setPreferredSize(new Dimension(120, 30));
		defaultCancelButton.addActionListener(this);
		buttonPanel.add(defaultCancelButton);

		return registerPanel;
	}

	/**
	 * Creates and returns a JPanel used as a screen that the user will see
	 * when he/she wants to return a car they rented.
	 * @return returnGUI. A JPanel holding the button to return a car.
	 */
	public JPanel returnButtonScreen()
	{
		JPanel returnGUI = new JPanel();
		returnGUI.setLayout(new BorderLayout(10, 10));

		// Title Label.
		JLabel loginLabel = new JLabel("Return Car");
		loginLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		loginLabel.setPreferredSize(new Dimension(70, 30));
		loginLabel.setHorizontalAlignment(0);
		returnGUI.add(loginLabel, BorderLayout.NORTH);

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(70, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		returnGUI.add(buttonPanel, BorderLayout.CENTER);

		// Return this car.
		userReturnThisCarButton = new JButton("Return Car");
		userReturnThisCarButton.setMinimumSize(new Dimension(100, 30));
		userReturnThisCarButton.setPreferredSize(new Dimension(120, 30));
		userReturnThisCarButton.addActionListener(this);
		buttonPanel.add(userReturnThisCarButton);		

		return returnGUI;
	}

	/**
	 * Creates and returns a JPanel used as a screen that the user will see
	 * when he/she wants to sign in to our system.
	 * @return signInGUI. A JPanel holding the information needed to sign in.
	 */
	public JPanel signInScreen()
	{
		JPanel signInGUI = new JPanel();
		signInGUI.setLayout(new BorderLayout(10, 10));

		// Title Label.
		JLabel loginLabel = new JLabel("Login Screen");
		loginLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		loginLabel.setPreferredSize(new Dimension(290, 30));
		loginLabel.setHorizontalAlignment(0);
		signInGUI.add(loginLabel, BorderLayout.NORTH);

		/** Following three JPanels are for formating purposes. */
		// Right space.
		JPanel rightSpace = new JPanel();
		rightSpace.setPreferredSize(new Dimension(50, 30));
		signInGUI.add(rightSpace, BorderLayout.WEST);

		// Left space.
		JPanel leftSpace = new JPanel();
		leftSpace.setPreferredSize(new Dimension(registerPanel.getX() + 10, 30));
		signInGUI.add(leftSpace, BorderLayout.EAST);

		// South space.
		JPanel bottomSpace = new JPanel();
		bottomSpace.setPreferredSize(new Dimension(registerPanel.getX(), 100));
		signInGUI.add(bottomSpace, BorderLayout.SOUTH);

		/** Panel to hold all TextFields. */
		JPanel signInFormPanel = new JPanel();
		signInFormPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		signInGUI.add(signInFormPanel, BorderLayout.CENTER);

		// User name.
		userNameField = new JTextField(10);

		// Password.
		loginField = new JPasswordField(8);

		// User Name Panel.
		JPanel uNameLabel = new JPanel();
		uNameLabel.setSize(100, 30);
		uNameLabel.add(new JLabel("Username (Full Name): "));
		uNameLabel.add(userNameField);
		signInFormPanel.add(uNameLabel);
		signInFormPanel.add(Box.createHorizontalBox());

		// Login Panel.
		JPanel loginPanel = new JPanel();
		loginPanel.setSize(100, 30);
		loginPanel.add(new JLabel("DriversLicense: "));
		loginPanel.add(loginField);
		signInFormPanel.add(loginPanel);
		signInFormPanel.add(Box.createHorizontalBox());

		/** Button Panel to hold buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(300, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		signInFormPanel.add(buttonPanel);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, new Dimension(80, 30), maxSize));

		// Add a car to the data base button.
		loginButton = new JButton("Login");
		loginButton.setMinimumSize(new Dimension(100, 30));
		loginButton.setPreferredSize(new Dimension(120, 30));
		loginButton.addActionListener(this);
		buttonPanel.add(loginButton);

		// Space between buttons changes if window size changed.
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		// Add a car to the data base button.
		cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(100, 30));
		cancelButton.setPreferredSize(new Dimension(120, 30));
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		// Format the south border.
		JPanel formatSouth = new JPanel();
		formatSouth.setSize(100, 100);
		formatSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		signInGUI.add(formatSouth, BorderLayout.SOUTH);

		JPanel registerPanel = new JPanel();
		registerPanel.setLayout(new FlowLayout());
		formatSouth.add(registerPanel);

		JLabel register = new JLabel("Not signed up? Click here to register");
		registerPanel.add(register);

		// Space between buttons changes if window size changed.
		registerPanel.add(Box.createHorizontalStrut(10));

		registerButton = new JButton("Register");
		registerButton.setSize(70, 30);
		registerButton.addActionListener(this);
		registerPanel.add(registerButton);

		return signInGUI;
	}

	/**
	 * Creates and returns a JPanel used as a screen that holds the table.
	 * @return tablePanel. A JPanel holding table that represents our tables
	 * from our database.
	 */
	public JPanel tableScreen()
	{
		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout(0, 0));

		optionsPanel = new JPanel();
		optionsPanel.setLayout(new CardLayout());
		tablePanel.add(optionsPanel, BorderLayout.WEST);

		JPanel filler = new JPanel();
		filler.add(new JLabel("      "));
		tablePanel.add(filler, BorderLayout.EAST);

		optionsPanel.add(new JPanel(), "blank");
		optionsPanel.add(optionsScreen(), "buttons");
		optionsPanel.add(returnButtonScreen(), "Click to return");

		try 
		{
			// create TableModel for results of Default query.
			tableModel = new ResultSetTableModel( JDBC_DRIVER, DATABASE_URL, 
					USERNAME, PASSWORD, DEFAULT_QUERY );

			JTable resultTable = new JTable(tableModel);
			JScrollPane area = new JScrollPane(resultTable, 
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tablePanel.add(area, BorderLayout.CENTER);
		} // end try
		catch ( ClassNotFoundException classNotFound ) 
		{
			JOptionPane.showMessageDialog( null, 
					"MySQL driver not found", "Driver not found",
					JOptionPane.ERROR_MESSAGE );

			System.exit( 1 ); // terminate application
		} // end catch
		catch ( SQLException sqlException ) 
		{
			JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
					"Database error", JOptionPane.ERROR_MESSAGE );

			// ensure database connection is closed
			tableModel.disconnectFromDatabase();

			System.exit( 1 );   // terminate application
		} // end catch

		return tablePanel;
	}

	@Override
	/**
	 * The various tasks accomplished by pushing various tasks.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		int indexSelected;
		String decending = "DESC";
		String acending = "ASC";
		String order;
		String[] sort = {"high", "low"};

		CardLayout buttons = (CardLayout)(buttonPanel.getLayout());
		CardLayout content = (CardLayout)(contentPanel.getLayout());
		CardLayout options = (CardLayout)(optionsPanel.getLayout());

		colorsList = new JComboBox(colors_arr);
		colorsList.setSelectedIndex(0);
		twoFourList = new JComboBox(twoFour_arr);
		twoFourList.setSelectedIndex(0);
		adminYearList = new JComboBox(year_arr);
		adminYearList.setSelectedIndex(0);

		if (e.getSource() == adminArchiveButton)
		{
			String archivingTable = null;
			int Ok = JOptionPane.showConfirmDialog(null, adminArchiveScreen(),
					"Archive Table: ", JOptionPane.OK_CANCEL_OPTION);
			if (Ok == JOptionPane.OK_OPTION) 
			{
				archivingTable = (String) tables.getSelectedItem();
				if (archivingTable.equals("Customer")) //TODO
				{
					newQuery("CALL CustomerArchiveTable('"
							+ lastModifiedDate.getText() + "')");
					newQuery("Select * from Customers_Archive;");
				}
				else if (archivingTable.equals("Rental"))
				{
					newQuery("CALL RentalArchiveTable('"
							+ lastModifiedDate.getText() + "')");
					newQuery("Select * from Rental_Archive;");
				}
				else if (archivingTable.equals("Returned"))
				{
					newQuery("CALL ReturnedArchiveTable('"
							+ lastModifiedDate.getText() + "')");
					newQuery("Select * from Returned_Archive;");
				}
				content.first(contentPanel);
			}
		}
		else if (e.getSource() == adminAddCarButton)
		{
			content.first(contentPanel);
			content.next(contentPanel);
			content.next(contentPanel);

			JPanel suggestedCars = new JPanel();
			String suggests = "Select * from suggest_car";
			try 
			{
				// create TableModel for results of Default query.
				ResultSetTableModel table = new ResultSetTableModel( JDBC_DRIVER, 
						DATABASE_URL, USERNAME, PASSWORD, suggests );

				JTable result = new JTable(table);
				JScrollPane area = new JScrollPane(result, 
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				suggestedCars.add(area);

				JOptionPane.showMessageDialog( null, 
						suggestedCars, "Suggested Cars",
						JOptionPane.DEFAULT_OPTION );
			} // end try
			catch ( ClassNotFoundException classNotFound ) 
			{
				JOptionPane.showMessageDialog( null, 
						"MySQL driver not found", "Driver not found",
						JOptionPane.ERROR_MESSAGE );

				System.exit( 1 ); // terminate application
			} // end catch
			catch ( SQLException sqlException ) 
			{
				JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
						"Database error", JOptionPane.ERROR_MESSAGE );

				// ensure database connection is closed
				tableModel.disconnectFromDatabase();

				System.exit( 1 );   // terminate application
			} // end catch
		}
		else if (e.getSource() == adminCarAddSubmitButton)
		{
			// Insert new user.
			boolean validInput = true;
			carLicensePlate = adminAddCarLicensePlateField.getText();
			carMake = carMakeField.getText();
			carModel = carModelField.getText();
			carCurrentMiles = carCurrentMilesField.getText();
			carPrice = carPriceField.getText();

			if(carLicensePlate.length() != 7 || !isValid(carLicensePlate,0))
			{
				JOptionPane.showMessageDialog(null, "\\Incorrect","Invalid License Plate",
						JOptionPane.ERROR_MESSAGE);
				validInput = false;
			}
			if(carMake.length()< 3 || !isValid(carMake,0))
			{
				JOptionPane.showMessageDialog(null, "\\Incorrect","Car Make",
						JOptionPane.ERROR_MESSAGE);
				validInput = false;
			}
			if(carModel.length()< 3|| !isValid(carModel,0))
			{
				JOptionPane.showMessageDialog(null, "\\Incorrect","Car Model",
						JOptionPane.ERROR_MESSAGE);
				validInput = false;
			}
			if(!isValid(carCurrentMiles,2) || Integer.parseInt(carCurrentMiles) < 0)
			{
				JOptionPane.showMessageDialog(null,
						"Please enter valid number",
						"Invalid Current Miles", JOptionPane.ERROR_MESSAGE);
				validInput = false;

			}
			if(validInput==true){
				// Insert new car.
				try 
				{
					tableModel.setQuery("INSERT INTO Car Values ('" 
							+ carLicensePlate + "', '" + carModel
							+ "', '" + carMake + "', '" + carYear
							+ "');");
					tableModel.setQuery("INSERT INTO Car_Spec Values ('" 
							+ carLicensePlate + "', '" + carMPG
							+ "', '" + car2_4Door + "', '" + carColor
							+ "', '" + carCurrentMiles + "', '" + carPrice
							+ "');");
					tableModel.setQuery("SELECT * FROM Car, Car_Spec WHERE " +
							"Car.LicensePlate = '" + carLicensePlate 
							+ "' AND Car.LicensePlate = Car_spec.LicensePlate;");

					carLicensePlateField.setText("");
					carMakeField.setText("");
					carModelField.setText("");
					carCurrentMilesField.setText("");
					carPriceField.setText("");
					content.first(contentPanel);
					options.first(optionsPanel);
				} // end try
				catch ( SQLException sqlException ) 
				{
					JOptionPane.showMessageDialog( null, 
							sqlException.getMessage(), "Database error", 
							JOptionPane.ERROR_MESSAGE );
					// try to recover from invalid user query 
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch ( SQLException sqlException2 ) 
					{
						JOptionPane.showMessageDialog( null, 
								sqlException2.getMessage(), "Database error", 
								JOptionPane.ERROR_MESSAGE );

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit( 1 ); // terminate application
					} // end inner catch                   
				} // end outer catch
			}
		}
		else if (e.getSource() == adminCarCancelButton)
		{
			carLicensePlateField.setText("");
			carMakeField.setText("");
			carModelField.setText("");
			carCurrentMilesField.setText("");
			carPriceField.setText("");

			adminColorLabel.setText("Color: ");
			adminYearLabel.setText("Year: ");
			adminMPGLabel.setText("MPG: ");
			admin2_4DoorLabel.setText("2/4 Door: ");			

			content.first(contentPanel);
			options.first(optionsPanel);
		}
		else if (e.getSource() == adminCarRemoveCancelButton)
		{
			carLicensePlateField.setText("");
			content.first(contentPanel);
			options.first(optionsPanel);
		}
		else if (e.getSource() == adminCarRemoveSubmitButton)
		{
			boolean validInput = true;
			carLicensePlate = carLicensePlateField.getText();
			if(carLicensePlate.length()<7 || !isValid(carLicensePlate,0))
			{
				JOptionPane.showMessageDialog(null, "\\Incorrect","Invalid License Plate",
						JOptionPane.ERROR_MESSAGE);
				validInput = false;
			}
			if(validInput==true){
				// Delete new car.
				try 
				{
					tableModel.setQuery("DELETE FROM Car_Spec WHERE LicensePlate = '"
							+ carLicensePlate + "' AND LicensePlate NOT IN ("
							+ "SELECT LicensePlate FROM Rental);");
					tableModel.setQuery("DELETE FROM Car WHERE LicensePlate = '"
							+ carLicensePlate + "' AND LicensePlate NOT IN ("
							+ "SELECT LicensePlate FROM Rental);");
					tableModel.setQuery("SELECT * FROM Car;");

					carLicensePlateField.setText("");
					content.first(contentPanel);
					options.first(optionsPanel);
				} // end try
				catch ( SQLException sqlException ) 
				{
					JOptionPane.showMessageDialog( null, 
							sqlException.getMessage(), "Database error", 
							JOptionPane.ERROR_MESSAGE );
					// try to recover from invalid user query 
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch ( SQLException sqlException2 ) 
					{
						JOptionPane.showMessageDialog( null, 
								sqlException2.getMessage(), "Database error", 
								JOptionPane.ERROR_MESSAGE );

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit( 1 ); // terminate application
					} // end inner catch                   
				} // end outer catch
			}
		}
		else if (e.getSource() == adminCar2_4Button)
		{
			int OkCancel = JOptionPane.showConfirmDialog(null, twoFourList,
					"Two or Four Doors: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				car2_4Door = (String) twoFourList.getSelectedItem();
			}
			admin2_4DoorLabel.setText(car2_4Door + " Door");
		}
		else if (e.getSource() == adminCarColorButton)
		{
			int OkCancel = JOptionPane.showConfirmDialog(null, colorsList,
					"Select Color: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				carColor = (String) colorsList.getSelectedItem();
			}
			adminColorLabel.setText(carColor);
		}
		else if (e.getSource() == adminCarMPGButton)
		{
			MPGList = new JComboBox(MPGList_arr);
			MPGList.setSelectedIndex(0);
			int OkCancel = JOptionPane.showConfirmDialog(null, MPGList,
					"Select MPG: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				carMPG = (String) MPGList.getSelectedItem();
			}
			adminMPGLabel.setText(carMPG);
		}
		else if (e.getSource() == adminCarYearButton)
		{
			int OkCancel = JOptionPane.showConfirmDialog(null, adminYearList,
					"Select Year: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				carYear = (String) adminYearList.getSelectedItem();
			}
			adminYearLabel.setText(carYear);
		}
		else if (e.getSource() == adminRemoveCarButton)
		{
			content.first(contentPanel);
			content.next(contentPanel);
			content.next(contentPanel);
			content.next(contentPanel);
		}
		else if (e.getSource() == adminViewCarsButton)
		{
			try 
			{
				tableModel.setQuery("SELECT Car.LicensePlate, Make, Model, Year,"
						+ " MPG, TwoFourDoor, Color, CurrentMiles, Price"
						+ " FROM Car, Car_Spec WHERE " +
						"Car.LicensePlate = Car_spec.LicensePlate;");
				content.first(contentPanel);
				options.first(optionsPanel);
			} // end try
			catch ( SQLException sqlException ) 
			{
				JOptionPane.showMessageDialog( null, 
						sqlException.getMessage(), "Database error", 
						JOptionPane.ERROR_MESSAGE );
				// try to recover from invalid user query 
				// by executing default query
				try 
				{
					tableModel.setQuery(DEFAULT_QUERY);
				} // end try
				catch ( SQLException sqlException2 ) 
				{
					JOptionPane.showMessageDialog( null, 
							sqlException2.getMessage(), "Database error", 
							JOptionPane.ERROR_MESSAGE );

					// ensure database connection is closed
					tableModel.disconnectFromDatabase();

					System.exit( 1 ); // terminate application
				} // end inner catch                   
			} // end outer catch
		}
		else if (e.getSource() == cancelButton)
		{
			userNameField.setText("");
			loginField.setText("");
			signInButton.setSelected(false);
		}
		else if (e.getSource() == defaultCancelButton)
		{
			if (signInButton.isSelected())
			{
				fullNameField.setText("");
				driversLicenseField.setText("");
				creditCardField.setText("");
				ageField.setText("");
				userCustomerInfoButton.doClick();
			}
			else
			{
				fullNameField.setText("");
				driversLicenseField.setText("");
				creditCardField.setText("");
				ageField.setText("");

				content.first(contentPanel);
				buttons.first(buttonPanel);  
				signInButton.setSelected(false);
			}
		}
		else if (e.getSource() == defaultSubmitButton)
		{
			// Insert new user or update customer information.
			boolean validInput = true;
			int age2;
			fullName = fullNameField.getText();
			driversLicense = driversLicenseField.getText();
			creditCard = creditCardField.getText();
			age = ageField.getText();

			if(fullName.length()<3 || !isValid(fullName,1))
			{
				JOptionPane.showMessageDialog(null, "\\Incorrect","Invalid Name",
						JOptionPane.ERROR_MESSAGE);
				validInput = false;
			}

			if(parseAge(age)!=0 || age.isEmpty())
			{
				//validInput = false;
				age2 = parseAge(age);
				if(age2<18){
					JOptionPane.showMessageDialog(null,
							"Note you are too young\\Incorrect",
							"Invalid Age", JOptionPane.ERROR_MESSAGE);
					validInput = false;
				}
			}
			if(driversLicense.length()!=8)
			{
				JOptionPane.showMessageDialog(null,
						"Incorrect value for Driver License","Invalid License",
						JOptionPane.ERROR_MESSAGE );
				validInput = false;
			}
			if(creditCard.length()!=16)
			{
				JOptionPane.showMessageDialog(null, "Incorrect CreditCard Number",
						"Invalid CC", JOptionPane.ERROR_MESSAGE );
				validInput = false;
			}

			if(validInput==true)
			{
				// Insert new user or update customer information.
				try 
				{
					if (isNew)
					{
						tableModel.setQuery("INSERT INTO Customer VALUES ('"
								+ driversLicense + "', '" + creditCard 
								+ "', '" + fullName + "', '" + age 
								+ "', true, NOW());");
						tableModel.setQuery("SELECT * FROM Customer WHERE " +
								"DriversLicense = '" + driversLicense + "';");
						isNew = false;
						rentTo = true;
						rented = false;
						signInButton.setSelected(true);
					}
					else
					{
						tableModel.setQuery("UPDATE Customer SET FullName = '"
								+ fullName + "', DriversLicense = '" 
								+ driversLicense + "', CreditCard = '"
								+ creditCard + "', Age = '"
								+ age + "' WHERE DriversLicense = '"
								+ driversLicense + "';");
						tableModel.setQuery("SELECT * FROM Customer WHERE " +
								"DriversLicense = '" + driversLicense + "';");
					}
					fullNameField.setText("");
					driversLicenseField.setText("");
					creditCardField.setText("");
					ageField.setText("");

					content.first(contentPanel);
					options.first(optionsPanel);
				} // end try
				catch ( SQLException sqlException ) 
				{
					JOptionPane.showMessageDialog( null, 
							sqlException.getMessage(), "Database error caused by defaultSubmitButton", 
							JOptionPane.ERROR_MESSAGE );
					// try to recover from invalid user query 
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch ( SQLException sqlException2 ) 
					{
						JOptionPane.showMessageDialog( null, 
								sqlException2.getMessage(), "Database error", 
								JOptionPane.ERROR_MESSAGE );

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit( 1 ); // terminate application
					} // end inner catch                   
				} // end outer catch
			}
		}
		else if (e.getSource() == defaultRegisterButton)
		{
			content.first(contentPanel);
			content.next(contentPanel);	
			formLabel.setText("Registration Form");
		}		
		else if (e.getSource() == defaultRentButton)
		{
			signInButton.setSelected(true);
		}
		else if (e.getSource() == loginButton)
		{			
			boolean validInput = true;
			String fullName1 = userNameField.getText();
			String password = loginField.getText();

			if (fullName1.toLowerCase().equals("admin"))
			{
				if (password.equals("admin"))
				{
					buttons.first(buttonPanel);
					buttons.next(buttonPanel);
					content.first(contentPanel);
					options.first(optionsPanel);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Invalid Password","Alert!", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				if(fullName1.length()<3 || !isValid(fullName1,1))
				{
					JOptionPane.showMessageDialog(null, "\\Incorrect","Invalid Name", JOptionPane.ERROR_MESSAGE);
					validInput = false;
				}
				if(password.length() !=8)
				{
					JOptionPane.showMessageDialog(null, "Password must be 8 characters long","Invalid Password", JOptionPane.ERROR_MESSAGE );
					validInput = false;
				}

				if (validInput == true) 
				{
					// Get all necessary customer information.
					try 
					{
						Class.forName(JDBC_DRIVER).newInstance();
						java.sql.Connection conn = DriverManager.getConnection(DATABASE_URL,
								USERNAME, PASSWORD);
						java.sql.Statement st = conn.createStatement();
						ResultSet res = st.executeQuery("SELECT * FROM Customer");
						// compare entered value with every dl value in customer
						// table
						while (res.next()) 
						{
							String driversLicense1 = res.getString("DriversLicense");
							String name = res.getString("FullName");

							if (driversLicense1.equals(password) && name.equals(fullName1)) 
							{
								System.out.println("you just loged in");
								fullName = fullName1;
								driversLicense = driversLicense1;
								creditCard = res.getString("CreditCard");
								age = res.getString("Age");
								int rent2 = Integer.parseInt(res.getString("RentTo"));
								if (rent2 != 0)
								{	// They are able to rent.
									rentTo = true;
								}							
								isNew = false;
								buttons.last(buttonPanel);
								buttons.previous(buttonPanel);
								content.first(contentPanel);
								options.first(optionsPanel);
								userNameField.setText("");
								loginField.setText("");
							}
						}
						conn.close();
					} 
					catch (Exception exc) 
					{
						exc.printStackTrace();
					}
					// Get license plate of car if they rented one.
					if (!rentTo) // If rentTo = false, they might be currently renting.
					{
						try 
						{
							Class.forName(JDBC_DRIVER).newInstance();
							java.sql.Connection conn = DriverManager.getConnection(DATABASE_URL,
									USERNAME, PASSWORD);
							java.sql.Statement st = conn.createStatement();
							ResultSet res = st.executeQuery("SELECT * FROM Rental " +
									"WHERE DriversLicense = '" + driversLicense +"';");
							// Get rented information if any.
							while (res.next()) 
							{
								String dl = res.getString("DriversLicense");
								if (dl.equals(driversLicense))
								{
									carLicensePlate = res.getString("LicensePlate");
									rented = true;
								}
							}
							conn.close();
						} 
						catch (Exception exc) 
						{
						}
					}
				}
			}
		}
		else if (e.getSource() == registerButton)
		{
			signInButton.setSelected(false);
			defaultRegisterButton.doClick();	
		}
		else if (e.getSource() == userAccountButton)
		{
			content.first(contentPanel);
			content.next(contentPanel);	
			formLabel.setText("Account Update");
		}
		else if (e.getSource() == userCustomerInfoButton)
		{
			if (rented)
			{
				// Query Customer information.
				try 
				{
					tableModel.setQuery("SELECT Customer.DriversLicense, FullName, "
							+ "CreditCard, Age, Car.LicensePlate, Make, Model, DateRented,"
							+ " PickUpDate, DueDate, ReturnDate, Car_Spec.Price, OverDue, RentTo"
							+ " FROM Customer, Rental, Car, Car_Spec"
							+ " WHERE Customer.DriversLicense = '" + driversLicense
							+ "' AND Customer.DriversLicense = Rental.DriversLicense"
							+ " AND Rental.LicensePlate = Car.LicensePlate" 
							+ " AND Car.LicensePlate = Car_Spec.LicensePlate;");
				} // end try
				catch (SQLException sqlException) 
				{
					JOptionPane.showMessageDialog(null, sqlException.getMessage(),
							"Database error", JOptionPane.ERROR_MESSAGE);
					// try to recover from invalid user query
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch (SQLException sqlException2) 
					{
						JOptionPane.showMessageDialog(null,
								sqlException2.getMessage(), "Database error",
								JOptionPane.ERROR_MESSAGE);

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit(1); // terminate application
					} // end inner catch
				}
			}
			else
			{
				try 
				{
					tableModel.setQuery("SELECT FullName, DriversLicense," +
							" CreditCard, Age, RentTo FROM Customer " +
							"WHERE DriversLicense = '" + driversLicense + "';");
				} // end try
				catch (SQLException sqlException4) 
				{
					JOptionPane.showMessageDialog(null, sqlException4.getMessage(),
							"Database error", JOptionPane.ERROR_MESSAGE);
					// try to recover from invalid user query
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch (SQLException sqlException5) 
					{
						JOptionPane.showMessageDialog(null,
								sqlException5.getMessage(), "Database error",
								JOptionPane.ERROR_MESSAGE);

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit(1); // terminate application
					} // end inner catch
				}
			}
			content.first(contentPanel);
			options.first(optionsPanel);
		}
		else if (e.getSource() == userViewButton)
		{
			// Query all Available Cars.
			newQuery("SELECT Year, Make, Model, Color, MPG, " +
					"TwoFourDoor, CurrentMiles, Price FROM Car, Car_Spec " +
					"WHERE Car.LicensePlate = Car_spec.LicensePlate AND " +
					"Car.LicensePlate NOT IN " +
					"(Select LicensePlate From rental);");
			content.first(contentPanel);
			options.first(optionsPanel);
		}
		else if (e.getSource() == userRentButton)
		{
			userDefaultButton.doClick();	// View default selection.
			content.first(contentPanel);
			options.last(optionsPanel);
			options.previous(optionsPanel);
		}
		else if (e.getSource() == userColorButton)
		{
			int OkCancel = JOptionPane.showConfirmDialog(null, colorsList,
					"Select Based on Car Color: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				carColor = "'" + (String) colorsList.getSelectedItem() + "'";
				System.out.println(carColor);
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and Color = ",
						carColor);
				query = "Select licenseplate from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and Color = " +
						carColor + ";";
			}
		}
		else if (e.getSource() == userDefaultButton)
		{
			query = "Select licenseplate, Make, Model, Year, Color, " +
					"MPG, TwoFourDoor, Price from car_spec natural join car" +
					" where CAR.LicensePlate NOT IN " +
					"(Select LicensePlate From Rental)";
			newQuery(query);
		}
		else if (e.getSource() == userMPGButton)
		{
			MPGList = new JComboBox(MPG_arr);
			MPGList.setSelectedIndex(0);
			int OkCancel = JOptionPane.showConfirmDialog(null, MPGList,
					"Select Based on Car MPG: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				String carMPG = "'"
						+ (String) MPGList.getSelectedItem() + "'";
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and MPG = ",
						carMPG);
				query = "Select licenseplate from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and MPG = " +
						carMPG + ";";
			}
		}
		else if (e.getSource() == userTwoFourDoorButton)
		{
			int OkCancel = JOptionPane.showConfirmDialog(null, twoFourList,
					"Two or Four Doors: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				car2_4Door = "'" + (String) twoFourList.getSelectedItem() + "'";
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and TwoFourDoor = ",
						car2_4Door);
				query = "Select licenseplate from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and TwoFourDoor = " +
						car2_4Door + ";";
			}
		}
		else if (e.getSource() == userCurrentMilesButton)
		{
			currentMilesList = new JComboBox(currentMiles_arr);
			currentMilesList.setSelectedIndex(0);
			int OkCancel = JOptionPane.showConfirmDialog(null, currentMilesList,
					"Select Car Based on Mileage: ", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				String carCurrentMiles = "'"
						+ (String) currentMilesList.getSelectedItem() + "'";
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and CurrentMiles = ",
						carCurrentMiles);
				query = "Select licenseplate from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) and CurrentMiles = " +
						carCurrentMiles + ";";
			}
		}
		else if (e.getSource() == userPriceButton)
		{
			priceList = new JComboBox(sort);
			priceList.setSelectedIndex(0);

			int OkCancel = JOptionPane.showConfirmDialog(null, priceList,
					"Order By Price", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				indexSelected = priceList.getSelectedIndex();
				if (indexSelected == 0)
				{
					order = decending;
				}
				else
				{
					order = acending;
				}
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) Order by price ",
						order);
				query = "Select licenseplate from car_spec natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) Order by price;";
			}
		}
		else if (e.getSource() == userRatingButton)
		{
			JComboBox rateList = new JComboBox(sort);
			rateList.setSelectedIndex(0);

			int OkCancel = JOptionPane.showConfirmDialog(null, rateList,
					"Order By Rating", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				indexSelected = rateList.getSelectedIndex();
				if (indexSelected == 0)
				{
					order = decending;
				}
				else
				{
					order = acending;
				}
				changeQuery("Select licenseplate, Make, Model, Year, Color, " +
						"MPG, TwoFourDoor, Price, avg(StarReview) as avgRate " +
						"from car_spec natural join car natural join returned " +
						"where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) Group by LicensePlate" +
						" Order by avgRate ", order);

				query = "Select distinct licenseplate from returned natural join car" +
						" where CAR.LicensePlate NOT IN " +
						"(Select LicensePlate From Rental) Group by LicensePlate" +
						" Order by avg(StarReview) " + order + ";";
			}
		}
		else if (e.getSource() == rentACarButton)
		{
			// Grab all the license plates the users sees.
			licensePlates_arr = addToArray(query); 
			// Put them in a ComboBox.
			JComboBox licensePlateList = new JComboBox(licensePlates_arr);
			licensePlateList.setSelectedIndex(0);
			// Give user option of how many days to rent.
			JComboBox daysRentList = new JComboBox(daysRent_arr);
			daysRentList.setSelectedIndex(0);
			// Ask user to pick a pick up date.
			JTextField pickUpDay = new JTextField();
			// Panel to hold relative information. 
			JPanel rentInformation = new JPanel();
			rentInformation.setLayout(new BoxLayout(rentInformation, BoxLayout.PAGE_AXIS));

			rentInformation.add(new JLabel("Enter the License Plate for the car " +
					"you wish to rent"));
			rentInformation.add(licensePlateList);
			rentInformation.add(new JLabel("Please enter the date you will be " +
					"picking up the vehicle.\nEnter in YYYY-MM-DD format"));
			rentInformation.add(pickUpDay);
			rentInformation.add(new JLabel("Enter days you wish to rent?"));
			rentInformation.add(daysRentList);

			int OkCancel = JOptionPane.showConfirmDialog(null, rentInformation,
					"Enter License Plate To Rent", JOptionPane.OK_CANCEL_OPTION);
			if (OkCancel == JOptionPane.OK_OPTION) 
			{
				pickUpDate = pickUpDay.getText();
				carLicensePlate = (String) licensePlateList.getSelectedItem();
				daysRented = (String) daysRentList.getSelectedItem();
				if (rentTo)
				{
					newQuery("INSERT INTO RENTAL VALUES('" + carLicensePlate + 
							"', '" + driversLicense + "', CURDATE(), '" + 
							pickUpDate + "', DATE_ADD('" + pickUpDate + 
							"', INTERVAL " + daysRented + " DAY), NULL," +
							DOWN_PAYMENT + ", " + false + ", " + updatedAt + ");");
					rentTo = false;
					rented = true;
				}
				else
				{
					JOptionPane.showMessageDialog(null,
							"Sorry, But you cannot rent.", "Renting Error",
							JOptionPane.ERROR_MESSAGE);
				}
				userCustomerInfoButton.doClick();
			}
		}
		else if (e.getSource() == userRequestCarButton)
		{
			// User requested car.
			JTextField makeField = new JTextField(10);
			JTextField modelField = new JTextField(10);
			String requestedColor = null;
			String requestedYear = null;
			// Panel to hold relative information.
			JPanel myPanel = new JPanel();
			myPanel.setLayout(new GridLayout(0,1));
			myPanel.add(new JLabel("Make:"));
			myPanel.add(makeField);
			myPanel.add(Box.createHorizontalStrut(10)); // a spacer
			myPanel.add(new JLabel("Model:"));
			myPanel.add(modelField);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(new JLabel("Year:"));
			myPanel.add(adminYearList);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(new JLabel("Color:"));
			myPanel.add(colorsList);
			myPanel.add(Box.createHorizontalStrut(10));

			int result = JOptionPane.showConfirmDialog(null, myPanel, 
					"Request a Car", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) 
			{
				requestedColor = (String) colorsList.getSelectedItem();
				requestedYear = (String) adminYearList.getSelectedItem();
				try 
				{
					tableModel.setQuery("INSERT INTO Suggest_Car Values ('" 
							+ makeField.getText() + "', '" + modelField.getText()
							+ "', '" + requestedYear + "', '" + requestedColor
							+ "');");
				} // end try
				catch ( SQLException sqlException ) 
				{
					JOptionPane.showMessageDialog( null, 
							sqlException.getMessage(), "Database error", 
							JOptionPane.ERROR_MESSAGE );
					// try to recover from invalid user query 
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch ( SQLException sqlException2 ) 
					{
						JOptionPane.showMessageDialog( null, 
								sqlException2.getMessage(), "Database error", 
								JOptionPane.ERROR_MESSAGE );

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit( 1 ); // terminate application
					} // end inner catch                   
				} // end outer catch
			}
		}
		else if (e.getSource() == userReturnButton)
		{ 
			System.out.println("You want to return!");
			if (rented)
			{
				// Query Customer information.
				try 
				{
					tableModel.setQuery("SELECT FullName, Car.LicensePlate, "
							+ "Make, Model, DateRented, PickUpDate, DueDate, "
							+ "ReturnDate, Car_Spec.Price, OverDue FROM Customer, "
							+ "Rental, Car, Car_Spec WHERE Customer.DriversLicense = '" 
							+ driversLicense + "' AND Customer.DriversLicense" 
							+ " = Rental.DriversLicense AND Rental.LicensePlate" 
							+ " = Car.LicensePlate AND Car.LicensePlate = " 
							+ "Car_Spec.LicensePlate;");

					content.first(contentPanel);
					options.last(optionsPanel);
				} // end try
				catch (SQLException sqlException) 
				{
					JOptionPane.showMessageDialog(null, sqlException.getMessage(),
							"Database error", JOptionPane.ERROR_MESSAGE);
					// try to recover from invalid user query
					// by executing default query
					try 
					{
						tableModel.setQuery(DEFAULT_QUERY);
					} // end try
					catch (SQLException sqlException2) 
					{
						JOptionPane.showMessageDialog(null,
								sqlException2.getMessage(), "Database error",
								JOptionPane.ERROR_MESSAGE);

						// ensure database connection is closed
						tableModel.disconnectFromDatabase();

						System.exit(1); // terminate application
					} // end inner catch
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You Have Not Rented A Car.",
						"Database error", JOptionPane.ERROR_MESSAGE);
				userCustomerInfoButton.doClick();
			}
		}
		else if(e.getSource() == cancelReservationButton) //TODO
		{
			JPanel cancel = new JPanel();
			cancel.setLayout(new GridLayout(0,1));
			cancel.add(new JLabel("Cancel Rental for:"));
			cancel.add(new JLabel(fullName));
			cancel.add(new JLabel("Drivers License:"));
			cancel.add(new JLabel(driversLicense));
			cancel.add(new JLabel("License Plate:"));
			cancel.add(new JLabel(carLicensePlate));
			cancel.add(Box.createVerticalStrut(10));
			cancel.add(new JLabel("Click OK to confirm cancelation."));

			if (rented)
			{
				int cancel2 = JOptionPane.showConfirmDialog(null, cancel,
						"Cancel Reservation",JOptionPane.OK_CANCEL_OPTION);
				if(cancel2 == JOptionPane.OK_OPTION)
				{
					newQuery("DELETE from rental where DriversLicense = '" 
							+ driversLicense + "' And LicensePlate = '" + 
							carLicensePlate + "';");
					rented = false;
					userCustomerInfoButton.doClick();
					content.first(contentPanel);
					options.last(optionsPanel);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"You have not rented a car yet.",
						"Alert!", JOptionPane.ERROR_MESSAGE);
				userRentButton.doClick();
			}
		}
		else if (e.getSource() == userReturnThisCarButton)
		{
			JTextField licensePlateField = new JTextField(10);
			JTextField milesField = new JTextField(10);
			JTextField paymentField = new JTextField(10);
			JTextField commentReviewField = new JTextField(10);

			JPanel myPanel = new JPanel();
			Box accidentBox = Box.createHorizontalBox();
			accidentBox.add(YesButton);
			accidentBox.add(Box.createHorizontalStrut(10));	// a spacer
			accidentBox.add(NoButton);

			myPanel.setLayout(new GridLayout(0, 1));
			myPanel.add(Box.createHorizontalStrut(10)); 
			myPanel.add(new JLabel("License Plate:"));
			myPanel.add(new JLabel(carLicensePlate));
			myPanel.add(Box.createHorizontalStrut(10));

			// Group the radio buttons.
			ButtonGroup group = new ButtonGroup();
			group.add(buttonOne);
			group.add(buttonTwo);
			group.add(buttonThree);
			group.add(buttonFour);
			group.add(buttonFive);
			group.clearSelection();

			// Put the radio buttons in a column in a panel.
			JPanel radioPanel = new JPanel(new GridLayout(1, 0));
			radioPanel.add(buttonOne);
			radioPanel.add(buttonTwo);
			radioPanel.add(buttonThree);
			radioPanel.add(buttonFour);
			radioPanel.add(buttonFive);

			// Register a listener for the radio buttons.
			buttonOne.addActionListener(this);
			buttonTwo.addActionListener(this);
			buttonThree.addActionListener(this);
			buttonFour.addActionListener(this);
			buttonFive.addActionListener(this);

			myPanel.add(new JLabel("Rate your experience"));
			myPanel.add(radioPanel);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(new JLabel("Comment Review"));
			myPanel.add(commentReviewField);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(new JLabel("How many miles did you drive?"));
			myPanel.add(milesField);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(userAccidentLabel); // TODO
			myPanel.add(accidentBox);
			myPanel.add(Box.createHorizontalStrut(10));
			accidentPanel.add(new JLabel("Accident Comment"));
			accidentPanel.add(accidentCommentField);
			myPanel.add(new JLabel("Payment: "));

			// Box to make payment.
			Box paymentBox = Box.createHorizontalBox();
			paymentBox.add(new JLabel("$"));
			paymentBox.add(paymentField);

			myPanel.add(paymentBox);
			myPanel.add(Box.createHorizontalStrut(10));
			myPanel.add(ExtendButton);

			ExtendButton.addActionListener(this);
			YesButton.addActionListener(this);
			NoButton.addActionListener(this);

			System.out.println("accident: " + accident);

			// Return Car Panel Ok and Cancel.
			int result = JOptionPane.showConfirmDialog(null, myPanel,
					"Return Car (Please Enter Information)",
					JOptionPane.OK_CANCEL_OPTION);
			int matches = 0;

			try 
			{
				Class.forName(JDBC_DRIVER).newInstance();
				java.sql.Connection conn = DriverManager.getConnection(
						DATABASE_URL, USERNAME, PASSWORD);
				java.sql.Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT * FROM RENTAL " +
						"WHERE DriversLicense = '" + driversLicense + "';");

				// compare entered value with every dl value in customer table
				// Date DateRented = null;
				Date PickUpDate = null;
				// Date DueDate = null;
				boolean Overdue = false;

				while (res.next()) 
				{
					carLicensePlate = res.getString("LicensePlate");
					dateRented = res.getString("DateRented");
					PickUpDate = res.getDate("PickUpDate");
					dueDate = res.getString("DueDate");
					Overdue = res.getBoolean("OverDue");
					matches++;
				}

				if (result == JOptionPane.OK_OPTION && matches == 1) 
				{
					/*
					 * Will need to check if values are good(we have code that
					 * does just that ) SQL code go here
					 */
					try 
					{
						st.executeUpdate("INSERT INTO RETURNED Values('"
								+ carLicensePlate + "','"
								+ driversLicense + "','" + dateRented + "','"
								+ PickUpDate + "','" + dueDate + "',"
								+ "CURDATE()" + ",'" + ratingSelected + "','"
								+ commentReviewField.getText() + "'," + Overdue
								+ ",'" + milesField.getText() + "','"
								+ accident + "','" + accidentComment + "','"
								+ paymentField.getText() + "'," + "CURDATE());");
						rentTo = true;
						rented = false;
						userCustomerInfoButton.doClick();
						conn.close();
					} 
					catch (Exception exc) 
					{
						exc.printStackTrace();
					}
				}
				if (matches == 0 && result == JOptionPane.OK_OPTION)
				{
					JOptionPane.showMessageDialog(null,
							"This car is not rented or not rented by you.",
							"Alert!", JOptionPane.ERROR_MESSAGE);
				}
				conn.close();
			} 
			catch (Exception exc) 
			{
				exc.printStackTrace();
			}
		} 
		else if (e.getSource() == ExtendButton) 
		{
			Box extendDateBox = Box.createHorizontalBox();
			String[] licensePlate_arr = addToArray("select licenseplate from rental where driverslicense = "
					+ driversLicense);
			JComboBox dayList = new JComboBox(daysRent_arr);
			JComboBox licenseList = new JComboBox(licensePlate_arr);
			JLabel daysLabel = new JLabel("Days");
			JLabel licensePlateLabel = new JLabel("License Plate");
			extendDateBox.add(daysLabel);
			extendDateBox.add(dayList);
			extendDateBox.add(Box.createHorizontalStrut(10));
			extendDateBox.add(licensePlateLabel);
			extendDateBox.add(licenseList);

			int result1 = JOptionPane.showConfirmDialog(null, extendDateBox,
					"Extend Due Date", JOptionPane.OK_CANCEL_OPTION);
			if (result1 == JOptionPane.OK_OPTION) 
			{
				String selectedDays = "'" + (String) dayList.getSelectedItem()
						+ "'";
				String selectedLicense = "'"
						+ (String) licenseList.getSelectedItem() + "'";

				System.out.println("selectedDays: " + selectedDays);
				System.out.println("selectedLicense: " + selectedLicense);
				System.out.println("result == ok");

				try 
				{
					Class.forName(JDBC_DRIVER).newInstance();
					java.sql.Connection conn = DriverManager.getConnection(
							DATABASE_URL, USERNAME, PASSWORD);
					java.sql.Statement st = conn.createStatement();

					st.executeUpdate("UPDATE RENTAL set DueDate = DueDate + INTERVAL"
							+ selectedDays
							+ "DAY where LicensePlate ="
							+ selectedLicense
							+ "AND driversLicense = "
							+ driversLicense + ";");
					conn.close();
				} 
				catch (Exception exc) 
				{
					exc.printStackTrace();
				}
			}
		} 
		else if (e.getSource() == YesButton) 
		{
			userAccidentLabel.setText("Accident Reported.");
			accident = 1;
			int result2 = JOptionPane.showConfirmDialog(null, accidentPanel,
					"Accident Report", JOptionPane.OK_CANCEL_OPTION);

			if (result2 == JOptionPane.OK_OPTION) 
			{
				accidentComment = accidentCommentField.getText();
			}
			accidentCommentField.setText("");
		} 
		else if (e.getSource() == NoButton) 
		{
			userAccidentLabel.setText("No Accident.");
			accident = 0;
		}
		else if (buttonOne.isSelected()) 
		{
			ratingSelected = 1;
		} 
		else if (buttonTwo.isSelected()) 
		{
			ratingSelected = 2;
		} 
		else if (buttonThree.isSelected()) 
		{
			ratingSelected = 3;
		} 
		else if (buttonFour.isSelected())
		{
			ratingSelected = 4;
		}
		else if (buttonFive.isSelected()) 
		{
			ratingSelected = 5;
		} 
	}

	@Override
	/**
	 * Sign-in Sign-out features.
	 */
	public void itemStateChanged(ItemEvent e) 
	{
		CardLayout buttons = (CardLayout)(buttonPanel.getLayout());
		CardLayout content = (CardLayout)(contentPanel.getLayout());
		CardLayout options = (CardLayout)(optionsPanel.getLayout());
		
		if(e.getStateChange() == ItemEvent.SELECTED)
		{
			signInButton.setText("Sign Out");

			if (isNew)	// Not a new user; did not have to register
			{			// (already in system).	
				buttons.last(buttonPanel); 
				content.last(contentPanel);
			}
			else
			{
				buttons.last(buttonPanel); 
				buttons.previous(buttonPanel);
				content.first(contentPanel); 
				options.first(optionsPanel);
			}
		}
		else
		{
			signInButton.setText("Sign In");
			buttons.first(buttonPanel);
			content.first(contentPanel);
			options.first(optionsPanel);

			fullName = null;
			driversLicense = null;
			creditCard = null;
			carLicensePlate = null;
			pickUpDate = null;
			age = null;
			rentTo = false;
			dueDate = null;
			daysRented = null;
			isNew = true;
			rented = false;

			userNameField.setText("");
			loginField.setText("");	

			newQuery("Select * From car Where CAR.LicensePlate" +
					" NOT IN (Select LicensePlate From rental);");
			content.first(contentPanel);
			options.first(optionsPanel);
		}
	}

	/**
	 * Submits new query through tableModel to the database.
	 * @param query. New query to submit.
	 */
	public void newQuery(String query) 
	{
		try 
		{
			tableModel.setQuery(query);
		} // end try
		catch (SQLException sqlException) 
		{
			JOptionPane.showMessageDialog(null, sqlException.getMessage(),
					"Database error", JOptionPane.ERROR_MESSAGE);
			// try to recover from invalid user query
			// by executing default query
			try 
			{
				tableModel.setQuery(DEFAULT_QUERY);
			} // end try
			catch (SQLException sqlException2) 
			{
				JOptionPane.showMessageDialog(null,
						sqlException2.getMessage(), "Database error",
						JOptionPane.ERROR_MESSAGE);

				// ensure database connection is closed
				tableModel.disconnectFromDatabase();

				System.exit(1); // terminate application
			} // end inner catch
		}
	}

	/**
	 * Submits a query through the tableModel to the database.
	 * @param query. Query to submit.
	 * @param s. Additional parameter for the query.
	 */
	public void changeQuery(String query, String s) 
	{
		try 
		{
			tableModel.setQuery(query + s + ";");
		} // end try
		catch (SQLException sqlException) 
		{
			JOptionPane.showMessageDialog(null, sqlException.getMessage(),
					"Database error", JOptionPane.ERROR_MESSAGE);
			// try to recover from invalid user query
			// by executing default query
			try 
			{
				tableModel.setQuery(DEFAULT_QUERY);
			} // end try
			catch (SQLException sqlException2) 
			{
				JOptionPane.showMessageDialog(null,
						sqlException2.getMessage(), "Database error",
						JOptionPane.ERROR_MESSAGE);

				// ensure database connection is closed
				tableModel.disconnectFromDatabase();

				System.exit(1); // terminate application
			} // end inner catch
		}
	}

	// execute application
	public static void main( String args[] ) 
	{
		new DisplayQueryResults();     
	} // end main


} // end class DisplayQueryResults



/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
