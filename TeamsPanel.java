package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.DB;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TeamsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTeamName, txtFoundedYear, txtHomeGround;
    private JComboBox<String> cmbSport, cmbCoach;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public TeamsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ----- NORTH PANEL (Form) -----
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Teams Management"));

        txtTeamName = new JTextField();
        txtFoundedYear = new JTextField();
        txtHomeGround = new JTextField();
        cmbSport = new JComboBox<>();
        cmbCoach = new JComboBox<>();

        formPanel.add(new JLabel("Team Name:"));
        formPanel.add(txtTeamName);
        formPanel.add(new JLabel("Sport:"));
        formPanel.add(cmbSport);
        formPanel.add(new JLabel("Coach:"));
        formPanel.add(cmbCoach);
        formPanel.add(new JLabel("Founded Year:"));
        formPanel.add(txtFoundedYear);
        formPanel.add(new JLabel("Home Ground:"));
        formPanel.add(txtHomeGround);

        add(formPanel, BorderLayout.NORTH);

        // ----- CENTER PANEL (Table) -----
        model = new DefaultTableModel(new String[]{"ID", "Team Name", "Sport", "Coach", "Founded Year", "Home Ground"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ----- SOUTH PANEL (Buttons) -----
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

        // Load initial data
        loadSports();
        loadCoaches();
        loadTeams();

        // Table click listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i != -1) {
                    selectedId = Integer.parseInt(model.getValueAt(i, 0).toString());
                    txtTeamName.setText(model.getValueAt(i, 1).toString());
                    cmbSport.setSelectedItem(model.getValueAt(i, 2).toString());
                    cmbCoach.setSelectedItem(model.getValueAt(i, 3).toString());
                    txtFoundedYear.setText(model.getValueAt(i, 4).toString());
                    txtHomeGround.setText(model.getValueAt(i, 5).toString());
                }
            }
        });

        // Button actions
        btnAdd.addActionListener(e -> addTeam());
        btnUpdate.addActionListener(e -> updateTeam());
        btnDelete.addActionListener(e -> deleteTeam());
        btnRefresh.addActionListener(e -> loadTeams());
    }

    // ----- Load sports into combo -----
    private void loadSports() {
        cmbSport.removeAllItems();
        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT sport_name FROM sports")) {
            while (rs.next()) {
                cmbSport.addItem(rs.getString("sport_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sports: " + e.getMessage());
        }
    }

    // ----- Load coaches into combo -----
    private void loadCoaches() {
        cmbCoach.removeAllItems();
        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT full_name FROM users WHERE role='coach'")) {
            while (rs.next()) {
                cmbCoach.addItem(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading coaches: " + e.getMessage());
        }
    }

    // ----- Load all teams into JTable -----
    private void loadTeams() {
        model.setRowCount(0);
        String query = """
            SELECT t.team_id, t.team_name, s.sport_name, u.full_name AS coach_name, 
                   t.founded_year, t.home_ground
            FROM teams t
            LEFT JOIN sports s ON t.sport_id = s.sport_id
            LEFT JOIN users u ON t.coach_id = u.user_id
        """;
        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getString("sport_name"),
                        rs.getString("coach_name"),
                        rs.getString("founded_year"),
                        rs.getString("home_ground")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + e.getMessage());
        }
    }

    // ----- Add new team -----
    private void addTeam() {
        String teamName = txtTeamName.getText();
        String sportName = (String) cmbSport.getSelectedItem();
        String coachName = (String) cmbCoach.getSelectedItem();
        String foundedYear = txtFoundedYear.getText();
        String homeGround = txtHomeGround.getText();

        if (teamName.isEmpty() || sportName == null || coachName == null) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            int sportId = getSportId(conn, sportName);
            int coachId = getCoachId(conn, coachName);

            String sql = "INSERT INTO teams (team_name, sport_id, coach_id, founded_year, home_ground) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, teamName);
            ps.setInt(2, sportId);
            ps.setInt(3, coachId);
            ps.setString(4, foundedYear);
            ps.setString(5, homeGround);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Team added successfully!");
            clearFields();
            loadTeams();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding team: " + e.getMessage());
        }
    }

    // ----- Update team -----
    private void updateTeam() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a team to update!");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            int sportId = getSportId(conn, (String) cmbSport.getSelectedItem());
            int coachId = getCoachId(conn, (String) cmbCoach.getSelectedItem());

            String sql = "UPDATE teams SET team_name=?, sport_id=?, coach_id=?, founded_year=?, home_ground=? WHERE team_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtTeamName.getText());
            ps.setInt(2, sportId);
            ps.setInt(3, coachId);
            ps.setString(4, txtFoundedYear.getText());
            ps.setString(5, txtHomeGround.getText());
            ps.setInt(6, selectedId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Team updated successfully!");
            clearFields();
            loadTeams();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating team: " + e.getMessage());
        }
    }

    // ----- Delete team -----
    private void deleteTeam() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a team to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DB.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM teams WHERE team_id=?")) {
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Team deleted successfully!");
                clearFields();
                loadTeams();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting team: " + e.getMessage());
            }
        }
    }

    // ----- Utility Methods -----
    private int getSportId(Connection conn, String sportName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT sport_id FROM sports WHERE sport_name=?");
        ps.setString(1, sportName);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("sport_id") : 0;
    }

    private int getCoachId(Connection conn, String coachName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE full_name=? AND role='coach'");
        ps.setString(1, coachName);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("user_id") : 0;
    }

    private void clearFields() {
        txtTeamName.setText("");
        txtFoundedYear.setText("");
        txtHomeGround.setText("");
        selectedId = -1;
    }
}
