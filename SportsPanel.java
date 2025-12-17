package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.DB;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SportsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName;
    private JTextArea txtDescription, txtRules;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public SportsPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadSports();
    }

    // ====================== UI DESIGN ======================
    private void initComponents() {
        // ---------- TOP FORM ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Sport Name:");
        JLabel lblDesc = new JLabel("Description:");
        JLabel lblRules = new JLabel("Rules:");

        txtName = new JTextField(20);
        txtDescription = new JTextArea(3, 20);
        txtRules = new JTextArea(3, 20);

        JScrollPane descScroll = new JScrollPane(txtDescription);
        JScrollPane rulesScroll = new JScrollPane(txtRules);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblName, gbc);
        gbc.gridx = 1; formPanel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblDesc, gbc);
        gbc.gridx = 1; formPanel.add(descScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblRules, gbc);
        gbc.gridx = 1; formPanel.add(rulesScroll, gbc);

        // ---------- BUTTONS ----------
        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"ID", "Sport Name", "Description", "Rules"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // ---------- ADD TO MAIN LAYOUT ----------
        add(formPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        btnAdd.addActionListener(e -> addSport());
        btnUpdate.addActionListener(e -> updateSport());
        btnDelete.addActionListener(e -> deleteSport());
        btnClear.addActionListener(e -> clearFields());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(model.getValueAt(row, 1).toString());
                txtDescription.setText(model.getValueAt(row, 2).toString());
                txtRules.setText(model.getValueAt(row, 3).toString());
            }
        });
    }

    // ====================== CRUD FUNCTIONS ======================
    private void loadSports() {
        try (var conn = DB.getConnection()) {
            model.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM sports");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("sport_id"),
                    rs.getString("sport_name"),
                    rs.getString("description"),
                    rs.getString("rules")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sports: " + e.getMessage());
        }
    }

    private void addSport() {
        String name = txtName.getText();
        String desc = txtDescription.getText();
        String rules = txtRules.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sport name cannot be empty.");
            return;
        }

        try (var conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sports (sport_name, description, rules) VALUES (?, ?, ?)"
            );
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setString(3, rules);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sport added successfully!");
            loadSports();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding sport: " + e.getMessage());
        }
    }

    private void updateSport() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sport to update.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = txtName.getText();
        String desc = txtDescription.getText();
        String rules = txtRules.getText();

        try (var conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE sports SET sport_name=?, description=?, rules=? WHERE sport_id=?"
            );
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setString(3, rules);
            ps.setInt(4, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sport updated successfully!");
            loadSports();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating sport: " + e.getMessage());
        }
    }

    private void deleteSport() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sport to delete.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this sport?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (var conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM sports WHERE sport_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sport deleted successfully!");
            loadSports();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting sport: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtDescription.setText("");
        txtRules.setText("");
        table.clearSelection();
    }
}
