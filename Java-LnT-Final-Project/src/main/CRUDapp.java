package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Random;

public class CRUDapp extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTextField txtNama, txtHarga, txtStok;
    private final JButton btnInsert, btnView, btnUpdate, btnDelete;
    private JList<String> listMenu;
    private DefaultListModel<String> listModel;
    private Connection connection;

    public CRUDapp() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/PT_Pudding", "username", "password");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }

        setTitle("PT Pudding Menu Management");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // input
        JPanel panelInput = new JPanel(new GridLayout(3, 2));
        panelInput.add(new JLabel("Nama Menu:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);
        panelInput.add(new JLabel("Harga Menu:"));
        txtHarga = new JTextField();
        panelInput.add(txtHarga);
        panelInput.add(new JLabel("Stok Menu:"));
        txtStok = new JTextField();
        panelInput.add(txtStok);
        add(panelInput, BorderLayout.NORTH);

        // buttons
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnInsert = new JButton("Insert Menu");
        btnInsert.addActionListener(this::insertMenu);
        panelButtons.add(btnInsert);

        btnView = new JButton("View Menus");
        btnView.addActionListener(this::viewMenus);
        panelButtons.add(btnView);

        btnUpdate = new JButton("Update Menu");
        btnUpdate.addActionListener(this::updateMenu);
        panelButtons.add(btnUpdate);

        btnDelete = new JButton("Delete Menu");
        btnDelete.addActionListener(this::deleteMenu);
        panelButtons.add(btnDelete);
        add(panelButtons, BorderLayout.CENTER);

        // menus
        listModel = new DefaultListModel<>();
        listMenu = new JList<>(listModel);
        add(new JScrollPane(listMenu), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void insertMenu(ActionEvent evt) {
        if (!validateInput()) {
            return;
        }

        // random kode_menu
        Random rand = new Random();
        String kode = "PD-" + (100 + rand.nextInt(900));
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Menu (kode_menu, nama_menu, harga_menu, stok_menu) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, kode);
            stmt.setString(2, txtNama.getText());
            stmt.setBigDecimal(3, new BigDecimal(txtHarga.getText()));
            stmt.setInt(4, Integer.parseInt(txtStok.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Menu inserted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting menu: " + e.getMessage());
        }
    }

    private void viewMenus(ActionEvent evt) {
        listModel.clear();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Menu")) {
            while (rs.next()) {
                listModel.addElement(rs.getString("kode_menu") + " - " + rs.getString("nama_menu") + " - " + rs.getBigDecimal("harga_menu") + " - " + rs.getInt("stok_menu"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menus: " + e.getMessage());
        }
    }

    private void updateMenu(ActionEvent evt) {
        String selected = listMenu.getSelectedValue();
        if (selected != null && validateInput()) {
            String kode = selected.split(" - ")[0];
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE Menu SET harga_menu = ?, stok_menu = ? WHERE kode_menu = ?")) {
                stmt.setBigDecimal(1, new BigDecimal(txtHarga.getText()));
                stmt.setInt(2, Integer.parseInt(txtStok.getText()));
                stmt.setString(3, kode);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Menu updated successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error updating menu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a menu to update and ensure all fields are valid.");
        }
    }

    private void deleteMenu(ActionEvent evt) {
        String selected = listMenu.getSelectedValue();
        if (selected != null) {
            String kode = selected.split(" - ")[0];
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Menu WHERE kode_menu = ?")) {
                stmt.setString(1, kode);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Menu deleted successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error deleting menu: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a menu to delete.");
        }
    }

    private boolean validateInput() {
        try {
            new BigDecimal(txtHarga.getText());
            Integer.parseInt(txtStok.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid numbers for price and stock.");
            return false;
        }
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a name for the menu.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CRUDapp::new);
    }

	public JList<String> getListMenu() {
		return listMenu;
	}

	public void setListMenu(JList<String> listMenu) {
		this.listMenu = listMenu;
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	public void setListModel(DefaultListModel<String> listModel) {
		this.listModel = listModel;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public JTextField getTxtNama() {
		return txtNama;
	}

	public JTextField getTxtHarga() {
		return txtHarga;
	}

	public JTextField getTxtStok() {
		return txtStok;
	}

	public JButton getBtnInsert() {
		return btnInsert;
	}

	public JButton getBtnView() {
		return btnView;
	}

	public JButton getBtnUpdate() {
		return btnUpdate;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}
}
