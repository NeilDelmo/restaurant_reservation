package restaurant_reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private final static String URL = "jdbc:mysql://localhost:3306/restaurant_reservation";
    private final static String USER = "root";
    private final static String PASS  = "";
    
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