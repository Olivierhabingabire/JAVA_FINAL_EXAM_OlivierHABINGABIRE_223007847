package admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RefereesPanel extends JPanel {
    private JTable table;
    private JTextField txtName, txtExperience;
    private DefaultTableModel model;
    private Connection conn;

    public RefereesPanel() {
        setLayout(new BorderLayout());
        connect();
        initUI();
        loadReferees();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sport_system", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        txtName = new JTextField();
        txtExperience = new JTextField();

        form.add(new JLabel("Referee Name:"));
        form.add(txtName);
        form.add(new JLabel("Years of Experience:"));
        form.add(txtExperience);

        JPanel buttons = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        model = new DefaultTableModel(new String[]{"ID", "Referee Name", "Experience (Years)"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addReferee());
        btnUpdate.addActionListener(e -> updateReferee());
        btnDelete.addActionListener(e -> deleteReferee());
        btnClear.addActionListener(e -> clearFields());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(model.getValueAt(row, 1).toString());
                txtExperience.setText(model.getValueAt(row, 2).toString());
            }
        });
    }

    private void loadReferees() {
        model.setRowCount(0);
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM referees")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("referee_id"), rs.getString("referee_name"), rs.getInt("year_of_experience")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addReferee() {
        String name = txtName.getText();
        String exp = txtExperience.getText();

        if (name.isEmpty() || exp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO referees (referee_name, year_of_experience) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(exp));
            ps.executeUpdate();
            loadReferees();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Insert Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReferee() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        try (PreparedStatement ps = conn.prepareStatement("UPDATE referees SET referee_name=?, year_of_experience=? WHERE referee_id=?")) {
            ps.setString(1, txtName.getText());
            ps.setInt(2, Integer.parseInt(txtExperience.getText()));
            ps.setInt(3, (int) model.getValueAt(row, 0));
            ps.executeUpdate();
            loadReferees();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteReferee() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM referees WHERE referee_id=?")) {
            ps.setInt(1, (int) model.getValueAt(row, 0));
            ps.executeUpdate();
            loadReferees();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtExperience.setText("");
        table.clearSelection();
    }
}
