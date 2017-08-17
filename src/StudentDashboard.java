import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;


public class StudentDashboard extends JFrame {

	private JButton button;
	private JTable table;
	private final DefaultTableModel tableModel = new DefaultTableModel();
	JPanel bottom = new JPanel(new BorderLayout());

	private JTextField nameField = new JTextField(30);
	private JTextField emailField = new JTextField(30);
	private JTextField passField = new JTextField(30);
	private JTextField courseField = new JTextField(30);
	private JTextField telField = new JTextField(30);
	private JTextField typeField = new JTextField(30);
	private JTextField uniqueNoField = new JTextField(30);

	private JButton updateButton = new JButton("EDIT MY INFO WITH UNIQUE-NO");

	private JButton endButton = new JButton("LOGOUT");
	private JButton exitButton = new JButton("EXIT");

	private JFrame frame;
	private String unique;
	private Component C;

	public Student setFieldsData(Student s) {
		s.setName(nameField.getText());
		s.setEmail(emailField.getText());
		s.setPass(passField.getText());
		s.setCourse(courseField.getText());
		s.setTel(telField.getText());
		s.setType(typeField.getText());
		return s;
	}

	public StudentDashboard() {
	};

	public StudentDashboard(String email) {
		// to get uniqueStudentNumber from Database....
		DB db = new DB();
		unique = db.returnUniqueId(email);

		// Main GUI section...
		frame = new JFrame("** Student **");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		addComponentsToPane(c, unique);

		frame.pack();
		// setSize and setLocation has to be after pack()
		frame.setSize(960, 480);
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
	}

	// adding components..
	private void addComponentsToPane(Container c, String unique) {

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				DB db = new DB();
				db.searchByUnique(unique, tableModel);
				return null;
			}
		}.execute();

		table = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		c.add(new JScrollPane(table));
		bottom.add(initFields(), BorderLayout.PAGE_START);
		C = new JLabel("** In case you edit your status, you are required to put correct your Student No. **",
				JLabel.CENTER);

		bottom.add(C, BorderLayout.CENTER);
		bottom.add(initButtons(), BorderLayout.PAGE_END);
		c.add(bottom, BorderLayout.PAGE_END);

	}

	// controlling part - filed section.
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
						db.loadData(tableModel);
						clearFields();
						return null;
					}
				}.execute();
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
