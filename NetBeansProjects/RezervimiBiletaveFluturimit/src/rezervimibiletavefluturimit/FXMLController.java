package rezervimibiletavefluturimit;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLController implements Initializable {

    @FXML
    private TextField email;

    @FXML
    private Button login;

    @FXML
    private PasswordField password;
   
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private double x = 0;
    private double y = 0;

    
    //Nepermjet metodes login() perdoruesi/perdoruesja mund te te beje log in ne aplikacion
     public void login() {

        String sql = "SELECT * FROM Perdoruesit WHERE email = ? and password = ?";

        connect = Databaza.connectDb();

        try {

            //Kontrollojme nese perdoruesi kerkon te hyje ne aplikacion pa vendosur
            //me pare email ose password
            if (email.getText().isEmpty() || password.getText().isEmpty()) {
                AirlineBookingSystems.shfaqAlert("Mesazh Gabimi", "Ju lutem plotesoni te gjitha te dhenat tuaja");
                

            } else {

                prepare = connect.prepareStatement(sql);
                prepare.setString(1, email.getText());
                prepare.setString(2, password.getText());

                result = prepare.executeQuery();

                if (result.next()) {

                   // getData.email = email.getText();

                    //Ne rastin kur te dhenat plotesohen sakte perdoruesit
                    //do t'i shfaqet mesazhi qe eshte loguar me sukses :) 
                    AirlineBookingSystems.shfaqAlertInfo("Mesazh informacioni", "Login u krye me sukses!");
                   
                    login.getScene().getWindow().hide();
 
                    Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    root.setOnMousePressed((MouseEvent event) -> {
                       x = event.getSceneX();
                       y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setScene(scene);
                    stage.show();

                } else {
                   //Ne rast se email ose password i perdoruesve qe tashme kane nje llogari
                   //ekzistuese eshte jo i sakte do te shafqet nje mesazh gabimi 
                   AirlineBookingSystems.shfaqAlert("Mesazh Gabimi", "Email/Password jo i sakte!");
                   

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     
     //Nepermjet metodes gotoSignUp() realizohet kalimi ne pjesen ku perdoruesi/perdoruesja
     //ben sign up ne aplikacion
    public void gotoSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));

            //Krijojme nje skene te re
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            //Vendosim mouse drag per skenen e re
            root.setOnMousePressed((MouseEvent mouseEvent) -> {
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent mouseEvent) -> {
                stage.setX(mouseEvent.getScreenX() - x);
                stage.setY(mouseEvent.getScreenY() - y);
            });

            //Shfaqja e skenes se re
            stage.show();

            //Mbyllim skenen aktuale (login form)
            ((Node) (event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    //Bejme daljen
    public void close(){
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
    }
}

