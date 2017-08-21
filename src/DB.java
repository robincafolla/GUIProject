import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DB {

	private static ComboPooledDataSource comboPooledDataSource;
	private static ResultSet rs;
	private static int lastNoOfStudent;

	
	/*
	 * 
	 * Connect to DB. using Connection Pooling. 
	 * Would there be benefit when I use Connection Pooling over normal JDBC ??? 
	 *
	 */	
	
	
	public DB() {
		try {
			comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
			comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/guiproj");
			comboPooledDataSource.setUser("root");
			comboPooledDataSource.setPassword("root");
			
		} catch (Exception e) {
			System.out.println(e);
		} 
	}
	
	

	/*
	 * 
	 * As you can see, every methods use Connection to DB in 'try with resourse' block.(no need to close()) 
	 *  
	 * What if I make Connection object, and re-use it for several methods. can it be possible? 
	 * if so, how do I close the connection? 
	 * 
	 */
	
	
	
	// Methode checking for userId and password
	public static Boolean checkLogin(String user, String pswd) {
		try (Connection con = comboPooledDataSource.getConnection(); 
				PreparedStatement pst = con.prepareStatement("select * from guirep where email=? and pass=?")) {
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// distinguish type of user - admin and student. return user's Type Value.
	public static String checkType(String user) {
		String tpVal = null;
		try (Connection con = comboPooledDataSource.getConnection(); 
				PreparedStatement pst = con.prepareStatement("select type from guirep where email=?")) {
			pst.setString(1, user);
			rs = pst.executeQuery();
			if (rs.next()) {
				tpVal = rs.getString("type");
				// System.out.println("category = " + tpVal);
			}
		} catch (Exception e) {
			System.out.println("error while validating" + e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tpVal;
	}

	public static int findLastNoOfStudent() {
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement("SELECT * FROM guirep ORDER BY id DESC LIMIT 1;")) {

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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastNoOfStudent;
	}

	public static void insertUser(User s) {
		String sql = "INSERT INTO guirep (name, email, pass, course, tel, type, uniqueNo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)) {
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

	public static void updateUser(User s) {
		String sql = "UPDATE guirep SET name=?, email=?, pass=?, course=?, tel=?, type=? WHERE uniqueNo=?";
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {
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
	public static void deleteUser(User s) {
		String sql = "DELETE FROM guirep WHERE name=?";
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {
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
	public static void searchByCourse(String course, DefaultTableModel tableModel) {
		String sql = "select * from guirep where course=?;";
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, course);

			rs = statement.executeQuery();
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// search by name methode..
	public static String searchByName(String name) {
		String userEmail = "";
		String sql = "select * from guirep where name=?;";
		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {

			statement.setString(1, name);

			rs = statement.executeQuery();
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userEmail;
	}

	public static String returnUniqueId(String email) {
		String uniqueId = "";
		String sql = "select * from guirep where email=?;";

		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, email);

			rs = statement.executeQuery();
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return uniqueId;

	}

	public static void loadData(DefaultTableModel tableModel) {

		try (Connection con = comboPooledDataSource.getConnection();
				Statement stmt = con.createStatement()) {

			rs = stmt.executeQuery("select * from guirep;");
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void searchByUnique(String unique, DefaultTableModel tableModel) {
		String sql = "select * from guirep where uniqueNo=?;";

		try (Connection con = comboPooledDataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, unique);

			rs = statement.executeQuery();
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
