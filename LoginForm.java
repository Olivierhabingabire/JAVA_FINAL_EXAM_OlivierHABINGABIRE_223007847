package forms;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;

import config.DB;

public class LoginForm extends JFrame {
	JLabel ul = new JLabel("Username:"); JTextField username = new JTextField(20);
	JLabel pl = new JLabel("Password:"); JPasswordField password = new JPasswordField(20);
	JButton loginBtn = new JButton("Login"); JButton registerBtn = new JButton("Register"); 

	public LoginForm() {
		setLayout(null);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setSize(350, 250);
		addHeader ();
		addComponents();
		initComponents();
		setVisible(true);
		registerBtn.addActionListener((e) ->{
			dispose();
			new RegisterForm();

		});
		loginBtn.addActionListener((e) -> loginUser());
	}
	private void addHeader() {
		JPanel header = new JPanel();
		header.setBounds(0, 0, 350, 50);             
		header.setBackground(new Color(211, 211, 211)); 
		JLabel title = new JLabel("Welcome to GIANT'S HUB OF SPORTS");
		title.setForeground(Color.BLACK);           
		title.setFont(new Font("Segoe UI", Font.BOLD, 17));
		header.add(title);
		add(header);

	}
	private void addComponents() {
		add(ul);
		add(pl);
		add(username);
		add(password);
		add(loginBtn);
		add(registerBtn);
	}

	private void initComponents() { 
		ul.setBounds(10, 70, 100, 20); username.setBounds(120, 70, 200, 25);
		pl.setBounds(10, 110, 100, 20); password.setBounds(120, 110, 200, 25);
		loginBtn.setBounds(10, 150, 100, 20); registerBtn.setBounds(220, 150, 100, 20);
	}
	private void loginUser() {
		if(username.getText().isEmpty()|| new String(password.getPassword()).isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter your username or Password");
			return;

		}
		String user = username.getText();
		String pass = new String(password.getPassword());

		try(var conn = DB.getConnection()) {
			var checkUserStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
			checkUserStatement.setString(1, user);
			checkUserStatement.setString(2, pass);
			var result = checkUserStatement.executeQuery();
			if (!result.next()){
				JOptionPane.showMessageDialog(null, "Invalid Username Or Password");
				return;

			}
			username.setText("");
			password.setText("");
			dispose();
			new SportPlatformSystem(result.getString(4), result.getInt(1));



		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "Something went wrong","Error", JOptionPane.ERROR_MESSAGE);
			System.out.print(ex.getMessage());
		}



	}

}
