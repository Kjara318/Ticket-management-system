/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author kostika.tome
 * @author edvin.perfundi
 * @author hysko
 */
public class database {
    
    public static Connection connectDb(){
     try{
            
            Class.forName("com.mysql.cj.jdbc.Driver");
                                //                      DATABASE LINK, USERNAME, PASSWORD ROOT IS THE DEFAULT USERNAME 
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/databaza", "root", ""); 
            return connect;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
}
