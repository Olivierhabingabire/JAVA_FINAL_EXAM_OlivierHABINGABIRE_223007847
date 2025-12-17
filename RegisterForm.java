package forms;

import java.awt.GridLayout;

import javax.swing.*;

import config.DB;

public class RegisterForm extends JFrame {
	JLabel ul = new JLabel("Username:"); JTextField username = new JTextField(20);
	JLabel fl = new JLabel("Full name:"); JTextField fullname = new JTextField(20);
	JLabel pl = new JLabel("Password:"); JPasswordField password = new JPasswordField(20);

	JButton loginBtn = new JButton("Login"); JButton registerBtn = new JButton("Register"); 
	public RegisterForm() {
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(450,200);
		addComponents();
		registerBtn.addActionListener((e) -> registerUser());
		loginBtn.addActionListener((e) -> {
			dispose();
			new LoginForm();
		});
	}
	private void addComponents() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		panel.add(ul);
		panel.add(username);
		panel.add(fl);
		panel.add(fullname);
		panel.add(pl);
		panel.add(password);
		panel.add(registerBtn);
		panel.add(loginBtn);
		add(panel);
	}

	private void registerUser(){
		if(username.getText().isEmpty()|| fullname.getText().isEmpty()|| new String(password.getPassword()).isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Set your username, Full name and Password");
			return;
		}
		String usern = username.getText();
		String fulln = fullname.getText();
		String passw = new String(password.getPassword());
		try(var conn = DB.getConnection()) {
			var stmt = conn.prepareStatement("SELECT * FROM users WHERE username =?");
			stmt.setString(1, usern);
			var rst = stmt.executeQuery();
			if(rst.next()) {
				JOptionPane.showMessageDialog(null, "Username Already taken");
				return;
			}
			var insertStmt = conn.prepareStatement("INSERT INTO users (username, full_name, password) VALUES(?,?,?)");
			insertStmt.setString(1, usern);
			insertStmt.setString(2, fulln);
			insertStmt.setString(3, passw);
			var rest2 = insertStmt.executeUpdate();
			if (rest2 == 1) {
				JOptionPane.showMessageDialog(null, "Registered Successfully");
				dispose();
				new LoginForm();
				
			} else {
				JOptionPane.showMessageDialog(null, "Try Again");
			}
		
			
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}
}
