import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DB {

	Connection con;
	PreparedStatement pst;
	ResultSet rs;

	private int lastNoOfStudent;
	private final DefaultTableModel tb = new DefaultTableModel();

	// connect to DB.
	public DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// methode checking for userId and password
	public Boolean checkLogin(String user, String pswd) {
		try {
			pst = con.prepareStatement("select * from guirep where email=? and pass=?");
			pst.setString(1, user);
			pst.setString(2, pswd);

			rs = pst.executeQuery();
			if (rs.next()) {
				// TRUE if the query founds any corresponding data
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("error while validating" + e);
			return false;
		}
	}

	// distinguish type of user - admin and student. return user's Type Value.
	public String checkType(String user) {
		String tpVal = null;
		try {
			pst = con.prepareStatement("select type from guirep where email=?");
			pst.setString(1, user);
			rs = pst.executeQuery();
			if (rs.next()) {
				tpVal = rs.getString("type");
				// System.out.println("category = " + tpVal);
			}
		} catch (Exception e) {
			System.out.println("error while validating" + e);
		}
		return tpVal;
	}

	public int findLastNoOfStudent() {
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
				// System.out.println(lastNoOfStudent);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return lastNoOfStudent;
	}

	public void insertUser(Student s) {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "INSERT INTO guirep (name, email, pass, course, tel, type, uniqueNo) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);

			statement.setString(1, s.getName());
			statement.setString(2, s.getEmail());
			statement.setString(3, s.getPass());
			statement.setString(4, s.getCourse());
			statement.setString(5, s.getTel());
			statement.setString(6, s.getType());

			// findlast number of student then, set last student no for new user. so
			// that they can get number - last number +1
			UniqueIdGenerator.setLastNumberOfStudent(findLastNoOfStudent());
			// set number for new student.
			statement.setInt(7, new UniqueIdGenerator().id);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new user was inserted successfully!");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public void updateUser(Student s) {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "UPDATE guirep SET name=?, email=?, pass=?, course=?, tel=?, type=? WHERE uniqueNo=?";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, s.getName());
			statement.setString(2, s.getEmail());
			statement.setString(3, s.getPass());
			statement.setString(4, s.getCourse());
			statement.setString(5, s.getTel());
			statement.setString(6, s.getType());
			statement.setString(7, Integer.toString(s.getUniqueId()));

			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("An existing user was updated successfully!");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	// delete with name - methode..
	public void deleteUser(Student s) {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "DELETE FROM guirep WHERE name=?";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, s.getName());

			int rowsDeleted = statement.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("A user was deleted successfully!");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	// search by course methode..
	public DefaultTableModel searchByCourse(String course) {

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "select * from guirep where course=?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, course);

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

			tb.setDataVector(data, columnNames);
			

		} catch (Exception e) {
			System.out.println(e);
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}
		return tb;
		
	}

	// search by name methode..
	public DefaultTableModel searchByName(String name) {

		String userEmail = "";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root")) {

			String sql = "select * from guirep where name=?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, name);

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

			tb.setDataVector(data, columnNames);
			// System.out.println("%%");

			// methode has to be addded later ########

			// frame.dispose();
			// new StudentDashboard(userEmail);

			// System.out.println("&&%");

		} catch (Exception e) {
			System.out.println(e);
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}
		
		return tb;

	}

}
