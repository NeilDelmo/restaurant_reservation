/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package restaurant_reservation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Neil
 */
public class DatabaseConnection {
    private final static String URL = "jdbc:mysql://localhost:3307/restaurant_reservation";
    private final static String USER = "root";
    private final static String PASS = "";
    
    public static Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(URL,USER,PASS);
            System.out.println("Database Connected Successfully");
        }catch(SQLException e){
           e.printStackTrace();
        }
        return connection;
    }
    
}
