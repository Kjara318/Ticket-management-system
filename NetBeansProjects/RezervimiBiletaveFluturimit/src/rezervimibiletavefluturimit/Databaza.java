package rezervimibiletavefluturimit;
import java.sql.Connection;
import java.sql.DriverManager;

public class Databaza {
    
    //Behet lidhja e databazes me aplikacionin
    public static Connection connectDb(){
     try{
            
            Class.forName("com.mysql.cj.jdbc.Driver");
                                //Linku i databazes,emri i perdoruesit,password root eshte username default  
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/databaza", "root", ""); 
            return connect;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
}
