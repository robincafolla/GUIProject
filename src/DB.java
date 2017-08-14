import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DB {
	
	Connection con;
	PreparedStatement pst;
	ResultSet rs;
	
	//connect to DB.
	
	public DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/guiproj", "root", "root");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//check method with userId and password

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
				//System.out.println("category = " + tpVal);
			} 
		} catch (Exception e) {
			System.out.println("error while validating" + e);		
		}
		return tpVal;
	}
	
	
}
