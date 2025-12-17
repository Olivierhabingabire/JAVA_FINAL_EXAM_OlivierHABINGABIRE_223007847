package fans;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FanTeamsPanel extends JPanel {
    private JTable tableTeams;
    private DefaultTableModel modelTeams;
    private Connection conn;

    public FanTeamsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadTeams();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("🏁 Teams Directory", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        modelTeams = new DefaultTableModel(
            new String[]{"Team ID", "Team Name", "Coach", "Founded", "Location"}, 0
        );

        tableTeams = new JTable(modelTeams);
        tableTeams.setRowHeight(25);
        tableTeams.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tableTeams);
        scrollPane.setBorder(new TitledBorder("All Registered Teams"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTeams() {
        modelTeams.setRowCount(0);
        try {
            String query = """
                SELECT t.team_id, t.team_name, c.username AS coach_name, 
     t.founded_year, t.home_ground
   FROM teams t
                LEFT JOIN users c ON t.coach_id = c.user_id
                ORDER BY t.team_name ASC
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelTeams.addRow(new Object[]{
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    rs.getString("coach_name"),
                    rs.getString("founded_year"),
                    rs.getString("home_ground")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Teams", JOptionPane.ERROR_MESSAGE);
        }
    }
}
