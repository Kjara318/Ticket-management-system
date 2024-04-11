package rezervimibiletavefluturimit;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.ResultSet;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class SignUpController implements Initializable {

    @FXML
    private TextField email;

    @FXML
    private PasswordField fjaleKalimi1;

    @FXML
    private PasswordField fjaleKalimi2;

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button signUp;

    private Connection connect;
    private PreparedStatement prepare;

    private double x = 0;
    private double y = 0;
    
    //Nepermjet metodes signUp() do te behet plotesimi i te dhenave te perdoruesit
    public void signUp() {
        String sql = "INSERT INTO Perdoruesit (email, password) VALUES (?, ?)";

        connect = Databaza.connectDb();

        try {
            
            //Nese perdoruesi harron ose le nje nga te dhenat qe i kerkohen te paplotesuara 
            //atij/asaj do t'i shfaqet nje mesazh gabimi.
            if (email.getText().isEmpty() || fjaleKalimi1.getText().isEmpty() || fjaleKalimi2.getText().isEmpty()) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.", "Ju lutem plotesoni te gjitha fushat!");
                
                
                //Nese perdoruesi vendos dy password qe nuk perkojne me njeri-tjetrin 
                //atij/asaj do t'i shfaqet nje mesazh gabimi.
            } else if (!fjaleKalimi1.getText().equals(fjaleKalimi2.getText())) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.", "Fjalekalimet nuk perkojne!");
                
                
                //Kontrollojme nese perdoruesi ka pranuar te gjitha kushtet  
                //ne menyre qe te vazhdohet me tutje
            } else if (!checkBox.isSelected()) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Ju lutem pranoni kushtet!");
                
                //KOntrollohet nese email i perdoruesit eshte vendosur ne formatin e duhur
            } else if (!emailVlefshem(email.getText())) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Emaili duhet te kete kete format shembull@gmail.com. Ju lutem vendosni nje email te vlefshem! ");
                
                
                //Kontrollohet nese perdoruesi vendos password ne formatin e duhur
            } else if (!fjaleKalimVlefshem(fjaleKalimi1.getText())) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Fjalekalimi duhet te permbaje te pakten 8 karaktere dhe duhet te permbaje numra dhe shkronja!");
                
            } else {
                //Nese perdoruesi deshiron te hape nje llogari te re por me nje email qe tashme
                //ka nje llogari ekzistuese atij/asaj do t'i shfaqet nje mesazh gabimi
                if (emailiEkziston(email.getText())) {
                     AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Ekziston nje llogari me kete email. Ju lutem zgjidhni nje email tjeter.");
                   
                    return;
                } else if (!dergoDheKonfirmoKodin(email.getText())) {
                    return;
                }

                prepare = connect.prepareStatement(sql);
                prepare.setString(1, email.getText());
                prepare.setString(2, fjaleKalimi1.getText());

                int rezultati = prepare.executeUpdate();

                
                //Nese perdoruesi i ploteson sakte te gjitha te dhenat sipas formateve
                //perkatese atehere do te behet regjistrimi i tij me sukses ne aplikacion.
                if (rezultati > 0) {
                    AirlineBookingSystems.shfaqAlertInfo("Mesazh Informimi", "Regjistrimi u be me sukses!");
                   

                    signUp.getScene().getWindow().hide();

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
                    //Ne rastin e kundert ai/ajo do te informohet qe regjistrimi nuk u be.
                    AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Nuk mund te regjistroheni.Ju lutem provoni perseri!");
                   
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void shkoTeLogin() throws IOException{
    signUp.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("FXML.fxml"));

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
    };

    //Nepermjet funksionit isEmailEkzisues() do te kontrollohet nese email i vendosur 
    //eshte ekzistues
    public boolean emailiEkziston(String email) throws Exception {
        String queriKontrolloEmail = "SELECT COUNT(*) FROM Perdoruesit WHERE email = ?";
        try (PreparedStatement kontrolloEmailinStatement = connect.prepareStatement(queriKontrolloEmail)) {
            kontrolloEmailinStatement.setString(1, email);

            try (ResultSet rezultatet = kontrolloEmailinStatement.executeQuery()) {
                rezultatet.next();

                return rezultatet.getInt(1) > 0;
            }
        }
    }

    //Nepermjet funksionit fjaleKalimVlefshem() do te kontrollojme nese password
    //i vendosur nga perdoruesi i permbush kriteret e meposhtme.
    public boolean fjaleKalimVlefshem(String fjalekalimi) {
        return fjalekalimi.length() >= 8 && fjalekalimi.matches(".*[a-zA-Z].*") && fjalekalimi.matches(".*\\d.*");
    }

    //Nepermjet funksionit emailVlefshem() kontrollojme nese email i vendosur
    //eshte ne formatin e sakte.
    public boolean emailVlefshem(String email) {

        String emailRegex = "^[a-zA-Z0-9_]+@(email|gmail|hotmail|yahoo|outlook)\\.com$";
        return email.matches(emailRegex);
    }

    //Nepermjet funksionit gjeneroKodin() bejme te mundur gjenerimin e kodeve random te cilat me 
    //pas do t'i dergohen perdoruesit qe ai/ajo te regjistroje llogarine
    public static String gjeneroKodin() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        String kodi = "";

        for (int i = 0; i < 8; i++) {
            int nrRastesishem = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(nrRastesishem);
            kodi += randomChar;
        }
        return kodi;
    }

    
    //Nepermjet funksionit dergoDheKonfirmoKodin() behet gjenerimi dhe konfirmimi i kodit qe do t'i
    //dergohet perdoruesit ne email
    public boolean dergoDheKonfirmoKodin(String emaili) {

        String generatedCode = gjeneroKodin();
        String koka = "Kodi i konfigurimit";
        String trupi = "Kodi i konfigurimit eshte: <b>" + generatedCode + "</b>";
        SherbimiEmail.dergoEmail(emaili, koka, trupi);

        long kohaDergimit = System.currentTimeMillis();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Konfiguro email-in");
        dialog.setHeaderText(null);
        dialog.setContentText("Vendos kodin e derguar ne email:");

        Optional<String> rezultati = dialog.showAndWait();

        if (rezultati.isPresent() && rezultati.get().equals(generatedCode)) {

            //Nese peroruesi nuk e vendos kodin e derguar brenda kohes se caktuar
            //atehere ky kod do te jete i pavlefshem.
            if (System.currentTimeMillis() - kohaDergimit > 120 * 1000) {
                AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Kodi ka skaduar. Provoni perseri.");
                
                return false;
            }

            return true;
        } else {
            
            //Ne rast se perdoruesi harron te vendose kodin e derguar ose e vendos ate gabim
            AirlineBookingSystems.shfaqAlert("Mesazh gabimi.","Kodi i vendosur eshte gabim ose nuk eshte vendosur.");
        
            return false;
        }
    }

   
    //Bejme daljen
    public void mbyllFaqen() {
        System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}