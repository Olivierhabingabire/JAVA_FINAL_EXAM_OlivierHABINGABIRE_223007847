package coaches;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CoachPlayersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtPosition, txtAge, txtNationality, txtJersey;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private Connection conn;
    private int coachId, teamId = -1;

    public CoachPlayersPanel(int coachId) {
        this.coachId = coachId;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 20, 10, 20));
        connect();
        findCoachTeam();
        initUI();
        if (teamId != -1) loadPlayers();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findCoachTeam() {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT team_id FROM teams WHERE coach_id=?");
            ps.setInt(1, coachId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) teamId = rs.getInt("team_id");
            else JOptionPane.showMessageDialog(this, "No team assigned to this coach.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error finding team", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Manage Players", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Position", "Age", "Nationality", "Jersey"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form panel
        JPanel form = new JPanel(new GridLayout(2, 5, 5, 5));
        txtName = new JTextField();
        txtPosition = new JTextField();
        txtAge = new JTextField();
        txtNationality = new JTextField();
        txtJersey = new JTextField();

        form.add(new JLabel("Name:"));
        form.add(new JLabel("Position:"));
        form.add(new JLabel("Age:"));
        form.add(new JLabel("Nationality:"));
        form.add(new JLabel("Jersey #:"));

        JPanel inputPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        inputPanel.add(txtName);
        inputPanel.add(txtPosition);
        inputPanel.add(txtAge);
        inputPanel.add(txtNationality);
        inputPanel.add(txtJersey);

        JPanel combinedForm = new JPanel(new BorderLayout());
        combinedForm.add(form, BorderLayout.NORTH);
        combinedForm.add(inputPanel, BorderLayout.CENTER);

        add(combinedForm, BorderLayout.SOUTH);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.PAGE_END);

        // Actions
        btnAdd.addActionListener(e -> addPlayer());
        btnUpdate.addActionListener(e -> updatePlayer());
        btnDelete.addActionListener(e -> deletePlayer());
        btnRefresh.addActionListener(e -> loadPlayers());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i >= 0) {
                    txtName.setText(model.getValueAt(i, 1).toString());
                    txtPosition.setText(model.getValueAt(i, 2).toString());
                    txtAge.setText(model.getValueAt(i, 3).toString());
                    txtNationality.setText(model.getValueAt(i, 4).toString());
                    txtJersey.setText(model.getValueAt(i, 5).toString());
                }
            }
        });
    }

    private void loadPlayers() {
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE team_id=?");
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getString("position"),
                        rs.getInt("age"),
                        rs.getString("nationality"),
                        rs.getInt("jersey_number")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Players", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPlayer() {
        if (teamId == -1) {
            JOptionPane.showMessageDialog(this, "You are not assigned to any team.");
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO players (team_id, player_name, position, age, nationality, jersey_number) VALUES (?,?,?,?,?,?)");
            ps.setInt(1, teamId);
            ps.setString(2, txtName.getText());
            ps.setString(3, txtPosition.getText());
            ps.setInt(4, Integer.parseInt(txtAge.getText()));
            ps.setString(5, txtNationality.getText());
            ps.setInt(6, Integer.parseInt(txtJersey.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Player added successfully!");
            loadPlayers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Adding Player", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePlayer() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a player to update.");
            return;
        }

        int playerId = (int) model.getValueAt(i, 0);
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE players SET player_name=?, position=?, age=?, nationality=?, jersey_number=? WHERE player_id=?");
            ps.setString(1, txtName.getText());
            ps.setString(2, txtPosition.getText());
            ps.setInt(3, Integer.parseInt(txtAge.getText()));
            ps.setString(4, txtNationality.getText());
            ps.setInt(5, Integer.parseInt(txtJersey.getText()));
            ps.setInt(6, playerId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Player updated successfully!");
            loadPlayers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Updating Player", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePlayer() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a player to delete.");
            return;
        }

        int playerId = (int) model.getValueAt(i, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this player?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM players WHERE player_id=?");
            ps.setInt(1, playerId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Player deleted successfully!");
            loadPlayers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Deleting Player", JOptionPane.ERROR_MESSAGE);
        }
    }
}
