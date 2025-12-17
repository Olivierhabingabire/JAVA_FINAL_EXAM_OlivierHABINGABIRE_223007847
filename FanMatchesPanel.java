package fans;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FanMatchesPanel extends JPanel {
    private JTable tableMatches;
    private DefaultTableModel modelMatches;
    private Connection conn;

    public FanMatchesPanel() {
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
        JLabel lblTitle = new JLabel("🏆 All Matches", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        modelMatches = new DefaultTableModel(
            new String[]{"Match ID", "Home Team", "Away Team", "Date", "Status", "Score"}, 0
        );

        tableMatches = new JTable(modelMatches);
        tableMatches.setRowHeight(25);
        tableMatches.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tableMatches);
        scrollPane.setBorder(new TitledBorder("Match Schedule & Results"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMatches() {
        modelMatches.setRowCount(0);
        try {
            String query = """
                SELECT m.match_id, t1.team_name AS home, t2.team_name AS away, 
                       m.match_date, m.status, 
                       CONCAT(m.home_score, ' - ', m.away_score) AS score
                FROM matches m
                JOIN teams t1 ON m.team_home_id = t1.team_id
                JOIN teams t2 ON m.team_away_id = t2.team_id
                ORDER BY m.match_date DESC
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelMatches.addRow(new Object[]{
                    rs.getInt("match_id"),
                    rs.getString("home"),
                    rs.getString("away"),
                    rs.getString("match_date"),
                    rs.getString("status"),
                    rs.getString("score")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Matches", JOptionPane.ERROR_MESSAGE);
        }
    }
}
