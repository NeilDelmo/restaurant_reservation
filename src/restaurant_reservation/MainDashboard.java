/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package restaurant_reservation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Date;
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

    initComponents(); // NetBeans-generated code
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
    
    
    
    
    
    
    
    initializeTable();
    setupCalendarListener();
    loadReservations(new Date());
    
    // Fix for JCalendar display issues

    // Rest of your existing initialization code
    jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Customer", "Time", "Party Size", "Status"}
    ));

    jCalendar1.addPropertyChangeListener("calendar", (evt) -> {
        JOptionPane.showMessageDialog(this, "Date changed!");
    });
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
        // Add date change listener to JCalendar
        jCalendar1.addPropertyChangeListener("calendar", evt -> {
            Date selectedDate = jCalendar1.getDate();
            loadReservations(selectedDate);
        });
    }

    private void loadReservations(Date date) {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT r.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
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
        rs.getInt("reservation_id"), // Add this
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
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ReservationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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

        javax.swing.GroupLayout ReservationPanelLayout = new javax.swing.GroupLayout(ReservationPanel);
        ReservationPanel.setLayout(ReservationPanelLayout);
        ReservationPanelLayout.setHorizontalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        ReservationPanelLayout.setVerticalGroup(
            ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(ReservationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ReservationPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(addresrvationbutton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modreservationbutton)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel2.setText("Restaurant Reservation");
        jPanel2.add(jLabel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ReservationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ReservationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(309, Short.MAX_VALUE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton modreservationbutton;
    // End of variables declaration//GEN-END:variables
}
