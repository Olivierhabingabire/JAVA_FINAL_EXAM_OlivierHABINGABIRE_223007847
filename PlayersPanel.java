package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.DB;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PlayersPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtPosition, txtAge, txtNationality, txtJersey;
    private JComboBox<String> cmbTeam, cmbSport;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    public PlayersPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadSports();
        loadTeams();
        loadPlayers();
    }

  
    // ================= UI DESIGN =================
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSport = new JLabel("Sport:");
        JLabel lblTeam = new JLabel("Team:");
        JLabel lblName = new JLabel("Player Name:");
        JLabel lblPosition = new JLabel("Position:");
        JLabel lblAge = new JLabel("Age:");
        JLabel lblNationality = new JLabel("Nationality:");
        JLabel lblJersey = new JLabel("Jersey #:");

        cmbSport = new JComboBox<>();
        cmbTeam = new JComboBox<>();
        txtName = new JTextField(15);
        txtPosition = new JTextField(15);
        txtAge = new JTextField(15);
        txtNationality = new JTextField(15);
        txtJersey = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblSport, gbc);
        gbc.gridx = 1; formPanel.add(cmbSport, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblTeam, gbc);
        gbc.gridx = 1; formPanel.add(cmbTeam, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblName, gbc);
        gbc.gridx = 1; formPanel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblPosition, gbc);
        gbc.gridx = 1; formPanel.add(txtPosition, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(lblAge, gbc);
        gbc.gridx = 1; formPanel.add(txtAge, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(lblNationality, gbc);
        gbc.gridx = 1; formPanel.add(txtNationality, gbc);
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(lblJersey, gbc);
        gbc.gridx = 1; formPanel.add(txtJersey, gbc);

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
        model = new DefaultTableModel(
            new String[]{"ID", "Sport", "Team", "Name", "Position", "Age", "Nationality", "Jersey #"}, 0
        );
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        // ---------- ADD TO MAIN PANEL ----------
        add(formPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        btnAdd.addActionListener(e -> addPlayer());
        btnUpdate.addActionListener(e -> updatePlayer());
        btnDelete.addActionListener(e -> deletePlayer());
        btnClear.addActionListener(e -> clearFields());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                cmbSport.setSelectedItem(model.getValueAt(row, 1).toString());
                cmbTeam.setSelectedItem(model.getValueAt(row, 2).toString());
                txtName.setText(model.getValueAt(row, 3).toString());
                txtPosition.setText(model.getValueAt(row, 4).toString());
                txtAge.setText(model.getValueAt(row, 5).toString());
                txtNationality.setText(model.getValueAt(row, 6).toString());
                txtJersey.setText(model.getValueAt(row, 7).toString());
            }
        });
    }

    // ================= LOAD COMBOBOXES =================
    private void loadSports() {
        try (var conn = DB.getConnection()) {
            cmbSport.removeAllItems();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT sport_name FROM sports");
            while (rs.next()) cmbSport.addItem(rs.getString("sport_name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sports: " + e.getMessage());
        }
    }

    private void loadTeams() {
        try (var conn = DB.getConnection()){
            cmbTeam.removeAllItems();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT team_name FROM teams");
            while (rs.next()) cmbTeam.addItem(rs.getString("team_name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + e.getMessage());
        }
    }

    // ================= CRUD =================
    private void loadPlayers() {
        try(var conn = DB.getConnection()) {
            model.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT p.player_id, s.sport_name, t.team_name, p.player_name, p.position, p.age, p.nationality, p.jersey_number " +
                "FROM players p " +
                "LEFT JOIN sports s ON p.sport_id = s.sport_id " +
                "LEFT JOIN teams t ON p.team_id = t.team_id"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getInt(6),
                    rs.getString(7), rs.getInt(8)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading players: " + e.getMessage());
        }
    }

    private void addPlayer() {
        String sportName = (String) cmbSport.getSelectedItem();
        String teamName = (String) cmbTeam.getSelectedItem();
        String name = txtName.getText();
        String pos = txtPosition.getText();
        String nat = txtNationality.getText();
        String ageText = txtAge.getText();
        String jerseyText = txtJersey.getText();

        if (name.isEmpty() || sportName == null || teamName == null) {
            JOptionPane.showMessageDialog(this, "Fill in all required fields!");
            return;
        }

        try (var conn = DB.getConnection()) {
            int age = ageText.isEmpty() ? 0 : Integer.parseInt(ageText);
            int jersey = jerseyText.isEmpty() ? 0 : Integer.parseInt(jerseyText);

            // get sport_id and team_id
            int sport_id = getId("sports", "sport_name", sportName, "sport_id");
            int team_id = getId("teams", "team_name", teamName, "team_id");

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO players (sport_id, team_id, player_name, position, age, nationality, jersey_number) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, sport_id);
            ps.setInt(2, team_id);
            ps.setString(3, name);
            ps.setString(4, pos);
            ps.setInt(5, age);
            ps.setString(6, nat);
            ps.setInt(7, jersey);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player added successfully!");
            loadPlayers();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding player: " + e.getMessage());
        }
    }

    private void updatePlayer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a player to update!");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sportName = (String) cmbSport.getSelectedItem();
        String teamName = (String) cmbTeam.getSelectedItem();
        String name = txtName.getText();
        String pos = txtPosition.getText();
        String nat = txtNationality.getText();
        int age = txtAge.getText().isEmpty() ? 0 : Integer.parseInt(txtAge.getText());
        int jersey = txtJersey.getText().isEmpty() ? 0 : Integer.parseInt(txtJersey.getText());

        try (var conn = DB.getConnection()) {
            int sport_id = getId("sports", "sport_name", sportName, "sport_id");
            int team_id = getId("teams", "team_name", teamName, "team_id");

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE players SET sport_id=?, team_id=?, player_name=?, position=?, age=?, nationality=?, jersey_number=? WHERE player_id=?"
            );
            ps.setInt(1, sport_id);
            ps.setInt(2, team_id);
            ps.setString(3, name);
            ps.setString(4, pos);
            ps.setInt(5, age);
            ps.setString(6, nat);
            ps.setInt(7, jersey);
            ps.setInt(8, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player updated successfully!");
            loadPlayers();
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating player: " + e.getMessage());
        }
    }

    private void deletePlayer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a player to delete!");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (var conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM players WHERE player_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Player deleted successfully!");
            loadPlayers();
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting player: " + e.getMessage());
        }
    }

    // ================= HELPERS =================
    private int getId(String table, String column, String value, String idColumn) throws SQLException {
    	var conn = DB.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT " + idColumn + " FROM " + table + " WHERE " + column + "=?");
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private void clearFields() {
        txtName.setText("");
        txtPosition.setText("");
        txtAge.setText("");
        txtNationality.setText("");
        txtJersey.setText("");
        cmbSport.setSelectedIndex(-1);
        cmbTeam.setSelectedIndex(-1);
        table.clearSelection();
    }
}
