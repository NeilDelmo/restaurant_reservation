/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package restaurant_reservation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.DefaultListModel;
import java.sql.*;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Date; // Add this at the top of your file
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Neil
 */
public class Reservationdialog extends javax.swing.JFrame {

    private static final String STATUS_CONFIRMED = "Confirmed";
    private int currentRestaurantId = 1;
    private Integer reservationId;

    /**
     * Creates new form Reservationdialog
     */
    public Reservationdialog(int reservationId) {
        this(); // Call default constructor
        this.reservationId = reservationId;
        loadReservationData();
    }

    public Reservationdialog() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initComponents(); // NetBeans-generated code
        setLocationRelativeTo(null);

        com.toedter.calendar.JDayChooser dayChooser = datePicker.getDayChooser();
        dayChooser.setForeground(Color.BLACK);
        dayChooser.setBackground(Color.WHITE);
        dayChooser.setSundayForeground(Color.RED);

        dayChooser.setOpaque(true);
        datePicker.setOpaque(true);
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);
        dayChooser.setFont(boldFont);
        dayChooser.getDayPanel().setFont(boldFont);

        datePicker.setPreferredSize(new Dimension(300, 200));
        dayChooser.setPreferredSize(new Dimension(280, 150));
        SwingUtilities.invokeLater(() -> {
            datePicker.updateUI();
            datePicker.revalidate();
            datePicker.repaint();
        });
        datePicker.setMinSelectableDate(new java.util.Date());
        setupComponents();
    }

    private void setupComponents() {
        // Configure time spinners with proper models
        SpinnerDateModel startModel = new SpinnerDateModel(
                new java.util.Date(), // Current time
                null, // No min date
                null, // No max date
                Calendar.MINUTE // Allow minute increments
        );
        spStartTime.setModel(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(spStartTime, "hh:mm a");
        spStartTime.setEditor(startEditor);

        SpinnerDateModel endModel = new SpinnerDateModel(
                new java.util.Date(),
                null,
                null,
                Calendar.MINUTE
        );
        spEndTime.setModel(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(spEndTime, "hh:mm a");
        spEndTime.setEditor(endEditor);

        // Add change listeners
        spStartTime.addChangeListener(e -> loadAvailableTables());
        spEndTime.addChangeListener(e -> loadAvailableTables());

        // Party size spinner
        spPartySize.setModel(new SpinnerNumberModel(2, 1, 20, 1));

        // Date picker listener
        datePicker.addPropertyChangeListener("calendar", evt -> loadAvailableTables());

        txtTotalAmount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDeposit();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDeposit();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDeposit();
            }

            private void updateDeposit() {
                try {
                    double total = Double.parseDouble(txtTotalAmount.getText());
                    double deposit = total * 0.3;
                    lblDeposit.setText(String.format("$%.2f", deposit));
                } catch (NumberFormatException ex) {
                    lblDeposit.setText("Invalid amount");
                }
            }
        });

        // Load statuses from database
        loadStatuses();

        // Set default status for new reservations
        if (reservationId == null) {
            cbStatus.setSelectedItem("Pending Deposit");
            cbStatus.setEnabled(false);
        }
    }

    private void loadStatuses() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT status_name FROM Reservation_Status ORDER BY status_id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("status_name"));
            }
            cbStatus.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback if DB fails
            model.addElement("Pending Deposit");
            model.addElement("Deposit Paid");
            model.addElement("Confirmed");
            model.addElement("Cancelled");
            model.addElement("Completed");
            model.addElement("No-Show");
        }
    }

    private int getOrCreateCustomer(Connection conn) throws SQLException {
        String email = jTextField3.getText().trim();

        // Validate email format
        if (email.isEmpty() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email address");
            return -1;
        }

        // Check existing customer
        String checkSql = "SELECT customer_id FROM Customer WHERE email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        }

        // Create new customer
        String insertSql = "INSERT INTO Customer (first_name, last_name, email, phone, preferences) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            // Validate required fields
            String firstName = jTextField1.getText().trim();
            String lastName = jTextField2.getText().trim();
            String phone = jTextField4.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name and last name are required");
                return -1;
            }

            insertStmt.setString(1, firstName);
            insertStmt.setString(2, lastName);
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, jTextArea1.getText().trim());

            int affectedRows = insertStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected");
            }

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique constraint violation
                JOptionPane.showMessageDialog(this, "Email already exists in system");
                return -1;
            }
            throw e;
        }
    }

    private void loadAvailableTables() {
        DefaultListModel<String> model = new DefaultListModel<>();
        lstAvailableTables.setModel(model);

        // Date validation
        Date selectedDate = datePicker.getDate();
        Date startTime = (Date) spStartTime.getValue();
        Date endTime = (Date) spEndTime.getValue();
        if (selectedDate == null) {
            return;
        }

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);

        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);

        if (selectedCal.before(todayCal)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot select past dates",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            datePicker.setDate(new Date());
            return;
        }
        if (endTime.before(startTime)) {
            JOptionPane.showMessageDialog(this, "End time must be after start time");
            return; // Exit early to avoid invalid queries
        }

        // Database query remains the same
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT t.table_id, t.table_number, t.capacity "
                    + "FROM Table_Layout t "
                    + "WHERE t.restaurant_id = ? "
                    + "AND t.table_id NOT IN ("
                    + "  SELECT table_id FROM Table_Availability "
                    + "  WHERE unavailable_date = ? "
                    + "  AND ? BETWEEN start_time AND end_time"
                    + ") "
                    + "AND t.table_id NOT IN ("
                    + "  SELECT rt.table_id FROM Reservation_Table rt "
                    + "  JOIN Reservation r ON rt.reservation_id = r.reservation_id "
                    + "  WHERE r.reservation_date = ? "
                    + "  AND NOT (r.end_time <= ? OR r.start_time >= ?)"
                    + ")";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentRestaurantId);
            pstmt.setDate(2, new java.sql.Date(selectedDate.getTime()));  // Unavailable date
            pstmt.setTime(3, getStartTime());                             // Availability time check
            pstmt.setDate(4, new java.sql.Date(selectedDate.getTime()));   // Reservation date
            pstmt.setTime(5, getStartTime());                             // Existing reservation end time
            pstmt.setTime(6, getEndTime());                               // Existing reservation start time

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addElement(
                        rs.getInt("table_id") + " - "
                        + rs.getString("table_number") + " (Capacity: " + rs.getInt("capacity") + ")"
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + ex.getMessage());
        }
    }

    private Time getStartTime() {
        Date date = (Date) spStartTime.getValue();
        return new Time(((Date) spStartTime.getValue()).getTime());
    }

    private Time getEndTime() {
        Date date = (Date) spEndTime.getValue(); // Rename spinner variable appropriately
        return new Time(((Date) spEndTime.getValue()).getTime());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        datePicker = new com.toedter.calendar.JCalendar();
        jLabel7 = new javax.swing.JLabel();
        spStartTime = new javax.swing.JSpinner();
        spEndTime = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAvailableTables = new javax.swing.JList<>();
        spPartySize = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSpecialRequests = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox<>();
        btnSubmit = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lblDeposit = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));

        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Enter first name:");

        jLabel2.setText("Enter last name:");

        jLabel3.setText("Enter email:");

        jLabel4.setText("Enter phone:");

        jLabel5.setText("Preferences:");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel6.setText("Customer Information");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextField4)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel7.setText("Select Date:");

        jLabel8.setText("Select Time:");

        jLabel9.setText("Number of guest:");

        lstAvailableTables.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstAvailableTables);

        jLabel10.setText("Available Tables:");

        jLabel11.setText("Special Request:");

        txtSpecialRequests.setColumns(20);
        txtSpecialRequests.setRows(5);
        jScrollPane3.setViewportView(txtSpecialRequests);

        jLabel12.setText("Status:");

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Confirmed", "Cancelled", "Completed", "No-Show" }));

        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(spStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spPartySize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(78, 78, 78))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSubmit)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89)
                                .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(spPartySize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel12))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSubmit)
                .addGap(19, 19, 19))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel13.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel13.setText("Required Deposit:");

        txtTotalAmount.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel14.setText("TOTAL AMOUNT:");

        lblDeposit.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        lblDeposit.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalAmount)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(lblDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(lblDeposit)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        if (reservationId != null) {
            updateReservation();
        } else {
            createNewReservation();
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void createNewReservation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Validate date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(datePicker.getDate());
            selectedDate.set(Calendar.HOUR_OF_DAY, 0);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);

            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);

            if (selectedDate.before(currentDate)) {
                JOptionPane.showMessageDialog(this, "Cannot book past dates");
                return;
            }
            String totalStr = txtTotalAmount.getText().trim();
            if (totalStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Total amount is required");
                return;
            }
            double totalAmount;
            try {
                totalAmount = Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid total amount");
                return;
            }

            // 2. Create/Get customer
            int customerId = getOrCreateCustomer(conn);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Failed to create customer");
                return;
            }

            // 3. Create reservation
            int reservationId = createReservation(conn, customerId, totalAmount);
            createPayment(conn, reservationId, totalAmount);

            // 4. Assign tables
            assignTables(conn, reservationId);

            // 5. Commit transaction
            conn.commit();
            JOptionPane.showMessageDialog(this, "Reservation created successfully!");
            this.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void createPayment(Connection conn, int reservationId, double totalAmount) throws SQLException {
        String sql = "INSERT INTO Payment (reservation_id, amount, payment_type, status) "
                + "VALUES (?, ?, 'deposit', 'pending')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.setDouble(2, totalAmount * 0.3);
            pstmt.executeUpdate();
        }
    }

    private void updateReservation() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Validate date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(datePicker.getDate());
            selectedDate.set(Calendar.HOUR_OF_DAY, 0);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);

            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);

            if (selectedDate.before(currentDate)) {
                JOptionPane.showMessageDialog(this, "Cannot update to past dates");
                return;
            }

            // 2. Update customer
            updateCustomer(conn);

            // 3. Update reservation
            String sql = "UPDATE Reservation SET "
                    + "reservation_date = ?, "
                    + "start_time = ?, "
                    + "end_time = ?, "
                    + "party_size = ?, "
                    + "status_id = ?, "
                    + "special_requests = ? "
                    + "WHERE reservation_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(datePicker.getDate().getTime()));
                pstmt.setTime(2, getStartTime());
                pstmt.setTime(3, getEndTime());
                pstmt.setInt(4, (Integer) spPartySize.getValue());
                pstmt.setInt(5, getStatusId(cbStatus.getSelectedItem().toString()));
                pstmt.setString(6, txtSpecialRequests.getText());
                pstmt.setInt(7, reservationId);
                pstmt.executeUpdate();
            }

            // 4. Update tables
            updateTables(conn);

            // 5. Commit transaction
            conn.commit();
            JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
            this.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateCustomer(Connection conn) throws SQLException {
        String sql = "UPDATE Customer SET "
                + "first_name = ?, "
                + "last_name = ?, "
                + "email = ?, "
                + "phone = ?, "
                + "preferences = ? "
                + "WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jTextField1.getText());
            pstmt.setString(2, jTextField2.getText());
            pstmt.setString(3, jTextField3.getText());
            pstmt.setString(4, jTextField4.getText());
            pstmt.setString(5, jTextArea1.getText());
            pstmt.setInt(6, getExistingCustomerId(conn));
            pstmt.executeUpdate();
        }
    }

    private void updateTables(Connection conn) throws SQLException {
        // Remove old tables
        String deleteSql = "DELETE FROM Reservation_Table WHERE reservation_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, reservationId);
            pstmt.executeUpdate();
        }

        // Assign new tables
        assignTables(conn, reservationId);
    }

    private int getExistingCustomerId(Connection conn) throws SQLException {
        String sql = "SELECT customer_id FROM Reservation WHERE reservation_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("customer_id") : -1;
        }
    }

    private int getStatusId(String statusName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT status_id FROM Reservation_Status WHERE status_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 1; // Default to first status
        }
    }

    private int createReservation(Connection conn, int customerId, double totalAmount) throws SQLException {
        String sql = "INSERT INTO Reservation (customer_id, restaurant_id, reservation_date, "
                + "start_time, end_time, party_size, status_id, special_requests, total_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, currentRestaurantId);
            pstmt.setDate(3, new java.sql.Date(datePicker.getDate().getTime()));
            pstmt.setTime(4, getStartTime());
            pstmt.setTime(5, getEndTime());
            pstmt.setInt(6, (Integer) spPartySize.getValue());
            pstmt.setInt(7, getStatusId("Pending Deposit")); // Force status for new reservations
            pstmt.setString(8, txtSpecialRequests.getText());
            pstmt.setDouble(9, totalAmount);
            pstmt.executeUpdate();

            // Retrieve generated reservation ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Reservation creation failed");
            }
        }
    }

    private void assignTables(Connection conn, int reservationId) throws SQLException {
        String sql = "INSERT INTO Reservation_Table (reservation_id, table_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String tableInfo : lstAvailableTables.getSelectedValuesList()) {
                int tableId = Integer.parseInt(tableInfo.split(" - ")[0]);
                pstmt.setInt(1, reservationId);
                pstmt.setInt(2, tableId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void loadReservationData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT r.*, rs.status_name, p.amount AS deposit "
                    + "FROM Reservation r "
                    + "JOIN Reservation_Status rs ON r.status_id = rs.status_id "
                    + "LEFT JOIN Payment p ON r.reservation_id = p.reservation_id AND p.payment_type = 'deposit' "
                    + "WHERE r.reservation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Populate customer info
                jTextField1.setText(rs.getString("first_name"));
                jTextField2.setText(rs.getString("last_name"));
                jTextField3.setText(rs.getString("email"));
                jTextField4.setText(rs.getString("phone"));
                jTextArea1.setText(rs.getString("preferences"));

                // Set date and time
                datePicker.setDate(rs.getDate("reservation_date"));
                spStartTime.setValue(rs.getTime("start_time"));
                spEndTime.setValue(rs.getTime("end_time"));

                // Set other fields
                spPartySize.setValue(rs.getInt("party_size"));
                cbStatus.setSelectedItem(rs.getString("status_name"));
                txtSpecialRequests.setText(rs.getString("special_requests"));
                txtTotalAmount.setText(String.valueOf(rs.getDouble("total_amount")));
                lblDeposit.setText(String.format("$%.2f", rs.getDouble("deposit")));
                cbStatus.setSelectedItem(rs.getString("status_name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reservation: " + ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Reservationdialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Reservationdialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Reservationdialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Reservationdialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Reservationdialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cbStatus;
    private com.toedter.calendar.JCalendar datePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblDeposit;
    private javax.swing.JList<String> lstAvailableTables;
    private javax.swing.JSpinner spEndTime;
    private javax.swing.JSpinner spPartySize;
    private javax.swing.JSpinner spStartTime;
    private javax.swing.JTextArea txtSpecialRequests;
    private javax.swing.JTextField txtTotalAmount;
    // End of variables declaration//GEN-END:variables
}
