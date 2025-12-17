package admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MatchesPanel extends JPanel {
    private JTable table;
    private JComboBox<String> cmbSport, cmbHome, cmbAway, cmbStatus;
    private JTextField txtDate, txtLocation, txtHomeScore, txtAwayScore;
    private DefaultTableModel model;
    private Connection conn;

    public MatchesPanel() {
        setLayout(new BorderLayout());
        connect();
        initUI();
        loadCombo(cmbSport, "SELECT sport_name FROM sports");
        loadCombo(cmbHome, "SELECT team_name FROM teams");
        loadCombo(cmbAway, "SELECT team_name FROM teams");
        loadMatches(); 
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(4, 4, 5, 5));
        cmbSport = new JComboBox<>();
        cmbHome = new JComboBox<>();
        cmbAway = new JComboBox<>();
        cmbStatus = new JComboBox<>(new String[]{"scheduled", "ongoing", "completed"});
        txtDate = new JTextField();
        txtLocation = new JTextField();
        txtHomeScore = new JTextField();
        txtAwayScore = new JTextField();

        form.add(new JLabel("Sport")); form.add(cmbSport);
        form.add(new JLabel("Home Team")); form.add(cmbHome);
        form.add(new JLabel("Away Team")); form.add(cmbAway);
        form.add(new JLabel("Date (YYYY-MM-DD HH:MM:SS)")); form.add(txtDate);
        form.add(new JLabel("Location")); form.add(txtLocation);
        form.add(new JLabel("Home Score")); form.add(txtHomeScore);
        form.add(new JLabel("Away Score")); form.add(txtAwayScore);
        form.add(new JLabel("Status")); form.add(cmbStatus);

        JPanel btns = new JPanel();
        JButton add = new JButton("Add"), upd = new JButton("Update"), del = new JButton("Delete"), clr = new JButton("Clear");
        btns.add(add); btns.add(upd); btns.add(del); btns.add(clr);

        model = new DefaultTableModel(new String[]{"ID", "Sport", "Home", "Away", "Date", "Location", "Status", "Home Score", "Away Score"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);
        add(btns, BorderLayout.SOUTH);

        add.addActionListener(e -> insertMatch());
        upd.addActionListener(e -> updateMatch());
        del.addActionListener(e -> deleteMatch());
        clr.addActionListener(e -> clearFields());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                cmbSport.setSelectedItem(model.getValueAt(r, 1));
                cmbHome.setSelectedItem(model.getValueAt(r, 2));
                cmbAway.setSelectedItem(model.getValueAt(r, 3));
                txtDate.setText(model.getValueAt(r, 4).toString());
                txtLocation.setText(model.getValueAt(r, 5).toString());
                cmbStatus.setSelectedItem(model.getValueAt(r, 6));
                txtHomeScore.setText(model.getValueAt(r, 7).toString());
                txtAwayScore.setText(model.getValueAt(r, 8).toString());
            }
        });
    }

    private void loadCombo(JComboBox<String> cmb, String sql) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) cmb.addItem(rs.getString(1));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private void loadMatches() {
        model.setRowCount(0);
        String sql = "SELECT m.match_id, s.sport_name, th.team_name, ta.team_name, m.match_date, " +
                     "m.location, m.status, m.home_score, m.away_score " +
                     "FROM matches m " +
                     "LEFT JOIN sports s ON m.sport_id = s.sport_id " +
                     "LEFT JOIN teams th ON m.team_home_id = th.team_id " +
                     "LEFT JOIN teams ta ON m.team_away_id = ta.team_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getInt(8), rs.getInt(9)
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private void insertMatch() {
        String sql = "INSERT INTO matches (sport_id, team_home_id, team_away_id, match_date, location, status, home_score, away_score) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getId("sports", "sport_name", (String) cmbSport.getSelectedItem()));
            ps.setInt(2, getId("teams", "team_name", (String) cmbHome.getSelectedItem()));
            ps.setInt(3, getId("teams", "team_name", (String) cmbAway.getSelectedItem()));
            ps.setString(4, txtDate.getText());
            ps.setString(5, txtLocation.getText());
            ps.setString(6, (String) cmbStatus.getSelectedItem());
            ps.setInt(7, Integer.parseInt(txtHomeScore.getText()));
            ps.setInt(8, Integer.parseInt(txtAwayScore.getText()));
            ps.executeUpdate();
            loadMatches();
            clearFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private void updateMatch() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String sql = "UPDATE matches SET sport_id=?, team_home_id=?, team_away_id=?, match_date=?, location=?, status=?, home_score=?, away_score=? WHERE match_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getId("sports", "sport_name", (String) cmbSport.getSelectedItem()));
            ps.setInt(2, getId("teams", "team_name", (String) cmbHome.getSelectedItem()));
            ps.setInt(3, getId("teams", "team_name", (String) cmbAway.getSelectedItem()));
            ps.setString(4, txtDate.getText());
            ps.setString(5, txtLocation.getText());
            ps.setString(6, (String) cmbStatus.getSelectedItem());
            ps.setInt(7, Integer.parseInt(txtHomeScore.getText()));
            ps.setInt(8, Integer.parseInt(txtAwayScore.getText()));
            ps.setInt(9, (int) model.getValueAt(row, 0));
            ps.executeUpdate();
            loadMatches();
            clearFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private void deleteMatch() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM matches WHERE match_id=?")) {
            ps.setInt(1, (int) model.getValueAt(row, 0));
            ps.executeUpdate();
            loadMatches();
            clearFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e); }
    }

    private int getId(String table, String col, String val) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT " + table.substring(0, table.length() - 1) + "_id FROM " + table + " WHERE " + col + "=?")) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void clearFields() {
        txtDate.setText(""); txtLocation.setText(""); txtHomeScore.setText(""); txtAwayScore.setText("");
        cmbSport.setSelectedIndex(-1); cmbHome.setSelectedIndex(-1); cmbAway.setSelectedIndex(-1); cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }
}
