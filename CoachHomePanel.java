package coaches;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class CoachHomePanel extends JPanel {
    private JLabel lblCoachName, lblTeam, lblNextMatch, lblRecentResult;
    private int coachId;
    private Connection conn;

    public CoachHomePanel(int coachId) {
        this.coachId = coachId;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadCoachInfo();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        lblCoachName = new JLabel("Welcome, Coach", SwingConstants.CENTER);
        lblCoachName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblCoachName, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBorder(new TitledBorder("Your Overview"));

        lblTeam = new JLabel("Your Team: Loading...");
        lblNextMatch = new JLabel("Next Match: Loading...");
        lblRecentResult = new JLabel("Recent Result: Loading...");

        infoPanel.add(lblTeam);
        infoPanel.add(lblNextMatch);
        infoPanel.add(lblRecentResult);
        add(infoPanel, BorderLayout.CENTER);
    }

    private void loadCoachInfo() {
        try {
            // ✅ Get coach name from users table
            PreparedStatement ps = conn.prepareStatement("SELECT full_name FROM users WHERE user_id=? AND role='coach'");
            ps.setInt(1, coachId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) lblCoachName.setText("Welcome,  Coach " + rs.getString("full_name"));

            // ✅ Get coach’s team
            ps = conn.prepareStatement("SELECT team_name FROM teams WHERE coach_id=?");
            ps.setInt(1, coachId);
            rs = ps.executeQuery();
            if (rs.next()) lblTeam.setText("Your team: " + rs.getString("team_name"));
            else lblTeam.setText("Your Team: Not assigned");

            // ✅ Get next scheduled match
            ps = conn.prepareStatement("""
                SELECT m.match_date, t1.team_name AS home, t2.team_name AS away
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                WHERE (m.team_home_id IN (SELECT team_id FROM teams WHERE coach_id=?)
                OR m.team_away_id IN (SELECT team_id FROM teams WHERE coach_id=?))
                AND m.status='scheduled'
                ORDER BY m.match_date ASC LIMIT 1
            """);
            ps.setInt(1, coachId);
            ps.setInt(2, coachId);
            rs = ps.executeQuery();
            if (rs.next()) {
                lblNextMatch.setText("Next Match: " + rs.getString("home") + " vs " +
                        rs.getString("away") + " on " + rs.getString("match_date"));
            } else {
                lblNextMatch.setText("Next Match: None scheduled");
            }

            // ✅ Get most recent completed match
            ps = conn.prepareStatement("""
                SELECT t1.team_name AS home, t2.team_name AS away, m.home_score, m.away_score
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                WHERE (m.team_home_id IN (SELECT team_id FROM teams WHERE coach_id=?)
                OR m.team_away_id IN (SELECT team_id FROM teams WHERE coach_id=?))
                AND m.status='completed'
                ORDER BY m.match_date DESC LIMIT 1
            """);
            ps.setInt(1, coachId);
            ps.setInt(2, coachId);
            rs = ps.executeQuery();
            if (rs.next()) {
                lblRecentResult.setText("Recent Result: " + rs.getString("home") + " " +
                        rs.getInt("home_score") + " - " + rs.getInt("away_score") + " " +
                        rs.getString("away"));
            } else {
                lblRecentResult.setText("Recent Result: None available");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Data Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
