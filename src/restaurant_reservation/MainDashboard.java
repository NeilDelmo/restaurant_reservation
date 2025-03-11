/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package restaurant_reservation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Neil
 */
public class MainDashboard extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    private javax.swing.JTable reservationsTable;

    /**
     * Creates new form MainDashboard
     */
    public MainDashboard() {
        // 1. FIRST: Force a compatible Look & Feel before initializing components
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initComponents();
        reservationsTable = jTable1;
        initializeTable();
        setLocationRelativeTo(null);
        
         reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 2. DIRECTLY MODIFY JCalendar's INTERNAL COMPONENTS
        com.toedter.calendar.JDayChooser dayChooser = jCalendar1.getDayChooser();

        // Fix day numbers visibility
        dayChooser.setForeground(Color.BLACK);
        dayChooser.setBackground(Color.WHITE);
        dayChooser.setSundayForeground(Color.RED);

        // Force component transparency settings
        dayChooser.setOpaque(true);
        jCalendar1.setOpaque(true);

        // 3. SET FONT HIERARCHY EXPLICITLY
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);
        dayChooser.setFont(boldFont);
        dayChooser.getDayPanel().setFont(boldFont);

        // 4. SET PREFERRED SIZES (Workaround for layout issues)
        jCalendar1.setPreferredSize(new Dimension(300, 200));
        dayChooser.setPreferredSize(new Dimension(280, 150));

        // 5. NUCLEAR OPTION: Replace the calendar completely
        // Uncomment these lines if nothing else works
        // javax.swing.JPanel calendarPanel = (javax.swing.JPanel) jCalendar1.getParent();
        // calendarPanel.removeAll();
        // calendarPanel.add(new com.toedter.calendar.JCalendar());
        // calendarPanel.revalidate();
        // 6. FINAL FORCED REFRESH
        SwingUtilities.invokeLater(() -> {
            jCalendar1.updateUI();
            jCalendar1.revalidate();
            jCalendar1.repaint();
        });

        setupCalendarListener();
        loadReservations(new Date());
        tableStatusPanel.setLayout(new GridLayout(0, 4));
        loadTableStatus();

    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Customer", "Time", "Party Size", "Status", "Status ID", "Total", "Deposit"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        jTable1.setModel(tableModel);
        jTable1.removeColumn(jTable1.getColumnModel().getColumn(0)); // Hide ID column
    }

    private void setupCalendarListener() {
        jCalendar1.getDayChooser().addPropertyChangeListener("day", evt -> {
            System.out.println("Date changed to: " + jCalendar1.getDate()); // Debug line
            loadReservations(jCalendar1.getDate());
            loadTableStatus(); // Add this line to update table status when date changes
        });
    }

  private void loadReservations(Date date) {
    tableModel.setRowCount(0); // Clear existing data

    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT " +
                     "r.reservation_id, " +
                     "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
                     "TIME_FORMAT(r.start_time, '%h:%i %p') AS start_time, " +
                     "TIME_FORMAT(r.end_time, '%h:%i %p') AS end_time, " +
                     "r.party_size, " +
                     "s.status_name, " +
                     "r.status_id, " + // Include status ID for programmatic checks
                     "r.total_amount, " +
                     "r.downpayment_amount " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.customer_id = c.customer_id " +
                     "JOIN Reservation_Status s ON r.status_id = s.status_id " +
                     "WHERE r.reservation_date = ? " +
                     "ORDER BY r.start_time";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDate(1, new java.sql.Date(date.getTime()));
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getInt("reservation_id"),
                rs.getString("customer_name"),
                rs.getString("start_time") + " - " + rs.getString("end_time"),
                rs.getInt("party_size"),
                rs.getString("status_name"),
                rs.getInt("status_id"), // Hidden column for internal use
                rs.getDouble("total_amount"),
                rs.getDouble("downpayment_amount")
            };
            
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading reservations");
    }
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
        ReservationPanel = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        addresrvationbutton = new javax.swing.JButton();
        modreservationbutton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tableStatusPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ReservationPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Select the Date:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        addresrvationbutton.setText("Add Reservation");
        addresrvationbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addresrvationbuttonActionPerformed(evt);
            }
        });

        modreservationbutton.setText("Modify Reservation");
        modreservationbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modreservationbuttonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel3.setText("Reservation");

        tableStatusPanel.setBorder(new javax.swing.border.MatteBorder(null));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel4.setText("Tables");

        javax.swing.GroupLayout tableStatusPanelLayout = new javax.swing.GroupLayout(tableStatusPanel);
        tableStatusPanel.setLayout(tableStatusPanelLayout);
        tableStatusPanelLayout.setHorizontalGroup(
            tableStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableStatusPanelLayout.createSequentialGroup()
                .addGroup(tableStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableStatusPanelLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tableStatusPanelLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tableStatusPanelLayout.setVerticalGroup(
            tableStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableStatusPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jButton3.setText("Mark as Deposit Paid");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Reports");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ReservationPanelLayout = new javax.swing.GroupLayout(ReservationPanel);
        ReservationPanel.setLayout(ReservationPanelLayout);
        ReservationPanelLayout.setHorizontalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationPanelLayout.createSequentialGroup()
                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGap(473, 473, 473)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(addresrvationbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(modreservationbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(tableStatusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(153, Short.MAX_VALUE))
        );
        ReservationPanelLayout.setVerticalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(addresrvationbutton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(modreservationbutton))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tableStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(209, 209, 209))
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jButton3)
                        .addGap(119, 119, 119)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel2.setText("Restaurant Reservation");
        jPanel2.add(jLabel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(ReservationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ReservationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addresrvationbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addresrvationbuttonActionPerformed
        // TODO add your handling code here:
        new Reservationdialog().setVisible(true);
        Date selectedDate = jCalendar1.getDate();
        loadReservations(selectedDate);
        loadTableStatus();
    }//GEN-LAST:event_addresrvationbuttonActionPerformed

    private void modreservationbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modreservationbuttonActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        Reservationdialog dialog = new Reservationdialog(reservationId);
        dialog.setVisible(true);
        loadReservations(jCalendar1.getDate());
        loadTableStatus();
    }//GEN-LAST:event_modreservationbuttonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = reservationsTable.getSelectedRow(); // Now matches variable name
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a reservation");
        return;
    }

    try {
        // Get status ID from hidden column (column index 5)
        int statusId = (int) tableModel.getValueAt(selectedRow, 5);
        
        if (statusId != 5) { // 5 = Pending Deposit status
            JOptionPane.showMessageDialog(this, "Only pending deposits can be marked as paid");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Update reservation status
            String updateSql = "UPDATE Reservation SET status_id = 6 WHERE reservation_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, reservationId);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // Create payment record
                    String paymentSql = "INSERT INTO Payment (reservation_id, amount, payment_type, status) " +
                                        "VALUES (?, (SELECT downpayment_amount FROM Reservation WHERE reservation_id = ?), 'deposit', 'completed')";
                    try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql)) {
                        paymentStmt.setInt(1, reservationId);
                        paymentStmt.setInt(2, reservationId);
                        paymentStmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Deposit marked as paid successfully");
                    loadReservations(jCalendar1.getDate()); // Refresh table
                }
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating payment status");
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Report rep = new Report();
        rep.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed
  private void loadTableStatus() {
    tableStatusPanel.removeAll();
    Date selectedDate = jCalendar1.getDate();

    // Determine if selected date is today
    Calendar selectedCal = Calendar.getInstance();
    selectedCal.setTime(selectedDate);
    selectedCal.set(Calendar.HOUR_OF_DAY, 0);
    selectedCal.set(Calendar.MINUTE, 0);
    selectedCal.set(Calendar.SECOND, 0);
    selectedCal.set(Calendar.MILLISECOND, 0);

    Calendar todayCal = Calendar.getInstance();
    todayCal.set(Calendar.HOUR_OF_DAY, 0);
    todayCal.set(Calendar.MINUTE, 0);
    todayCal.set(Calendar.SECOND, 0);
    todayCal.set(Calendar.MILLISECOND, 0);

    boolean isToday = selectedCal.getTimeInMillis() == todayCal.getTimeInMillis();
    Time currentTime = isToday ? new Time(System.currentTimeMillis()) : null;

    // Use a Map to track tables we've already processed
    Map<Integer, JLabel> processedTables = new HashMap<>();

    try (Connection conn = DatabaseConnection.getConnection()) {
        // First, get all tables
        String tablesSql = "SELECT table_id, table_number, capacity, is_outdoor FROM Table_Layout ORDER BY table_number";
        PreparedStatement tableStmt = conn.prepareStatement(tablesSql);
        ResultSet tablesRs = tableStmt.executeQuery();
        
        // Create a map of table IDs to their basic data
        Map<Integer, Map<String, Object>> tablesData = new HashMap<>();
        while (tablesRs.next()) {
            Map<String, Object> tableData = new HashMap<>();
            int tableId = tablesRs.getInt("table_id");
            tableData.put("table_id", tableId);
            tableData.put("table_number", tablesRs.getString("table_number"));
            tableData.put("capacity", tablesRs.getInt("capacity"));
            tableData.put("is_outdoor", tablesRs.getBoolean("is_outdoor"));
            tableData.put("status", "Available"); // Default status
            tableData.put("color", Color.GREEN);  // Default color
            tableData.put("time_info", "");       // Default time info
            
            tablesData.put(tableId, tableData);
        }
        
        // Next, check for maintenance
        String maintSql = "SELECT table_id, start_time, end_time FROM Table_Availability " +
                          "WHERE unavailable_date = ?";
        PreparedStatement maintStmt = conn.prepareStatement(maintSql);
        maintStmt.setDate(1, new java.sql.Date(selectedDate.getTime()));
        ResultSet maintRs = maintStmt.executeQuery();
        
        // Update table data with maintenance info
        while (maintRs.next()) {
            int tableId = maintRs.getInt("table_id");
            if (tablesData.containsKey(tableId)) {
                Map<String, Object> tableData = tablesData.get(tableId);
                Time maintStart = maintRs.getTime("start_time");
                Time maintEnd = maintRs.getTime("end_time");
                
                if (maintStart != null && maintEnd != null) {
                    if (!isToday || (currentTime != null && currentTime.after(maintStart) && currentTime.before(maintEnd))) {
                        tableData.put("status", "Maintenance");
                        tableData.put("color", Color.RED);
                    }
                } else {
                    // All day maintenance
                    tableData.put("status", "Maintenance");
                    tableData.put("color", Color.RED);
                }
            }
        }
        
        // Finally, check for reservations (only if not under maintenance)
        String resSql = "SELECT rt.table_id, r.start_time, r.end_time " +
                        "FROM Reservation_Table rt " +
                        "JOIN Reservation r ON rt.reservation_id = r.reservation_id " +
                        "WHERE r.reservation_date = ? " +
                        "AND r.status_id IN (1, 5, 6) " + // Confirmed, Pending Deposit, Deposit Paid
                        "ORDER BY r.start_time";
        
        PreparedStatement resStmt = conn.prepareStatement(resSql);
        resStmt.setDate(1, new java.sql.Date(selectedDate.getTime()));
        ResultSet resRs = resStmt.executeQuery();
        
        // Process reservation information
        while (resRs.next()) {
            int tableId = resRs.getInt("table_id");
            if (tablesData.containsKey(tableId)) {
                Map<String, Object> tableData = tablesData.get(tableId);
                
                // Only update if not already marked for maintenance
                if (!tableData.get("status").equals("Maintenance")) {
                    Time start = resRs.getTime("start_time");
                    Time end = resRs.getTime("end_time");
                    
                    if (start != null && end != null) {
                        // Format time to 12-hour with AM/PM
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        String startStr = sdf.format(start);
                        String endStr = sdf.format(end);
                        String timeRange = startStr + " - " + endStr;
                        
                        if (isToday && currentTime != null) {
                            if (currentTime.after(start) && currentTime.before(end)) {
                                tableData.put("status", "Occupied");
                                tableData.put("color", Color.ORANGE);
                                tableData.put("time_info", timeRange);
                            } else if (currentTime.before(start)) {
                                tableData.put("status", "Reserved");
                                tableData.put("color", Color.YELLOW);
                                tableData.put("time_info", timeRange);
                            }
                        } else {
                            // Not today, show as reserved
                            tableData.put("status", "Reserved");
                            tableData.put("color", Color.YELLOW);
                            tableData.put("time_info", timeRange);
                        }
                    }
                }
            }
        }
        
        // Now create and add the labels for each table
        for (Map<String, Object> tableData : tablesData.values()) {
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(150, 100));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            String displayText = String.format("<html><center>%s<br>%s</center></html>",
                    tableData.get("table_number"),
                    tableData.get("status") + (tableData.get("time_info").toString().isEmpty() ? "" : 
                                              "<br>" + tableData.get("time_info")));
            
            label.setText(displayText);
            label.setBackground((Color)tableData.get("color"));
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            
            String tooltip = String.format(
                    "<html>Table: %s<br>Capacity: %d<br>Outdoor: %s</html>",
                    tableData.get("table_number"),
                    tableData.get("capacity"),
                    (Boolean)tableData.get("is_outdoor") ? "Yes" : "No"
            );
            label.setToolTipText(tooltip);
            
            tableStatusPanel.add(label);
        }
        
        tableStatusPanel.revalidate();
        tableStatusPanel.repaint();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading table status");
    }
}

