import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DB {

	Connection con;
	PreparedStatement pst;
	ResultSet rs;

	private int lastNoOfStudent;

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
		try (PreparedStatement pst = con.prepareStatement("select * from guirep where email=? and pass=?")) {
			pst.setString(1, user);
			pst.setString(2, pswd);

			rs = pst.executeQuery();
			if (rs.next()) { // TRUE if the query founds any corresponding data
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
		try (PreparedStatement pst = con.prepareStatement("select type from guirep where email=?")) {
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
		try (PreparedStatement pst = con.prepareStatement("SELECT * FROM guirep ORDER BY id DESC LIMIT 1;")) {

			ResultSet rs = pst.executeQuery();
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

	public void insertUser(User s) {
		String sql = "INSERT INTO guirep (name, email, pass, course, tel, type, uniqueNo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pst = con.prepareStatement(sql);) {
			pst.setString(1, s.getName());
			pst.setString(2, s.getEmail());
			pst.setString(3, s.getPass());
			pst.setString(4, s.getCourse());
			pst.setString(5, s.getTel());
			pst.setString(6, s.getType());

			// findlast number of student then, set last student no for new user. so
			// that they can get number - last number +1
			UniqueIdGenerator.setLastNumberOfStudent(findLastNoOfStudent());
			// set number for new student.
			pst.setInt(7, new UniqueIdGenerator().id);

			int rowsInserted = pst.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new user was inserted successfully!");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void updateUser(User s) {
		String sql = "UPDATE guirep SET name=?, email=?, pass=?, course=?, tel=?, type=? WHERE uniqueNo=?";
		try (PreparedStatement statement = con.prepareStatement(sql)) {
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
	public void deleteUser(User s) {
		String sql = "DELETE FROM guirep WHERE name=?";
		try (PreparedStatement statement = con.prepareStatement(sql);) {
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
	public void searchByCourse(String course, DefaultTableModel tableModel) {
		String sql = "select * from guirep where course=?;";
		try (PreparedStatement statement = con.prepareStatement(sql);) {
			statement.setString(1, course);

			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();

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
	}

	// search by name methode..
	public String searchByName(String name) {
		String userEmail = "";
		String sql = "select * from guirep where name=?;";
		try (PreparedStatement statement = con.prepareStatement(sql)) {

			statement.setString(1, name);

			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();

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

		} catch (Exception e) {
			System.out.println(e);
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}
		return userEmail;
	}

	public String returnUniqueId(String email) {
		String uniqueId = "";
		String sql = "select * from guirep where email=?;";

		try (PreparedStatement statement = con.prepareStatement(sql);) {
			statement.setString(1, email);

			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();

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
					uniqueId = rs.getString("uniqueNo");
				}
				data.add(vector);
			}

		} catch (Exception e) {
			System.out.println(e);
			// LOG.log(Level.SEVERE, "Exception in Load Data", e);
		}

		return uniqueId;

	}

	public void loadData(DefaultTableModel tableModel) {

		try (Statement stmt = con.createStatement()) {

			ResultSet rs = stmt.executeQuery("select * from guirep;");
			ResultSetMetaData metaData = rs.getMetaData();

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
	}

	public void searchByUnique(String unique, DefaultTableModel tableModel) {
		String sql = "select * from guirep where uniqueNo=?;";

		try (PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, unique);

			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();

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
	}

}
