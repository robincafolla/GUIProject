import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

public class AdminDashboard extends JPanel {

	private DefaultTableModel tableModel = new DefaultTableModel();
	public static boolean RIGHT_TO_LEFT = false;

	JButton button = new JButton();
	JTable table;
	
	JPanel bottom = new JPanel(new BorderLayout());

	private JTextField nameField = new JTextField(30);
	private JTextField emailField = new JTextField(30);
	private JTextField passField = new JTextField(30);
	private JTextField courseField = new JTextField(30);
	private JTextField telField = new JTextField(30);
	private JTextField typeField = new JTextField(30);
	private JTextField uniqueNoField = new JTextField(30);

	private JButton createButton = new JButton("NEW USER");
	private JButton updateButton = new JButton("EDIT USER");
	private JButton deleteButton = new JButton("DELETE USER WITH NAME");
	private JButton searchButton = new JButton("SEARCH WITH COURSE");
	private JButton nameSearchButton = new JButton("SEARCH WITH NAME");
	private JFrame frame;

	private JButton endButton = new JButton("LOGOUT");
	private JButton exitButton = new JButton("EXIT");
	private JLabel C;

	public Student setFieldsData(Student s) {
		s.setName(nameField.getText());
		s.setEmail(emailField.getText());
		s.setPass(passField.getText());
		s.setCourse(courseField.getText());
		s.setTel(telField.getText());
		s.setType(typeField.getText());
		return s;
	}

	// Constructor
	public AdminDashboard() {

		frame = new JFrame("** Admin **");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		addComponentsToPane(c);

		frame.pack();
		// setSize() and setLocation() has to be after pack() to show every elements
		// at the first time.
		frame.setSize(1024, 640);
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
	}

	// Adding components to Pane..
	public void addComponentsToPane(Container pane) {

		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}

		// Top Button to show every infomation in the system.
		button = new JButton("## CLICK TO SEE ALL STUDENTS AND MEMBERS INFO ##");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						return null;
					}
				}.execute();
			}
		});
		pane.add(button, BorderLayout.PAGE_START);

		// main Jtable - center field for showing result.
		table = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		pane.add(new JScrollPane(table), BorderLayout.CENTER);

		// bottom input part for controling database.
		bottom.add(initFields(), BorderLayout.PAGE_START);

		C = new JLabel(
				"** In case you edit student's status, you are required to put Unique Student Id which has to be correct**",
				JLabel.CENTER);

		bottom.add(C, BorderLayout.CENTER);
		bottom.add(initButtons(), BorderLayout.PAGE_END);
		pane.add(bottom, BorderLayout.PAGE_END);

	}

	// contoroling part - Filed input section.
	private JPanel initFields() {
		JPanel panel = new JPanel();

		panel.setLayout(new MigLayout());

		panel.add(new JLabel("Name"), "align label");
		panel.add(nameField, "wrap");
		nameField.setEnabled(true);

		panel.add(new JLabel("Email"), "align label");
		panel.add(emailField, "wrap");
		emailField.setEnabled(true);

		panel.add(new JLabel("Password"), "align label");
		panel.add(passField, "wrap");
		passField.setEnabled(true);

		panel.add(new JLabel("Course"), "align label");
		panel.add(courseField, "wrap");
		courseField.setEnabled(true);

		panel.add(new JLabel("Phone"), "align label");
		panel.add(telField, "wrap");
		telField.setEnabled(true);

		panel.add(new JLabel("Type"), "align label");
		panel.add(typeField, "wrap");
		typeField.setEnabled(true);

		panel.add(new JLabel("UniqueStudentNumber"), "align label");
		panel.add(uniqueNoField, "wrap");
		uniqueNoField.setEnabled(true);

		return panel;
	}

	// contoroling part - Button input section.
	private JPanel initButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

		// 1. Create Button.
		panel.add(createButton);
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// place for methode..
				Student s = new Student();
				setFieldsData(s);
				DB db = new DB();
				db.insertUser(s);

				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						clearFields();
						return null;
					}
				}.execute();
			}
		});

		// 2 Update Button.
		panel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// methodes...
				Student s = new Student();
				setFieldsData(s);
				s.setUniqueId(Integer.parseInt(uniqueNoField.getText()));

				DB db = new DB();
				db.updateUser(s);

				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						clearFields();
						return null;
					}
				}.execute();
			}
		});

		// 3.Delete Button.
		panel.add(deleteButton);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Methode for deleting..
				Student s = new Student();
				setFieldsData(s);
				DB db = new DB();
				db.deleteUser(s);

				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						clearFields();
						return null;
					}
				}.execute();
			}
		});

		// 4.Course Search Button.
		panel.add(searchButton);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DB db = new DB();
				db.searchByCourse(courseField.getText(), tableModel);

			}
		});

		// 5.Name Search Button.
		panel.add(nameSearchButton);
		nameSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// searchByName
				DB db = new DB();
				frame.dispose();
				new StudentDashboard(db.searchByName(nameField.getText()));
			}
		});

		// 6.Log out
		panel.add(endButton);
		endButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new Login();
			}
		});

		// 7.exit
		panel.add(exitButton);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});

		return panel;
	}

	// load every data from Mysql...
	private void loadData() {

		button.setEnabled(false);

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root");
				Statement stmt = conn.createStatement()) {

			ResultSet rs = stmt.executeQuery("select * from guirep;");
			ResultSetMetaData metaData = rs.getMetaData();

			// System.out.println("LoadData works");

			// Names of columns
			Vector<String> columnNames = new Vector<String>();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNames.add(metaData.getColumnName(i));
			}

			// Data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for (int i = 1; i <= columnCount; i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
			}

			tableModel.setDataVector(data, columnNames);

		} catch (Exception e) {
			System.out.println(e);
		}

		button.setEnabled(true);
	}

	// clearing filed...
	private void clearFields() {
		nameField.setText(null);
		emailField.setText(null);
		passField.setText(null);
		courseField.setText(null);
		telField.setText(null);
		typeField.setText(null);
		uniqueNoField.setText(null);
	}

}