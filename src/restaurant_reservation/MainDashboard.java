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
import javax.swing.JOptionPane;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Neil
 */
public class MainDashboard extends javax.swing.JFrame {

    private DefaultTableModel tableModel;

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
        initializeTable();
        setLocationRelativeTo(null);

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
                new String[]{"ID", "Customer", "Time", "Party Size", "Status"}
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
            String sql = "SELECT r.reservation_id, "
                    + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
                    + "TIME_FORMAT(r.start_time, '%h:%i %p') AS start_time, "
                    + "TIME_FORMAT(r.end_time, '%h:%i %p') AS end_time, "
                    + "r.party_size, "
                    + "s.status_name "
                    + "FROM Reservation r "
                    + "JOIN Customer c ON r.customer_id = c.customer_id "
                    + "JOIN Reservation_Status s ON r.status_id = s.status_id "
                    + "WHERE r.reservation_date = ? "
                    + "ORDER BY r.start_time";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("reservation_id"),
                    rs.getString("customer_name"),
                    rs.getString("start_time") + " - " + rs.getString("end_time"),
                    rs.getInt("party_size"),
                    rs.getString("status_name")
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tableStatusPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
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

        jButton1.setText("Add Table");

        jButton2.setText("Maintenance");

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

        javax.swing.GroupLayout ReservationPanelLayout = new javax.swing.GroupLayout(ReservationPanel);
        ReservationPanel.setLayout(ReservationPanelLayout);
        ReservationPanelLayout.setHorizontalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationPanelLayout.createSequentialGroup()
                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ReservationPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ReservationPanelLayout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(addresrvationbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(modreservationbutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(ReservationPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                            .addComponent(tableStatusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGap(473, 473, 473)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(228, Short.MAX_VALUE))
        );
        ReservationPanelLayout.setVerticalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
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
                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tableStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(209, 209, 209))
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
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
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1080, Short.MAX_VALUE)
            .addComponent(ReservationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        loadReservations(jCalendar1.getDate());
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
    }//GEN-LAST:event_modreservationbuttonActionPerformed
     private void loadTableStatus() {
    tableStatusPanel.removeAll();
    
    // Get the selected date from JCalendar
    Date selectedDate = jCalendar1.getDate();
    System.out.println("Loading table status for date: " + selectedDate); // DEBUG
    
    try (Connection conn = DatabaseConnection.getConnection()) {
        // This query directly checks for reservations on the selected date
        String sql = 
            "SELECT t.table_id, t.table_number, t.capacity, t.is_outdoor, " +
            "r.reservation_id, r.reservation_date, r.start_time, r.end_time, r.status_id " +
            "FROM Table_Layout t " +
            "LEFT JOIN Reservation_Table rt ON t.table_id = rt.table_id " +
            "LEFT JOIN Reservation r ON rt.reservation_id = r.reservation_id " +
            "AND r.reservation_date = ? " +
            "ORDER BY t.table_number";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDate(1, new java.sql.Date(selectedDate.getTime()));
        
        System.out.println("Executing query with date: " + new java.sql.Date(selectedDate.getTime())); // DEBUG
        ResultSet rs = pstmt.executeQuery();

        // For each table
        while (rs.next()) {
            String tableNumber = rs.getString("table_number");
            System.out.println("Processing table: " + tableNumber); // DEBUG
            
            JLabel tableLabel = new JLabel();
            tableLabel.setPreferredSize(new Dimension(150, 100));
            tableLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tableLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            tableLabel.setOpaque(true);
            tableLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            // Check if this table has a reservation for the selected date
            if (rs.getObject("reservation_id") != null) {
                System.out.println("  Table " + tableNumber + " has reservation ID: " + rs.getInt("reservation_id")); // DEBUG
                System.out.println("  Reservation date: " + rs.getDate("reservation_date")); // DEBUG
                System.out.println("  Reservation time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time")); // DEBUG
                
                // This table is reserved for today
                tableLabel.setBackground(Color.ORANGE);
                tableLabel.setText(String.format(
                    "<html><center>%s<br>Reserved<br>%s - %s</center></html>", 
                    tableNumber,
                    rs.getTime("start_time").toString().substring(0, 5),
                    rs.getTime("end_time").toString().substring(0, 5)
                ));
            } else {
                // No reservation for today
                System.out.println("  Table " + tableNumber + " has NO reservation"); // DEBUG
                tableLabel.setBackground(Color.GREEN);
                tableLabel.setText(String.format(
                    "<html><center>%s<br>Available</center></html>", 
                    tableNumber
                ));
            }
            
            // Add tooltip with table details
            tableLabel.setToolTipText(String.format(
                "Capacity: %d\nOutdoor: %s",
                rs.getInt("capacity"),
                rs.getBoolean("is_outdoor") ? "Yes" : "No"
            ));
            
            tableStatusPanel.add(tableLabel);
        }
        
        tableStatusPanel.revalidate();
        tableStatusPanel.repaint();
        
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading table status: " + e.getMessage());
    }
}

    private JLabel createStatusLabel(ResultSet rs) throws SQLException {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(150, 100));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        label.setOpaque(true);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        Date today = new Date(System.currentTimeMillis());
        Time now = new Time(today.getTime());

        // Maintenance check
        if (rs.getObject("availability_id") != null
                && rs.getDate("unavailable_date").equals(today)
                && now.after(rs.getTime("maintenance_start"))
                && now.before(rs.getTime("maintenance_end"))) {

            label.setBackground(Color.RED);
            label.setText("<html><center>Maintenance<br>"
                    + rs.getString("table_number") + "</center></html>");
            return label;
        }

        // Reservation check
        if (rs.getObject("reservation_id") != null
                && rs.getDate("reservation_date").equals(today)) {

            Time start = rs.getTime("reservation_start");
            Time end = rs.getTime("reservation_end");

            if (now.after(start) && now.before(end)) {
                label.setBackground(new Color(255, 165, 0)); // Orange
                label.setText("<html><center>Occupied<br>"
                        + rs.getString("table_number") + "</center></html>");
            } else if (now.before(start)) {
                label.setBackground(Color.YELLOW);
                label.setText("<html><center>Reserved<br>"
                        + rs.getString("table_number") + "</center></html>");
            }
            return label;
        }

        // Default available
        label.setBackground(Color.GREEN);
        label.setText("<html><center>Available<br>"
                + rs.getString("table_number") + "</center></html>");
        return label;
    }

    private JLabel createColoredLabel(ResultSet rs, Color color, String text)
            throws SQLException {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(150, 100));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText("<html><center>" + text + "</center></html>");
        label.setBackground(color);
        label.setOpaque(true);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        // Add tooltip with table details
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