private JLabel createStatusLabel(ResultSet rs, boolean isToday, Time currentTime) throws SQLException {
    int tableId = rs.getInt("table_id");
    String tableNumber = rs.getString("table_number");
    boolean hasMaintenance = rs.getObject("availability_id") != null;
    boolean hasReservation = rs.getObject("reservation_id") != null;

    // Maintenance check
    if (hasMaintenance) {
        Time maintStart = rs.getTime("maint_start");
        Time maintEnd = rs.getTime("maint_end");
        
        if (maintStart != null && maintEnd != null) {
            if (!isToday || (currentTime != null && currentTime.after(maintStart) && currentTime.before(maintEnd))) {
                return createColoredLabel(rs, Color.RED, "Maintenance");
            }
        } else {
            // If no specific time range, consider it under maintenance all day
            return createColoredLabel(rs, Color.RED, "Maintenance");
        }
    }

    // Reservation check
    if (hasReservation) {
        Time start = rs.getTime("res_start");
        Time end = rs.getTime("res_end");
        
        if (start != null && end != null) {
            // Format time to 12-hour with AM/PM
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            String startStr = sdf.format(start);
            String endStr = sdf.format(end);
            String timeRange = startStr + " - " + endStr;

            if (isToday && currentTime != null) {
                if (currentTime.after(start) && currentTime.before(end)) {
                    return createColoredLabel(rs, Color.ORANGE, "Occupied\n" + timeRange);
                } else if (currentTime.before(start)) {
                    return createColoredLabel(rs, Color.YELLOW, "Reserved\n" + timeRange);
                } else {
                    // After the reservation time
                    return createColoredLabel(rs, Color.GREEN, "Available");
                }
            } else {
                // Not today, show as reserved
                return createColoredLabel(rs, Color.YELLOW, "Reserved\n" + timeRange);
            }
        }
    }

    // Default available
    return createColoredLabel(rs, Color.GREEN, "Available");
}

private JLabel createColoredLabel(ResultSet rs, Color color, String status) throws SQLException {
    JLabel label = new JLabel();
    label.setPreferredSize(new Dimension(150, 100));
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    label.setText(String.format("<html><center>%s<br>%s</center></html>",
            rs.getString("table_number"), status));
    label.setBackground(color);
    label.setOpaque(true);
    label.setFont(new Font("Arial", Font.BOLD, 14));

    String tooltip = String.format(
            "<html>Table: %s<br>Capacity: %d<br>Outdoor: %s</html>",
            rs.getString("table_number"),
            rs.getInt("capacity"),
            rs.getBoolean("is_outdoor") ? "Yes" : "No"
    );
    label.setToolTipText(tooltip);
    return label;
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
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ReservationPanel;
    private javax.swing.JButton addresrvationbutton;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton modreservationbutton;
    private javax.swing.JPanel tableStatusPanel;
    // End of variables declaration//GEN-END:variables
}
