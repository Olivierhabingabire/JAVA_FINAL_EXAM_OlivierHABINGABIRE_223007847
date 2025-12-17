package fans;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FanSportsPanel extends JPanel {
    private JTable tableSports;
    private DefaultTableModel modelSports;
    private Connection conn;

    public FanSportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        connect();
        initUI();
        loadSports();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("🏅 Available Sports", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        modelSports = new DefaultTableModel(
            new String[]{"Sport ID", "Sport Name", "Description", "Rules"}, 0
        );

        tableSports = new JTable(modelSports);
        tableSports.setRowHeight(25);
        tableSports.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tableSports);
        scrollPane.setBorder(new TitledBorder("Sports Information"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSports() {
        modelSports.setRowCount(0);
        try {
            String query = "SELECT sport_id, sport_name, description, rules FROM sports ORDER BY sport_name";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelSports.addRow(new Object[]{
                    rs.getInt("sport_id"),
                    rs.getString("sport_name"),
                    rs.getString("description"),
                    rs.getString("rules")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error Loading Sports", JOptionPane.ERROR_MESSAGE);
        }
    }
}
