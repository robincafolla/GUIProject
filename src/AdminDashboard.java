import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Vector;

public class AdminDashboard extends JPanel {

	private final DefaultTableModel tableModel = new DefaultTableModel();
	public static boolean RIGHT_TO_LEFT = false;

	JButton button = new JButton();
	JTable table;

	JPanel bottom = new JPanel(new BorderLayout());

	// private JTextField idField = new JTextField(10);
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

	private int lastNoOfStudent;

	public AdminDashboard() {

		frame = new JFrame("** Admin **");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		addComponentsToPane(c);

		frame.pack();
		// setSize and setLocation has to be after pack()
		frame.setSize(1024, 640);
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);

	}

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
		/*
		 * panel.add(new JLabel("ID"), "align label"); panel.add(idField, "wrap");
		 * idField.setEnabled(true);
		 */
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

		panel.add(createButton);
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertUser();
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						return null;
					}
				}.execute();
			}
		});

		panel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUser();
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						return null;
					}
				}.execute();
			}
		});

		panel.add(deleteButton);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteUser();
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						loadData();
						return null;
					}
				}.execute();
			}
		});

		panel.add(searchButton);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchByCourse();
			}
		});

		panel.add(nameSearchButton);
		nameSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchByName();
			}
		});

		panel.add(endButton);
		endButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new Login();
			}
		});

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
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}

		button.setEnabled(true);

		// LOG.info("END loadData method");
	}
	
	// Method that you can find Last student Number from Mysql...
	private int findLastNoOfStudent() {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "SELECT * FROM guirep ORDER BY id DESC LIMIT 1;";
			PreparedStatement statement = conn.prepareStatement(sql);

			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();

			int columnCount = metaData.getColumnCount();
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for (int i = 1; i <= columnCount; i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
				lastNoOfStudent = rs.getInt(8);
				//System.out.println(lastNoOfStudent);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return lastNoOfStudent;

	}

	private void insertUser() {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "INSERT INTO guirep (name, email, pass, course, tel, type, uniqueNo) VALUES (?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, nameField.getText());
			statement.setString(2, emailField.getText());
			statement.setString(3, passField.getText());
			statement.setString(4, courseField.getText());
			statement.setString(5, telField.getText());
			statement.setString(6, typeField.getText());
			findLastNoOfStudent(); // findlast number of student
			// set last student no for new user. so that they can get number -  last number +1
			UniqueIdGenerator.setLastNumberOfStudent(findLastNoOfStudent()); 
			// set number for new student. 
			statement.setInt(7, new UniqueIdGenerator().id);

			// statement.setInt(7, new Random().nextInt(Integer.MAX_VALUE) + 1);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new user was inserted successfully!");
			}

			clearFields();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}
	// updater methode.
	private void updateUser() {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "UPDATE guirep SET name=?, email=?, pass=?, course=?, tel=?, type=? WHERE uniqueNo=?";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, nameField.getText());
			statement.setString(2, emailField.getText());
			statement.setString(3, passField.getText());
			statement.setString(4, courseField.getText());
			statement.setString(5, telField.getText());
			statement.setString(6, typeField.getText());
			statement.setString(7, uniqueNoField.getText());

			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("An existing user was updated successfully!");
			}

			clearFields();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}
	// delete with name - methode..
	private void deleteUser() {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "DELETE FROM guirep WHERE name=?";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, nameField.getText());

			int rowsDeleted = statement.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("A user was deleted successfully!");
			}

			clearFields();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}
	//search by course methode..
	private void searchByCourse() {

		button.setEnabled(false);

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "select * from guirep where course=?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, courseField.getText());

			ResultSet rs = statement.executeQuery();
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
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}

		button.setEnabled(true);
		clearFields();
	}
	
	// search by name methode..
	private void searchByName() {

		button.setEnabled(false);
		String userEmail = "";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "select * from guirep where name=?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, nameField.getText());

			ResultSet rs = statement.executeQuery();
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
					userEmail = rs.getString("email");
				}
				data.add(vector);
			}

			tableModel.setDataVector(data, columnNames);
			// System.out.println("%%");

			frame.dispose();
			new StudentDashboard(userEmail);
			// System.out.println("&&%");

		} catch (Exception e) {
			System.out.println(e);
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}

		button.setEnabled(true);

		clearFields();

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

	/*
	 * 
	 * public static void main(String[] args) { /* Use an appropriate Look and
	 * Feel try { // UIManager.setLookAndFeel(
	 * "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	 * UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); }
	 * catch (UnsupportedLookAndFeelException ex) { ex.printStackTrace(); } catch
	 * (IllegalAccessException ex) { ex.printStackTrace(); } catch
	 * (InstantiationException ex) { ex.printStackTrace(); } catch
	 * (ClassNotFoundException ex) { ex.printStackTrace(); }
	 * 
	 * // creating and showing this application's GUI.
	 * javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {
	 * new AdminDashboard(); } }); }
	 * 
	 */
}