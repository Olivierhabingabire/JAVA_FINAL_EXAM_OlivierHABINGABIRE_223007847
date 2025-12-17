package fans;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FanPlayersPanel extends JPanel {
    private JTable tablePlayers;
    private DefaultTableModel modelPlayers;
    private Connection conn;

    public FanPlayersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadPlayers();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("⚽ All Players", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        modelPlayers = new DefaultTableModel(
            new String[]{"Player ID", "Player Name", "Team", "Position", "Age"}, 0
        );

        tablePlayers = new JTable(modelPlayers);
        tablePlayers.setRowHeight(25);
        tablePlayers.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tablePlayers);
        scrollPane.setBorder(new TitledBorder("Players Information"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPlayers() {
        modelPlayers.setRowCount(0);
        try {
            String query = """
                SELECT p.player_id, p.player_name, t.team_name, p.position, p.age
                FROM players p
                JOIN teams t ON p.team_id = t.team_id
                ORDER BY t.team_name, p.player_name
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelPlayers.addRow(new Object[]{
                    rs.getInt("player_id"),
                    rs.getString("player_name"),
                    rs.getString("team_name"),
                    rs.getString("position"),
                    rs.getInt("age")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Players", JOptionPane.ERROR_MESSAGE);
        }
    }
}
