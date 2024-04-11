package rezervimibiletavefluturimit;

import java.util.Random;
import java.sql.Connection;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController implements Initializable {

    @FXML
    private TableColumn<InfoFluturimet, Integer> CmimiEko;

    @FXML
    private TableColumn<InfoFluturimet, Integer> CmimiPare;

    @FXML
    private ComboBox<String> bagazheInput;

    @FXML
    private TextField bagazheLabel;

    @FXML
    private TextField biletaLabel;

    @FXML
    private Button butoniDashboard;

    @FXML
    private Button butoniFluturimet;

    @FXML
    private Button butoniRezervimi;

    @FXML
    private Button butoniKerko;

    @FXML
    private Button logout;

    @FXML
    private Label cmimiBiletesLabel;

    @FXML
    private DatePicker dataNisjes;

    @FXML
    private DatePicker dataMberritjes;

    @FXML
    private TextField dataLabel;

    @FXML
    private AnchorPane dritarjaKryesore;

    @FXML
    private TextField emriInput;

    @FXML
    private TextField emriLabel;

    @FXML
    private AnchorPane faqjaFluturimet;

    @FXML
    private AnchorPane formular1;

    @FXML
    private AnchorPane formularPjesa1;

    @FXML
    private AnchorPane formularPjesa2;

    @FXML
    private ComboBox<String> gjiniaInput;

    @FXML
    private TextField gjiniaLabel;

    @FXML
    private TableColumn<InfoFluturimet, Integer> idFluturimit;

    @FXML
    private TextField kerkoFluturimi;

    @FXML
    private TextField klasiLabel;

    @FXML
    private TextField mbiemriInput;

    @FXML
    private TextField mbiemriLabel;

    @FXML
    private TextField vendiNisjesLabel;

    @FXML
    private TextField vendiMberrijesLabel;

    @FXML
    private RadioButton vajtjeardhjeradio;

    @FXML
    private RadioButton njedrejtimradio;

    @FXML
    private TextField nrTelefonitInput;

    @FXML
    private TextField nrTelefonitLabel;

    @FXML
    private TableColumn<InfoFluturimet, Date> oraMberritjes;

    @FXML
    private TableColumn<InfoFluturimet, Date> oraNisjes;

    @FXML
    private TableColumn<InfoFluturimet, String> vendiMberritjes;

    @FXML
    private TableColumn<InfoFluturimet, String> vendiNisjes;

    @FXML
    private ComboBox<String> vendiNisjesField;

    @FXML
    private ComboBox<String> vendiMberritjesField;

    @FXML
    private TableView<InfoFluturimet> tabelaFluturimeve;

    @FXML
    private ComboBox<?> klasicombo;

    @FXML
    private ComboBox<?> pasagjerecombo;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private ToggleGroup toggleGroup;

    
    //Nepermjet metodes populloVendiNisjes() realizohet shfaqja e udhetimeve/fluturimeve qe ofrohen ne faqen e pare.
    //Perkatesisht ne combobox 'nga' jane te gjitha linjat ajrore qe ofrohen
    //midis vendeve.(Behet mbushja e combobox me te dhena)
    public void populloVendiNisjes() {
        String sql = "SELECT * FROM fluturimet WHERE ora_e_nisjes > NOW()";
        ObservableList<String> ngaList = FXCollections.observableArrayList();
        connect = Databaza.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                Integer idAeroportiNisjes = result.getInt("aeroporti_nisjes_id");
                String sql1 = "SELECT * FROM aeroportet WHERE aeroporti_id = ?";
                PreparedStatement prepare1 = connect.prepareStatement(sql1);
                prepare1.setInt(1, idAeroportiNisjes);
                ResultSet result1 = prepare1.executeQuery();
                String emriAeroportitNisje = null;

                if (result1.next()) {
                    emriAeroportitNisje = result1.getString("emri_aeroportit");
                }

                Integer idShteti = result1.getInt("shteti_id");
                String sql2 = "SELECT * FROM shtetet WHERE shteti_id = ?";
                PreparedStatement prepare2 = connect.prepareStatement(sql2);
                prepare2.setInt(1, idShteti);
                ResultSet result2 = prepare2.executeQuery();
                String emriShtetitNisje = null;

                if (result2.next()) {
                    emriShtetitNisje = result2.getString("emri_shtetit");
                }

                String vendiNisjes = emriAeroportitNisje + " (" + emriShtetitNisje + ")";

                if (!ngaList.contains(vendiNisjes)) {
                    ngaList.add(vendiNisjes);
                }

            }
            vendiNisjesField.setItems(ngaList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ObservableList<String> filtroVendetMberritjes(String vendiNisjes) {
        ObservableList<String> vendetMberritjesFilturar = FXCollections.observableArrayList();

        //Merret emri aeroportit nga vendiNisjes
        String[] pjeset = vendiNisjes.split("\\(");
        String emriAeroportitNisje = pjeset[0].trim();

        //SQL query per te marre aeroporti_nisjes_id bazuar ne emriAeroportitNisje qe eshte zgjedhur
        String sqlNisjesId = "SELECT aeroporti_id FROM aeroportet WHERE emri_aeroportit = ?";

        connect = Databaza.connectDb();

        try {
            //Marrim aeroporti_nisjes_id per emriAeroportitNisje qe kemi perzgjedhur
            prepare = connect.prepareStatement(sqlNisjesId);
            prepare.setString(1, emriAeroportitNisje);
            result = prepare.executeQuery();

            if (result.next()) {
                int aeroportiNisjesId = result.getInt("aeroporti_id");

                // SQL query per te marre destinacionet e filtruara ne varesi te aeroporti_nisjes_id
                String sql = "SELECT DISTINCT emri_aeroportit, shteti_id FROM aeroportet WHERE aeroporti_id IN (SELECT DISTINCT aeroporti_destinacionit_id FROM fluturimet WHERE aeroporti_nisjes_id = ?)";

                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, aeroportiNisjesId);
                result = prepare.executeQuery();

                while (result.next()) {
                    Integer id = result.getInt("shteti_id");
                    String emri_aeroportit = result.getString("emri_aeroportit");

                    String sql2 = "SELECT * FROM shtetet WHERE shteti_id = ?";
                    try (PreparedStatement prepare2 = connect.prepareStatement(sql2)) {
                        prepare2.setInt(1, id);
                        try (ResultSet result2 = prepare2.executeQuery()) {
                            while (result2.next()) {
                                String shteti = result2.getString("emri_shtetit");
                                String vendiMberritjes = emri_aeroportit + " (" + shteti + ")";
                                vendetMberritjesFilturar.add(vendiMberritjes);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vendetMberritjesFilturar;
    }

    //Nepermjet metodes populloVendiMberritjes() mbushet me vlera e vendeve te mberritjes ne baze te vendit te nisjes
    private void populloVendiMberritjes() {
        String vendiNisjes = vendiNisjesField.getSelectionModel().getSelectedItem();

        if (vendiNisjes != null) {
            ObservableList<String> VendiMberritjesVlefshem = filtroVendetMberritjes(vendiNisjes);
            vendiMberritjesField.setDisable(false);
            vendiMberritjesField.setItems(VendiMberritjesVlefshem);

        }
    }
    
    //Perdorim ObservableList(liste ne JavaFX) qe 'listeners' te kapin/ndjekin te 
    //gjitha ndryshimet qe ndodhin
    public ObservableList<InfoFluturimet> merrFluturimet() {
        ObservableList<InfoFluturimet> listaFluturimeve = FXCollections.observableArrayList();
        //perdoruesit do t'i shfaqen vetem ato fluturime qe kane nje ore nisje me te madhe se koha qe ka kaluar
        String sql = "SELECT * FROM fluturimet WHERE ora_e_nisjes > NOW()";
        connect = Databaza.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            InfoFluturimet fluturim;

            while (result.next()) {
                Integer idFluturimit = result.getInt("fluturimi_id");
                String nrFluturimit = result.getString("numri_fluturimit");
                Integer idAeroportiNisjes = result.getInt("aeroporti_nisjes_id");

                String sql1 = "SELECT * FROM aeroportet WHERE aeroporti_id = ?";
                PreparedStatement prepare1 = connect.prepareStatement(sql1);
                prepare1.setInt(1, idAeroportiNisjes);
                ResultSet result1 = prepare1.executeQuery();
                String emriAeroportitNisje = null;

                if (result1.next()) {
                    emriAeroportitNisje = result1.getString("emri_aeroportit");
                }

                Integer idShteti = result1.getInt("shteti_id");
                String sql2 = "SELECT * FROM shtetet WHERE shteti_id = ?";
                PreparedStatement prepare2 = connect.prepareStatement(sql2);
                prepare2.setInt(1, idShteti);
                ResultSet result2 = prepare2.executeQuery();
                String emriShtetitNisje = null;

                if (result2.next()) {
                    emriShtetitNisje = result2.getString("emri_shtetit");
                }

                String vendiNisjes = emriAeroportitNisje + " (" + emriShtetitNisje + ")";

                Integer idAeroportiKthimit = result.getInt("aeroporti_destinacionit_id");
                prepare1 = connect.prepareStatement(sql1);
                prepare1.setInt(1, idAeroportiKthimit);
                result1 = prepare1.executeQuery();
                String emriAeroportitMberritje = null;

                if (result1.next()) {
                    emriAeroportitMberritje = result1.getString("emri_aeroportit");
                }

                idShteti = result1.getInt("shteti_id");
                prepare2 = connect.prepareStatement(sql2);
                prepare2.setInt(1, idShteti);
                result2 = prepare2.executeQuery();
                String emriShtetitMberritje = null;

                if (result2.next()) {
                    emriShtetitMberritje = result2.getString("emri_shtetit");
                }

                String vendiMberritjes = emriAeroportitMberritje + " (" + emriShtetitMberritje + ")";

                SimpleDateFormat formatiDates = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String oraNisjesString = result.getString("ora_e_nisjes");
                String oraMberritjesString = result.getString("ora_e_mberritjes");
                Date oraNisjes = formatiDates.parse(oraNisjesString);
                Date oraMberritjes = formatiDates.parse(oraMberritjesString);
                formatiDates = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                String OraNisjes = formatiDates.format(oraNisjes);
                String OraMberritjes = formatiDates.format(oraMberritjes);

                Integer idAvionit = result.getInt("avioni_id");
                Integer cmimiEkonomik = result.getInt("cmime_ekonomik");
                Integer cmimeFirst = result.getInt("cmime_first");
                fluturim = new InfoFluturimet(idFluturimit, nrFluturimit, vendiNisjes, vendiMberritjes, OraNisjes, OraMberritjes, idAvionit, cmimiEkonomik, cmimeFirst);

                listaFluturimeve.add(fluturim);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaFluturimeve;
    }

    //Krijojme nje array listKlasi te tipit String qe do te mbaje klaset ne ofrojme
    //perkatesisht ate ekonomik dhe first(klasi i pare).
    private String[] listKlasi = {"Ekonomik", "First"};

    //Ne metoden klasi() krojohet nje arraylist qe ka te njejtat elemente si listKlasi.
    //Qellimi eshte qe ne combo box te klasit te shfaqen dy klaset dhe perdoruesi te
    //zgjedhe ndermjet tyre.
    public void klasi() {

        List<String> klasiList = new ArrayList<>();
        for (String data : listKlasi) {
            klasiList.add(data);
        }
        ObservableList listKlasi = FXCollections.observableArrayList(klasiList);
        klasicombo.setItems(listKlasi);
    }
    ;
    
    //Krijojme nje array neIPasagjereve qe mban nr e biletave qe nje perdorues deshiron te prese.
    //Per qellime ilustrimi eshte supozuar se ai mund te deshiroje max deri ne 3 bileta
    private String[] nrIPasagjereve = {"1", "2", "3"};

    //Nepermjet metodes pasagjeret() perdoruesit do t'i shfaqet nr i biletave qe ai deshiron
    //te prese
    public void pasagjeret() {

        List<String> pasagjeretList = new ArrayList<>();
        for (String data : nrIPasagjereve) {
            pasagjeretList.add(data);
        }
        ObservableList nrIPasagjereve = FXCollections.observableArrayList(pasagjeretList);
        pasagjerecombo.setItems(nrIPasagjereve);
    }
    ;
    
    
    //Krijojme nje array Gjinia te tipit String qe mban gjinite
      private String[] Gjinia = {"Mashkull", "Femer"};

    //Nepermjet metodes gjiniaList() perdoruesit do t'i shfaqet gjinia dhe ai/ajo do te zgjedhe
    public void gjiniaList() {

        List<String> listgjinia = new ArrayList<>();
        for (String data : Gjinia) {
            listgjinia.add(data);
        }

        ObservableList gjinia = FXCollections.observableArrayList(listgjinia);
        gjiniaInput.setItems(gjinia);
    }

    //Krijojme nje array Bagazhe te tipit String qe mban dy vlera po ose jo, pra nje pasagjer 
    //mund te kete bagazhe ose jo.
    private String[] Bagazhe = {"Po", "Jo"};

    //Nepermjet metodes bagazheList() perdoruesit do te zgjedhe nese do te udhetoje me apo pa bagazhe
    public void bagazheList() {

        List<String> listbagazhe = new ArrayList<>();
        for (String data1 : Bagazhe) {
            listbagazhe.add(data1);
        }

        ObservableList bagazhe = FXCollections.observableArrayList(listbagazhe);
        bagazheInput.setItems(bagazhe);
    }

    //Metoda selektoNjeRadioButton() ben te mundur klikimin e radiobutons dhe me pas percaktimin
    //se si do te jete gjendja e dates se kthimit ne varesi te kesaj zgjedhje
    public void selektoNjeRadioButton() {
        toggleGroup = new ToggleGroup();

        //Perdorim toggle group qe radio buttons te mund te klikohen vetem njeri prej
        //tyre ne nje moment te caktuar
        njedrejtimradio.setToggleGroup(toggleGroup);
        vajtjeardhjeradio.setToggleGroup(toggleGroup);

        dataMberritjes.setDisable(true); //Ne fillim data e kthimit nuk do te jete e klikueshme per perdoruesin

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == vajtjeardhjeradio) {
                dataMberritjes.setDisable(false); //Nese perdoruesi do te zgjedhe bilete vajtje-ardhje data e kthimit
            } else {                              //do te jete e klikueshme
                dataMberritjes.setDisable(true); //Nese perdoruesi do te zgjedhe bilete vetem vajtje atehere data e 
            }                                     //kthimit nuk do te jete e klikueshme per te
        });
    }

    //Nepermjet metodes numriBiletaveList() do te mbajme ne kontroll numrin e pasagjereve 
    //qe ne kemi duke patur parasysh kapacitetin  e avionit.
    //Gjithashtu do te behet edhe heqja e vendeve te lira nga ky avion ne momentin qe behet 
    //nje rezervim.
    public void numriBiletaveList() {
        List<String> listTicket = new ArrayList<>();
        for (int q = 1; q <= 150; q++) {          //vlera e q shkon deri ne 150 pra sa 
            listTicket.add(String.valueOf(q));    //kapaciteti qe ka avioni
        }

        String removeSeat = "SELECT vende_te_lira FROM fluturimet WHERE aeroporti_nisjes_id='"
                + vendiNisjesField.getSelectionModel().getSelectedItem() + "'";

        connect = Databaza.connectDb();

        try {
            prepare = connect.prepareStatement(removeSeat);
            result = prepare.executeQuery();

            while (result.next()) {
                listTicket.remove(result.getString("vende_te_lira"));
            }

        } catch (Exception e) {
            e.printStackTrace();    //na lejon te kuptojme se ku mund te kemi nje exception
        }
    }

    private double cmimiTotal = 0;
    private String cmimiTotQuery = "";
    private double cmimiKlas = 0;
    private String columnName = "";

    //Nepermjet funksionit kontrolloEmer() behet kontrolli nese perdoruesi ka vendosur
    //nje vlere te sakte ne textfield per emrin ose mbiemrin.
    private boolean kontrolloEmer(String emer) {

        return emer.matches("^[a-zA-Z]+(?:\\s[a-zA-Z]+)?$");
    }
    //Nepermjet funksionit kontrollonumri() behet kontrolli nese perdoruesi ka vendosur
    //nje vlere te sakte ne textfield per numrin e telefonit.

    private boolean kontrolloNumri(String numri) {

        return numri.matches("^\\d+$");
    }

    //Nepermjet metodes informacionPerdoruesit() do te merren te dhenat nga perdoruesi
    public void informacionPerdoruesit() {
        String emri = emriInput.getText();
        String mbiemri = mbiemriInput.getText();
        String gjinia = (String) gjiniaInput.getSelectionModel().getSelectedItem();
        String nrtel = nrTelefonitInput.getText();
        String bagazhe = (String) bagazheInput.getSelectionModel().getSelectedItem();
        String data = String.valueOf(dataNisjes.getValue());
        String vendiN = vendiNisjesField.getSelectionModel().getSelectedItem();
        String vendiM = vendiMberritjesField.getSelectionModel().getSelectedItem();
        String klasi = (String) klasicombo.getSelectionModel().getSelectedItem();
        String pasagjeret = (String) pasagjerecombo.getSelectionModel().getSelectedItem();
        Boolean vajtjeArdhje = vajtjeardhjeradio.isSelected();

        String vendiNisjesKod = vendiN.substring(0, Math.min(vendiN.length(), 3));
        String vendiMberritjesKod = vendiM.substring(0, Math.min(vendiM.length(), 3));

        //Bejme kontroll ne rast se perdoruesi harron ose le nje nga te dhenat e tij te 
        //paplotesuara.Ne nje situate te tille atij do t'i shfaqet nje mesazh gabimi ku 
        //do t'i kerkohet qe te plotesoje te dhenat e tij.
        if (vendiNisjesField.getSelectionModel().getSelectedItem().isEmpty()
                || vendiMberritjesField.getSelectionModel().getSelectedItem().isEmpty()
                || klasi.isEmpty() || pasagjeret.isEmpty()
                || emriInput.getText().isEmpty() || mbiemriInput.getText().isEmpty()
                || gjiniaInput.getSelectionModel().getSelectedItem().isEmpty()
                || nrTelefonitInput.getText().isEmpty()
                || bagazheInput.getSelectionModel().getSelectedItem().isEmpty()) {

            RezervimiBiletaveFluturimit.shfaqAlert("Mesazh Gabimi", "Ju lutem plotesoni te gjitha te dhenat tuaja!");

        } else if (!kontrolloNumri(nrtel) || !kontrolloEmer(emri) || !kontrolloEmer(mbiemri)) {
            RezervimiBiletaveFluturimit.shfaqAlert("Mesazh Gabimi", "Vendosni te dhenat e sakta");

        } else {
            //Bejme kontrollet qe te percaktojme cmimin ne varesi te zgjedhjeve qe do te 
            //beje perdoruesi/perdoruesja per flutimin e tij/saj.
            if (klasi == "Ekonomik") {
                cmimiTotQuery = "SELECT cmime_ekonomik FROM fluturimet AS f "
                        + "INNER JOIN aeroportet AS nisje_aeroport ON f.aeroporti_nisjes_id = nisje_aeroport.aeroporti_id "
                        + "INNER JOIN aeroportet AS destinacion_aeroport ON f.aeroporti_destinacionit_id = destinacion_aeroport.aeroporti_id "
                        + "WHERE nisje_aeroport.emri_aeroportit = '" + vendiNisjesKod + "' "
                        + "AND destinacion_aeroport.emri_aeroportit = '" + vendiMberritjesKod + "'";
                columnName = "cmime_ekonomik";
                if (bagazhe == "Po") {
                    cmimiTotal = cmimiTotal + 15;
                }  //Nese perdoruesi fluturon ne klasin ekonomik dhe me bagazhe cmimi i tij 
                //do te kete edhe nje tarife fikse te shtuar ne vleren 15.
            } else {
                cmimiTotQuery = "SELECT cmime_first FROM fluturimet AS f "
                        + "INNER JOIN aeroportet AS nisje_aeroport ON f.aeroporti_nisjes_id = nisje_aeroport.aeroporti_id "
                        + "INNER JOIN aeroportet AS destinacion_aeroport ON f.aeroporti_destinacionit_id = destinacion_aeroport.aeroporti_id "
                        + "WHERE nisje_aeroport.emri_aeroportit = '" + vendiNisjesKod + "' "
                        + "AND destinacion_aeroport.emri_aeroportit = '" + vendiMberritjesKod + "'";
                columnName = "cmime_first";
                if (bagazhe == "Po") {
                    cmimiTotal = cmimiTotal + 25;
                }  //Nese perdoruesi fluturon ne klasin e pare dhe me bagazhe cmimi i tij 
            }      //do te kete edhe nje tarife fikse ne vleren 25.

            //Ne rastet e tjera kur ai nuk ka bagazhe cmimi nuk do te kete tarife te shtuar
            //Gjithashtu ne rastet kur perdoruesi zgjedh qe te beje fluturim vajtje-ardhje
            //cmimi i biletes se tij do te jete i dyfishuar
            try {
                connect = Databaza.connectDb();
                prepare = connect.prepareStatement(cmimiTotQuery);
                result = prepare.executeQuery();

                if (result.next()) {
                    cmimiKlas = result.getDouble(columnName);
                    cmimiTotal = cmimiTotal + cmimiKlas;
                    if (vajtjeArdhje) {
                        cmimiTotal = cmimiTotal * 2;
                    }
                    cmimiTotal = cmimiTotal * Integer.parseInt(pasagjeret);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Qe perdoruesi te vendose/plotesoje te dhenat e tij
            cmimiBiletesLabel.setText(String.valueOf(cmimiTotal) + "€");
            emriLabel.setText(emri);
            mbiemriLabel.setText(mbiemri);
            gjiniaLabel.setText(gjinia);
            nrTelefonitLabel.setText(nrtel);
            bagazheLabel.setText(bagazhe);

            dataLabel.setText(data);
            vendiNisjesLabel.setText(vendiN);
            vendiMberrijesLabel.setText(vendiM);
            klasiLabel.setText(klasi);
            biletaLabel.setText(gjeneroNumerRastesishem());

            //Ne momentin qe perdoruesi.perdoruesja e ben te plote plotesimin e te dhenave te
            //tij/saj do te shfaqet mesazhi qe te dhenat u plotesuan me sukses
            RezervimiBiletaveFluturimit.shfaqAlertInfo("Mesazh informues", "Plotesimi u be me sukses!");

            pastroFushat();  //Pasi te dhenat e perdoruesit ruhen ai mund te beje 
            //beje nje plotesim te dyte nepermjet kesaj metode.
        }
    }

//Nepermjet funsionit generateRandomNumber() do te gjenerojme vlera random
    public static String gjeneroNumerRastesishem() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000); // Gjenerojme nje numer random ndermjet 0 dhe 999
        return String.format("#%03d", randomNumber); // Formatojme numrin qe te sigurohemi ensure qe permban 3 shifra
    }

    //Nepermjet metodes kontrollDate() bejme te mundur qe perdoruesi te mund te zgjedhe vetem
    //ato data qe jane ne vijim,pra jo ato data qe kane kaluar
    public void kontrollDaten() {
        final LocalDate today = LocalDate.now();

        //Vendosim nje event listener per vleren e zgjedhur ne fillim
        dataNisjes.valueProperty().addListener((observable, oldValue, newValue) -> {
            //Nese data e fillimit ndryshon
            if (newValue != null && !newValue.isBefore(today)) {
                dataMberritjes.setDisable(false); // data e kthimit nuk mund te zgjidhet nga perdoruesi

                final LocalDate newDate = newValue.plusDays(1);

                dataMberritjes.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isBefore(newDate)); //Datat qe jane para dates qe peroruesi zgjedh si date nisje
                    }                                       //do te jene te paaksesueshme(disabled)
                });
            } else {
                dataMberritjes.setDisable(true); //Data e kthimit behet e paaksesueshme nese data e nisjes
            }                             //eshte bosh ose me heret nga data e dites se sotme.
        });

        //Vendosim daten e nisjes ne daten e dites se sotme dhe datat para kesaj dite i 
        //bejme te paaksesueshme per perdoruesin
        dataNisjes.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(today)); //Bejme datat para dites se sotme te paaksesueshme
            }
        });
    }

    //Pasi perdoruesi ka plotesuar sakte te dhenat qe i jane kerkuar, ka zgjedhur fluturimin
    //sipas preferencave te tij dhe ka klikuar butonin paguaj atij/asaj do t'i shfaqet mesazhi 
    //informues qe pagesa eshte bere me sukses, duke konfirmuar ne nje fare menyre edhe rezervimin
    //e bere.
    public void pagesaSukses(ActionEvent event) {
        if (emriLabel.getText() == null
                || vendiMberrijesLabel.getText() == null
                || gjiniaLabel.getText() == null
                || nrTelefonitLabel.getText().isEmpty()
                || bagazheLabel.getText() == null
                || nrTelefonitLabel.getText() == null
                || mbiemriLabel.getText() == null) {
            RezervimiBiletaveFluturimit.shfaqAlert("Mesazh gabimi", "Ploteso te dhenat!");
        } else {
            RezervimiBiletaveFluturimit.shfaqAlertInfo("Mesazh informimi", "Pagesa u krye me sukses!");
        }
    }

    //Nepermjet metodes butoniPastro() ne rast se perdoruesi deshiron te ndryshoje te dhenat e 
    //tij behet fshirja e te gjitha te dhenave qe ai ka vendosur( ne rast se ai nuk ka klikuar
    //vazhdo) ose fshihet te dhenat qe te behet i mundur riplotesimi i tyre
    //nese perdoruesi deshiron te beje nje rezervim tjeter
    public void pastroFushat() {
        emriInput.setText("");
        mbiemriInput.setText("");
        gjiniaInput.getSelectionModel().clearSelection();
        nrTelefonitInput.setText("");
        bagazheInput.getSelectionModel().clearSelection();
    }
    
     private ObservableList<InfoFluturimet> fluturimet;

    //Nepermjet metodes shfaqFluturimet() behet shtimi i informacineve qe lidhen me fluturimet
    //qe ne ofrojme ne 'tabelaFluturimeve'.
    public void shfaqFluturimet() {

        fluturimet = merrFluturimet();
        idFluturimit.setCellValueFactory(new PropertyValueFactory<>("fluturimiId"));
        vendiNisjes.setCellValueFactory(new PropertyValueFactory<>("vendiNisjes"));
        vendiMberritjes.setCellValueFactory(new PropertyValueFactory<>("vendiMberritjes"));
        oraNisjes.setCellValueFactory(new PropertyValueFactory<>("oraENisjes"));
        oraMberritjes.setCellValueFactory(new PropertyValueFactory<>("oraEMberritjes"));
        CmimiEko.setCellValueFactory(new PropertyValueFactory<>("cmimeEkonomik"));
        CmimiPare.setCellValueFactory(new PropertyValueFactory<>("cmimeFirst"));

        tabelaFluturimeve.setItems(fluturimet);
    }

    //Nepermjet metodes kerkoFluturime() do te kontrollohet nese 'vlera' e kerkuar nga 
    //perdoruesi/perdoruesja gjendet ne ate cfare aplikacioni(kompania) ofron.
    public void kerkoFluturime() {
        kerkoFluturimi.textProperty().addListener((observable, vleraPara, vleraRe) -> {
            Platform.runLater(() -> {
                FilteredList<InfoFluturimet> listaFiltruar = new FilteredList<>(fluturimet, e -> true);

                listaFiltruar.setPredicate(vleraPritur -> {
                    if (vleraRe.isEmpty() || vleraRe == null) {
                        return true;
                    }
                    String vleraKerkuar = vleraRe.toLowerCase();

                    if (vleraPritur.getFluturimiId().toString().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getVendiNisjes().toLowerCase().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getVendiMberritjes().toLowerCase().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getOraENisjes().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getOraEMberritjes().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getCmimeEkonomik().toString().contains(vleraKerkuar)) {
                        return true;
                    } else if (vleraPritur.getCmimeFirst().toString().contains(vleraKerkuar)) {
                        return true;
                    } else {
                        return false;
                    }
                });

                SortedList<InfoFluturimet> listaRenditur = new SortedList<>(listaFiltruar);

                listaRenditur.comparatorProperty().bind(tabelaFluturimeve.comparatorProperty());
                tabelaFluturimeve.setItems(listaRenditur);
            });
        });
    }

    private double x = 0;
    private double y = 0;

    //Nepermjet metodes butoniKurHapet() realizojme shfaqjen e butonave dashboard,rezervimi
    //dhe fluturimet qe jane ne faqen kryesore. Behet edhe vendosja e ngjyrave qe do te perdoren
    //per keto butona.
    public void butoniKurHapet() {
        butoniDashboard.setStyle("-fx-background-color:linear-gradient(to bottom right,#841b1b, #808080)");
        butoniRezervimi.setStyle("-fx-background-color:transparent");
        butoniFluturimet.setStyle("-fx-background-color:transparent");
    }

    //Nepermjet metodes nderroFaqen() behet nderrimi i faqeve ne varesi te butonave qe 
    //perdoruesi do te klikoje.
    public void nderroFaqen(ActionEvent event) {

        //Nese perdoruesi klikon butonin dashboard atij do t'i shfaqet pjesa e pare,
        //ku behet edhe pershendetja e perdoruesit dhe kerkohen disa nga te dhenat per
        //fluturimin qe perdoruesi do te beje
        if (event.getSource() == butoniDashboard) {
            formular1.setVisible(true);
            formularPjesa1.setVisible(false);
            formularPjesa2.setVisible(false);
            faqjaFluturimet.setVisible(false);

            butoniDashboard.setStyle("-fx-background-color:linear-gradient(to bottom right,#841b1b, #808080)");
            butoniRezervimi.setStyle("-fx-background-color:transparent");
            butoniFluturimet.setStyle("-fx-background-color:transparent");

            //Nese perdoruesi klikon butonin kerko do te behet lidhja e faqes se pare me ate 
            //te dyten.Ne kete faqe perdoruesit i kerkohet qe te plotesoje te dhenat e tij 
            //personale dhe me pas i shfaqen te gjitha te dhenat e plotesuara ne pjesen  e 
            //rezultatit.
        } else if (event.getSource() == butoniKerko) {
            formularPjesa1.setVisible(true);
            formularPjesa2.setVisible(true);
            formular1.setVisible(false);
            formular1.setVisible(false);
            faqjaFluturimet.setVisible(false);

            butoniDashboard.setStyle("-fx-background-color:transparent");
            butoniRezervimi.setStyle("-fx-background-color:linear-gradient(to bottom right,#841b1b, #808080)");
            butoniFluturimet.setStyle("-fx-background-color:transparent");

            gjiniaList();
            bagazheList();
            klasi();

            //Nese perdoruesi klikon butonin fluturimet atij do t'i shfaqet faqja e trete.
            //Ne kete pjese atij i shfaqen ne nje tabele te gjitha fluturimet qe kompania ka
            //vendin e nisjes dhe mberritjes,oret e nisjes dhe mberritjes si dhe cmimet sipas 
            //klasit.
        } else if (event.getSource() == butoniFluturimet) {
            faqjaFluturimet.setVisible(true);
            formular1.setVisible(false);
            formular1.setVisible(false);
            formularPjesa1.setVisible(false);
            formularPjesa2.setVisible(false);

            butoniDashboard.setStyle("-fx-background-color:transparent");
            butoniRezervimi.setStyle("-fx-background-color:transparent");
            butoniFluturimet.setStyle("-fx-background-color:linear-gradient(to bottom right,#841b1b, #808080)");

            shfaqFluturimet();
            kerkoFluturime();

        }
    }

    //Nepermjet metodes dil() behet edhe log out i perdoruesit
    //Ne fiillim kontrollojme nese perdoruesi e ka klikuar qellimisht 'log out'
    //nepermjet nje mesazhi konfirmues ku ai/ajo pyetet nese realisht deshiron qe 
    //te beje log out.Nese pergjigja eshte "ok" atehere behet edhe dalja perndryshe
    //perdoruesi do te vazhdoje te mbetet ne aplikacion
    public void dil() {
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mesazh konfirmimi");
            alert.setHeaderText(null);
            alert.setContentText("Jeni te sigurt qe doni te dilni?");

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                logout.getScene().getWindow().hide();

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

                    stage.setOpacity(.8);
                });
                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Nepermjet metodes mbyllFaqen() do te behet edhe mbyllja e faqeve ne ikonen e X ne cepin e djathte 
    //e cepin e djathte lart te faqes 
    public void mbyllFaqen() {
        System.exit(0);
    }

    //Nepermjet metodes minimizoDritaren() do te behet edhe minimizimi i faqes
    //duke klikuar ne ikonen e -
    public void minimizoDritaren() {
        Stage stage = (Stage) dritarjaKryesore.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        butoniKurHapet();
        shfaqFluturimet();
        gjiniaList();
        bagazheList();
        klasi();
        pasagjeret();
        selektoNjeRadioButton();
        populloVendiNisjes();
        kontrollDaten();
        vendiNisjesField.setOnAction(event -> populloVendiMberritjes());

    }

}
