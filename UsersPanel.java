package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.DB;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UsersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUsername, txtFullName, txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Management"));
        
        txtUsername = new JTextField();
        txtFullName = new JTextField();
        txtPassword = new JTextField();
        cmbRole = new JComboBox<>(new String[]{"admin", "coach", "fan"});
        
        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Role", "Created At"}, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        loadUsers();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i != -1) {
                    selectedId = Integer.parseInt(model.getValueAt(i, 0).toString());
                    txtUsername.setText(model.getValueAt(i, 1).toString());
                    txtFullName.setText(model.getValueAt(i, 2).toString());
                    cmbRole.setSelectedItem(model.getValueAt(i, 3).toString());
                }
            }
        });


        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnRefresh.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        model.setRowCount(0);
        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void addUser() {
        String username = txtUsername.getText();
        String fullname = txtFullName.getText();
        String password = txtPassword.getText();
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || fullname.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        String sql = "INSERT INTO users (username, full_name, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, fullname);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
            clearFields();
            loadUsers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
        }
    }

    private void updateUser() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to update!");
            return;
        }

        String sql = "UPDATE users SET username=?, full_name=?, role=?, password=? WHERE user_id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtUsername.getText());
            ps.setString(2, txtFullName.getText());
            ps.setString(3, cmbRole.getSelectedItem().toString());
            ps.setString(4, txtPassword.getText());
            ps.setInt(5, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            clearFields();
            loadUsers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM users WHERE user_id=?";
            try (Connection conn = DB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                clearFields();
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
            }
        }
    }
    private void clearFields() {
        txtUsername.setText("");
        txtFullName.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        selectedId = -1;
    }
}
