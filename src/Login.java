import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class Login extends JFrame implements ActionListener {
	
	private JPasswordField psField;
	private JTextField idField;
	private JButton button;
	private DB db;
	private JFrame login;

	public Login() {
		// to connect mysql..
		db = new DB();
		login = new JFrame("## Please Log In... ##");

		JPanel p = new JPanel(new GridBagLayout());
		login.add(p); // Add a panel inside of Frame.
		login.getContentPane().add(p, BorderLayout.CENTER); // Add panel into

		p.setFont(new Font("Consolas", Font.PLAIN, 12));

		GridBagConstraints c = new GridBagConstraints(); // Create GridLayout in
		
		// components..
		JLabel userId, pass;
		userId = new JLabel("USER ID - it's your email.", SwingConstants.CENTER);
		userId.setFont(null);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(40, 30, 40, 30);
		p.add(userId, c);
		pass = new JLabel("PASSWORD", SwingConstants.CENTER);
		pass.setFont(null);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 1;
		p.add(pass, c);

		idField = new JTextField(10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.7;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		p.add(idField, c);

		psField = new JPasswordField(10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.7;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 1;
		p.add(psField, c);

		JLabel comment;
		comment = new JLabel(
				"<html><font face='Consolas'><font size=3>Sample login<br><br>admin@gmail.com / 1234 <br>student@gmail.com / 1234 </font></html>",
				SwingConstants.CENTER);
		comment.setFont(null);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 2;
		p.add(comment, c);

		button = new JButton("Login");
		button.setFont(null);
		c.weightx = 0.7;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 2;
		p.add(button, c);
		button.addActionListener(this);

		login.setSize(760, 500);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		// checks if the button clicked
		if (ae.getSource() == button) {
			char[] temp_pwd = psField.getPassword();
			String pwd = null;
			pwd = String.copyValueOf(temp_pwd);
			// System.out.println("Username,Pwd:" + idField.getText() + "," + pwd);

			// The entered username and password are sent via "checkLogin()" which
			// return boolean, then "checkType()"
			if (db.checkLogin(idField.getText(), pwd)) {
				if (db.checkType(idField.getText()).equals("A")) {
					JOptionPane.showMessageDialog(null, "## Welcome, You will be brought to Admin Dashboard ##", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					login.dispose();
					new AdminDashboard();

				} else if (db.checkType(idField.getText()).equals("S")) {
					JOptionPane.showMessageDialog(null, "## Hello, You will be brought to Student Dashboard ##", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					login.dispose();
					new StudentDashboard(idField.getText());
				}

			} else {
				// a pop-up box
				JOptionPane.showMessageDialog(null, "Login failed!", "Failed!!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void main(String[] arsg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Login();
			}
		});
	}

}
