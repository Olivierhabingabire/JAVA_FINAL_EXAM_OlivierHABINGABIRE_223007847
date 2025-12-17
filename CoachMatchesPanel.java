package coaches;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class CoachMatchesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;
    private int coachId;

    public CoachMatchesPanel(int coachId) {
        this.coachId = coachId;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadMatches();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Your Team's Matches", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(
            new String[]{"Match ID", "Home Team", "Away Team", "Date", "Location", "Status", "Score"}, 0
        );
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadMatches());
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadMatches() {
        model.setRowCount(0);
        try {
            String query = """
                SELECT m.match_id, 
                       t1.team_name AS home, 
                       t2.team_name AS away, 
                       m.match_date, 
                       m.location, 
                       m.status, 
                       CONCAT(m.home_score, ' - ', m.away_score) AS score
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                WHERE m.team_home_id IN (SELECT team_id FROM teams WHERE coach_id=?)
                   OR m.team_away_id IN (SELECT team_id FROM teams WHERE coach_id=?)
                ORDER BY m.match_date DESC
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, coachId);
            ps.setInt(2, coachId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("match_id"),
                    rs.getString("home"),
                    rs.getString("away"),
                    rs.getString("match_date"),
                    rs.getString("location"),
                    rs.getString("status"),
                    rs.getString("score")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Load Matches Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
