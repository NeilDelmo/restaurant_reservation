/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package restaurant_reservation;

/**
 *
 * @author Neil
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
public class Report extends javax.swing.JFrame {

    /**
     * Creates new form Report
     */
    private JTabbedPane tabbedPane;

    public Report() {
        setTitle("Restaurant Performance Report");
        setSize(1400, 800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        createDailyReport();
        createMonthlyReport();
    }

    private void createDailyReport() {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries reservations = new TimeSeries("Reservations");
        TimeSeries income = new TimeSeries("Income");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Daily reservations
            String resSql = "SELECT reservation_date, COUNT(*) AS count " +
                "FROM Reservation " +
                "GROUP BY reservation_date";

            // Daily income
            String incSql = "SELECT r.reservation_date, SUM(p.amount) AS total " +
                "FROM Reservation r " +
                "JOIN Payment p ON r.reservation_id = p.reservation_id " +
                "GROUP BY r.reservation_date";

            try (Statement stmt = conn.createStatement()) {
                ResultSet resRs = stmt.executeQuery(resSql);
                while (resRs.next()) {
                    Date date = resRs.getDate("reservation_date");
                    if (date != null) {
                        reservations.add(new Day(date), resRs.getInt("count"));
                    }
                }

                ResultSet incRs = stmt.executeQuery(incSql);
                while (incRs.next()) {
                    Date date = incRs.getDate("reservation_date");
                    if (date != null) {
                        income.add(new Day(date), incRs.getDouble("total"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }

        dataset.addSeries(reservations);
        dataset.addSeries(income);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Daily Report",
                "Date",
                "Value",
                dataset
        );

        configureChart(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        tabbedPane.addTab("Report", chartPanel);
    }

    private void createMonthlyReport() {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries reservations = new TimeSeries("Reservations");
        TimeSeries income = new TimeSeries("Income");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Monthly reservations
           // Monthly reservations - show ALL reservations
String resSql = "SELECT DATE_FORMAT(reservation_date, '%Y-%m-01') AS month_start, " +
                "COUNT(*) AS count " +
                "FROM Reservation " +
                "GROUP BY month_start";

            // Monthly income
           // Monthly income - show ALL payments
String incSql = "SELECT DATE_FORMAT(r.reservation_date, '%Y-%m-01') AS month_start, " +
                "SUM(p.amount) AS total " +
                "FROM Reservation r " +
                "JOIN Payment p ON r.reservation_id = p.reservation_id " +
                "GROUP BY month_start";

            try (Statement stmt = conn.createStatement()) {
                ResultSet resRs = stmt.executeQuery(resSql);
                while (resRs.next()) {
                    Date date = resRs.getDate("month_start");
                    if (date != null) {
                        reservations.add(new Month(date), resRs.getInt("count"));
                    }
                }

                ResultSet incRs = stmt.executeQuery(incSql);
                while (incRs.next()) {
                    Date date = incRs.getDate("month_start");
                    if (date != null) {
                        income.add(new Month(date), incRs.getDouble("total"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }

        dataset.addSeries(reservations);
        dataset.addSeries(income);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Monthly Report",
                "Month",
                "Value",
                dataset
        );

        configureChart(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        tabbedPane.addTab("Monthly Report", chartPanel);
    }

    private void configureChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Create dual axes
        NumberAxis resAxis = new NumberAxis("Reservations");
        resAxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(0, resAxis);

        NumberAxis incAxis = new NumberAxis("Income");
        incAxis.setAutoRangeIncludesZero(false);
        incAxis.setNumberFormatOverride(NumberFormat.getCurrencyInstance());
        plot.setRangeAxis(1, incAxis);

        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        // Configure renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 204)); // Blue
        renderer.setSeriesPaint(1, new Color(0, 153, 51));  // Green
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setDefaultItemLabelGenerator(new CustomLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        plot.setRenderer(renderer);

        // Format date axis
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM yyyy"));
    }

   private static class CustomLabelGenerator extends StandardXYItemLabelGenerator {
    private DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");

    @Override
    public String generateLabel(XYDataset dataset, int series, int item) {
        if (dataset instanceof TimeSeriesCollection) {
            // Get time period using series and item indices
            RegularTimePeriod period = ((TimeSeriesCollection) dataset)
                .getSeries(series)
                .getTimePeriod(item);
            
            Number value = dataset.getY(series, item);

            if (period instanceof Day) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            } else if (period instanceof Month) {
                dateFormat = new SimpleDateFormat("MMM yyyy");
            }

            return String.format("%s\n%s",
                    dateFormat.format(period.getStart()),
                    value instanceof Double ?
                            NumberFormat.getCurrencyInstance().format(value) :
                            NumberFormat.getIntegerInstance().format(value));
        }
        return super.generateLabel(dataset, series, item);
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
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Report().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
