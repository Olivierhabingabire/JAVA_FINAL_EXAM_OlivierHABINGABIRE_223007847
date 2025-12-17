package fans;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class FanHomePanel extends JPanel {
    private JTable tableUpcoming, tableRecent;
    private DefaultTableModel modelUpcoming, modelRecent;
    private Connection conn;

    public FanHomePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadUpcomingMatches();
        loadRecentResults();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Welcome, Fan! ⚽", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Upcoming Matches Table
        modelUpcoming = new DefaultTableModel(
            new String[]{"Match", "Date", "Location", "Status"}, 0
        );
        tableUpcoming = new JTable(modelUpcoming);
        tableUpcoming.setRowHeight(25);
        JScrollPane upcomingScroll = new JScrollPane(tableUpcoming);
        upcomingScroll.setBorder(new TitledBorder("📅 Upcoming Matches"));
        centerPanel.add(upcomingScroll);

        // Recent Results Table
        modelRecent = new DefaultTableModel(
            new String[]{"Match", "Score", "Date", "Status"}, 0
        );
        tableRecent = new JTable(modelRecent);
        tableRecent.setRowHeight(25);
        JScrollPane recentScroll = new JScrollPane(tableRecent);
        recentScroll.setBorder(new TitledBorder("🏆 Recent Results"));
        centerPanel.add(recentScroll);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadUpcomingMatches() {
        modelUpcoming.setRowCount(0);
        try {
            String query = """
                SELECT CONCAT(t1.team_name, ' vs ', t2.team_name) AS match_name,
                       m.match_date, m.location, m.status
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                WHERE m.status = 'scheduled'
                ORDER BY m.match_date ASC LIMIT 10
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelUpcoming.addRow(new Object[]{
                    rs.getString("match_name"),
                    rs.getString("match_date"),
                    rs.getString("location"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Upcoming Matches", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentResults() {
        modelRecent.setRowCount(0);
        try {
            String query = """
                SELECT CONCAT(t1.team_name, ' vs ', t2.team_name) AS match_name,
                       CONCAT(m.home_score, ' - ', m.away_score) AS score,
                       m.match_date, m.status
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                WHERE m.status = 'completed'
                ORDER BY m.match_date DESC LIMIT 10
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelRecent.addRow(new Object[]{
                    rs.getString("match_name"),
                    rs.getString("score"),
                    rs.getString("match_date"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Recent Results", JOptionPane.ERROR_MESSAGE);
        }
    }
}
