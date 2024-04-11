package rezervimibiletavefluturimit;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class RezervimiBiletaveFluturimit extends Application {
    
    private double x = 0;
    private double y = 0;

    @Override
    
    //Krijimi i nje skene te re
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXML.fxml"));
            
            
            Scene scene = new Scene(root);
            
              root.setOnMousePressed((MouseEvent event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
            
          root.setOnMouseDragged((MouseEvent event) -> {

            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);

            stage.setOpacity(.8);
        });
            root.setOnMouseReleased((MouseEvent event) -> {
            stage.setOpacity(1);
        });
            stage.initStyle(StageStyle.TRANSPARENT);
            
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void shfaqAlert(String titulli, String permbajtja) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(titulli);
    alert.setHeaderText(null);
    alert.setContentText(permbajtja);
    alert.showAndWait();
}
    
    public static void shfaqAlertInfo(String titulli, String permbajtja) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulli);
    alert.setHeaderText(null);
    alert.setContentText(permbajtja);
    alert.showAndWait();
    }
}

