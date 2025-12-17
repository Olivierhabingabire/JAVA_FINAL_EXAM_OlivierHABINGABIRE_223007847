package coaches;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CoachTeamPanel extends JPanel {
    private JTextField txtTeamName, txtFoundedYear, txtHomeGround;
    private JLabel lblSport;
    private JButton btnUpdate;
    private Connection conn;
    private int coachId, teamId = -1;

    public CoachTeamPanel(int coachId) {
        this.coachId = coachId;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 40, 20, 40));
        connect();
        initUI();
        loadTeamData();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("My Team Information", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new TitledBorder("Team Details"));

        form.add(new JLabel("Team Name:"));
        txtTeamName = new JTextField();
        txtTeamName.setEditable(false);
        form.add(txtTeamName);

        form.add(new JLabel("Sport:"));
        lblSport = new JLabel("-");
        form.add(lblSport);

        form.add(new JLabel("Founded Year:"));
        txtFoundedYear = new JTextField();
        form.add(txtFoundedYear);

        form.add(new JLabel("Home Ground:"));
        txtHomeGround = new JTextField();
        form.add(txtHomeGround);

        add(form, BorderLayout.CENTER);

        btnUpdate = new JButton("Update Team Info");
        add(btnUpdate, BorderLayout.SOUTH);

        btnUpdate.addActionListener(e -> updateTeam());
    }

    private void loadTeamData() {
        try {
            String sql = """
                SELECT t.team_id, t.team_name, t.founded_year, t.home_ground, s.sport_name
                FROM teams t
                JOIN sports s ON t.sport_id = s.sport_id
                WHERE t.coach_id = ?
            """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, coachId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                teamId = rs.getInt("team_id");
                txtTeamName.setText(rs.getString("team_name"));
                txtFoundedYear.setText(String.valueOf(rs.getInt("founded_year")));
                txtHomeGround.setText(rs.getString("home_ground"));
                lblSport.setText(rs.getString("sport_name"));
            } else {
                JOptionPane.showMessageDialog(this, "No team found for this coach.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Team Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeam() {
        if (teamId == -1) {
            JOptionPane.showMessageDialog(this, "No team assigned to you yet.");
            return;
        }

        try {
            String sql = "UPDATE teams SET founded_year=?, home_ground=? WHERE team_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(txtFoundedYear.getText()));
            ps.setString(2, txtHomeGround.getText());
            ps.setInt(3, teamId);
            int rows = ps.executeUpdate();

            if (rows > 0)
                JOptionPane.showMessageDialog(this, "Team info updated successfully!");
            else
                JOptionPane.showMessageDialog(this, "No changes made.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Updating Team", JOptionPane.ERROR_MESSAGE);
        }
    }
}
