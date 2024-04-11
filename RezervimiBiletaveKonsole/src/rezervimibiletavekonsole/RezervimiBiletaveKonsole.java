package rezervimibiletavekonsole;
/**
 *
 * @author hysko
 */
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class RezervimiBiletaveKonsole {

// Metoda për lidhjen e databazes
    public static Connection connectDb() throws SQLException {
        try {
             // Behet ngarkimi i driver te  MySQL JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Krijo lidhjen me bazën e të dhënave
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/Databaza", "root", "");
            return connect;
        } catch (ClassNotFoundException e) {
            // Nese nuk gjendet driveri MySql
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }

// Metoda për marrjen e inputit nga përdoruesi
    private static String merrInputinUserit(String prompt, boolean eshtePsw) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return eshtePsw ? scanner.nextLine() : scanner.nextLine();
    }

// Metoda për zgjedhjen e aeroportit nga përdoruesi
    public static int zgjidhAeroport(String prompt) {
        Scanner scanner = new Scanner(System.in);

        // Shfaq aeroportet në dispozicion
        shfaqAeroportet();

        // Përdoruesi zgjedh një aeroport duke përdorur ID
        System.out.print(prompt);
        return scanner.nextInt();
    }

// Metoda për shfaqjen e listës së aeroporteve
    public static void shfaqAeroportet() {
        try {
            Connection connection = connectDb();

            String sql = "SELECT * FROM aeroportet";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                System.out.println("\nAeroportet qe ne ofrojme sherbimin:");
                System.out.println("ID\tEmri");

                while (resultSet.next()) {
                    // Merr vlerat e rreshtit nga rezultati i query
                    int airportId = resultSet.getInt("aeroporti_id");
                    String airportName = resultSet.getString("emri_aeroportit");
                    // Shfaq ID-në dhe emrin e aeroportit
                    System.out.println(airportId + "\t" + airportName);
                }
            }
        } catch (SQLException e) {
            // Shfaq gabimet në rast se ndodhin
            e.printStackTrace();
        }
    }

// Metoda për shfaqjen e datave të fluturimeve
    private static void shfaqDatatFluturime(int aeroprtinisjes, int aerprotidestinacionit) {
        try {
            Connection connection = connectDb();
            // SQL query për të marrë datat unike të fluturimeve për vendin qe perdouresi zgjedh
            String sql = "SELECT DISTINCT DATE(ora_e_nisjes) AS ora_e_nisjes FROM fluturimet WHERE aeroporti_nisjes_id = ? AND aeroporti_destinacionit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, aeroprtinisjes);
                statement.setInt(2, aerprotidestinacionit);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Shfaq datat  e fluturimev
                    while (resultSet.next()) {
                        Date data_e_nisjes = resultSet.getDate("ora_e_nisjes");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        System.out.println(dateFormat.format(data_e_nisjes));
                    }
                }
            }
        } catch (SQLException e) {
            // Shfaq gabimet në rast se ndodhin
            e.printStackTrace();
        }
    }

// Metoda për krahasimin e datave
    private static boolean krahasoData(String data_e_nisjes, String data_e_kthimit) {
        try {
            // Kthe datat e stringut në objekte java.sql.Date për krahasim
            java.sql.Date datanisjesObj = java.sql.Date.valueOf(data_e_nisjes);
            java.sql.Date dataKthimitObj = java.sql.Date.valueOf(data_e_kthimit);
            // Krahaso datat pa marrë parasysh kohën
            return dataKthimitObj.after(datanisjesObj);
        } catch (IllegalArgumentException e) {
            // Kthe false në rast se ka ndonjë gabim gjatë konvertimit të datave
            return false;
        }
    }

// Metoda për verifikimin e emailit në bazë të databazes
    private static boolean egzEmaili(String email) {
        try {
            Connection connection = connectDb();

            // SQL query për të verifikuar ekzistencën e emailit në bazë te databazes
            String sql = "SELECT * FROM perdoruesit WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);

                ResultSet resultSet = statement.executeQuery();

                return resultSet.next(); // Kthen true nëse ekziston të paktën një rresht me emailin e specifikuar
            }
        } catch (SQLException e) {
            // Shfaq gabimet në rast se ndodhin
            e.printStackTrace();
            return false;
        }
    }

// Metoda për verifikimin e login-it në bazë të databazës
    private static boolean LoginOK(String email, String password) {
        try {
            Connection connection = connectDb();

            // SQL query për të verifikuar ekzistencën e kombinimit të emailit dhe fjalëkalimit nëpermjet databazes
            String sql = "SELECT * FROM perdoruesit WHERE email = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();

                return resultSet.next(); 
            }
        } catch (SQLException e) {
            // Shfaq gabimet në rast se ndodhin
            e.printStackTrace();
            return false;
        }
    }

// Metoda për shtimin e të dhënave në databazë
    private static void shtoDatabaze(String email, String password) throws SQLException {
        try (Connection connection = connectDb()) {
            // SQL query për shtimin e një perdoruesi në databazë 
            String insertUserSQL = "INSERT INTO perdoruesit (email, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();
            }
        }
    }

// Metoda për verifikimin nesë dy ID jane të barabarta
    private static boolean IDBaraz(int aeroprtinisjes, int aerprotidestinacionit) {
        return aeroprtinisjes == aerprotidestinacionit;
    }

// Metoda për verifikimin nese egziston fluturimi
    private static boolean egzFluturim(int aeroprtinisjes, int aerprotidestinacionit) {
        try {
            Connection connection = connectDb();

            // SQL query për të verifikuar ekzistencën e një fluturimi 
            String sql = "SELECT * FROM fluturimet WHERE aeroporti_nisjes_id = ? AND aeroporti_destinacionit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, aeroprtinisjes);
                statement.setInt(2, aerprotidestinacionit);

                ResultSet resultSet = statement.executeQuery();

                return resultSet.next(); 
            }
        } catch (SQLException e) {
            // Shfaq gabimet në rast se ndodhin
            e.printStackTrace();
            return false;
        }
    }

// Metoda për verifikimin e vlefshmërisë së datës për një fluturim
    private static boolean eshtDataOK(int aeroprtinisjes, int aerprotidestinacionit, String data_E_zgjedhur) {
        try {
            Connection connection = connectDb();

            // SQL query për marrjen e datave unike të fluturimeve për destinacionin e deshiruar
            String sql = "SELECT DISTINCT DATE(ora_e_nisjes) AS ora_e_nisjes FROM fluturimet WHERE aeroporti_nisjes_id = ? AND aeroporti_destinacionit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, aeroprtinisjes);
                statement.setInt(2, aerprotidestinacionit);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Kthehet true nëse datat e zgjedhura për fluturim janë ok
                    while (resultSet.next()) {
                        String data_aktuale = resultSet.getString("ora_e_nisjes");
                        // Kthe datën e stringut në objekt java.sql.Date për krahasim
                        java.sql.Date data_aktualeStr = java.sql.Date.valueOf(data_aktuale);
                        java.sql.Date data_E_zgjedhurObj = java.sql.Date.valueOf(data_E_zgjedhur);
                        // Krahaso datat pa marrë parasysh kohën
                        if (data_aktualeStr.equals(data_E_zgjedhurObj)) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // Shfaq gabimet në rast se ndodhin
            ex.printStackTrace();
        }
        return false;
    }

// Metoda për llogaritjen e çmimit të biletës
    private static double llogaritCmimBilete(int nrpasagjereve, int aeroprtinisjes, int aerprotidestinacionit, boolean kaValixhe, boolean eshteEkonomik, boolean eshteVjatje) {
        try {
            Connection connection = connectDb();

            // Marrja e informacionit të çmimit nga databaza
            String sql = "SELECT * FROM fluturimet WHERE aeroporti_nisjes_id = ? AND aeroporti_destinacionit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, aeroprtinisjes);
                statement.setInt(2, aerprotidestinacionit);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    double cmimi_ekonomike = resultSet.getDouble("cmime_ekonomik");
                    double cmimi_frist1 = resultSet.getDouble("cmime_first");

                    double cmim_baze = eshteEkonomik ? cmimi_ekonomike : cmimi_frist1;

                    double kavalixhe = kaValixhe ? 15.0 : 25.0; // Shto tarifën e bagazhit nëse përdoruesi ka bagazh

                    // Llogarit çmimin total
                    double cmimi_total;

                    if (eshteVjatje) {
                        cmimi_total = (cmim_baze + kavalixhe) * nrpasagjereve;
                    } else {
                        // Nëse eshte vajtje ardhje shumezojme me dy 
                        cmimi_total = ((cmim_baze + kavalixhe) * nrpasagjereve) * 2;
                    }

                    return cmimi_total;
                }
            }
        } catch (SQLException e) {
        
            e.printStackTrace();
        }

        return 0.0; // Kthe 0 nëse ka një gabim
    }

// Metoda për marrjen e datës së zgjedhur nga përdoruesi
    private static String dataZgjedhur(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }

// Metoda për rezervimin e një fluturimi
    private static void rezervo(int aeroprtinisjes, int aerprotidestinacionit, String data_E_zgjedhur, int nrpasagjereve, boolean eshteVjatje, String data_e_kthimit) {
        // Implementimi i logjikës së rezervimit 


        System.out.println("Fluturimi juaj u rezervua me suksese!");
        System.out.println("Id e aeroportit te nisjes : " + aeroprtinisjes);
        System.out.println("Id e aeroprtit te mberritjes: " + aerprotidestinacionit);
        System.out.println(" Data e nisjes: " + data_E_zgjedhur);
        System.out.println("Numri i pasagjerve: " + nrpasagjereve);

        if (!eshteVjatje) {
            System.out.println("Data e kthimi: " + data_e_kthimit);
        }
    }

    // Metoda kryesore për menaxhimin e hyrjes së përdoruesit
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int zgjedhja = 0;


        do {
            System.out.println("Miresevini ne Albawings");
            System.out.println("Zgjidhni nje nga opsionet per te hyre:");
            System.out.println("1. Identifikohu me nje llogari egzistuese");
            System.out.println("2. Regjistrohu dhe krijoni nje llogari te re");
            System.out.print("Beni zgjedhjen tuaj sipas numrave (1 ose 2): ");

            try {
                zgjedhja = scanner.nextInt();
                if (zgjedhja != 1 && zgjedhja != 2) {
                    System.out.println("Ju lutem zgjidhni opsionin 1 ose 2");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Ju lutem zgjidhni opsionin me ane te numrave 1 ose 2.");
            } finally {
                scanner.nextLine();
            }
        } while (zgjedhja != 1 && zgjedhja != 2);

        boolean eshteLoguar = false;

        while (!eshteLoguar) {
            // Procesi i loginit
            System.out.print("Vendosni emailin tuaj: ");
            String email = scanner.nextLine();
            System.out.print("Vendosni fjalkalimin: ");
            String password = merrInputinUserit("", true);

            eshteLoguar = LoginOK(email, password);

            // Kontrolloni nëse email-i ekziston
            if (egzEmaili(email)) {
                // Përdoruesi identifikohet me sukses
                if (LoginOK(email, password)) {
                    System.out.println("Ju u loguat me sukses");

                    // Shfaq aeroportet dhe zgjidh aeroportin e nisjes dhe destinacionit
                    int aeroprtinisjes = zgjidhAeroport("Zgjidh ID e aeroportit te nisjes (vendos ID): ");
                    int aerprotidestinacionit = zgjidhAeroport("Zgjidh ID e aeroportit destinacion (vendos ID): ");

                    // Kontrollo nese fluturimi ekziston
                    if (IDBaraz(aeroprtinisjes, aerprotidestinacionit)) {
                        System.out.println("Id e aeroporteve nuk mund te jene te barabarta");
                        // Kërko përdoruesin të vendosë përsëri destinacionin
                        aerprotidestinacionit = zgjidhAeroport("Zgjidh ID e aeroportit destinacion (vendos ID): ");
                    }

                    if (egzFluturim(aeroprtinisjes, aerprotidestinacionit)) {
                        // Shfaq datat e disponueshme të fluturimeve
                        shfaqDatatFluturime(aeroprtinisjes, aerprotidestinacionit);

                        // Zgjidh datën
                        System.out.print("Vendosni daten qe e deshironi fluturimin (YYYY-MM-DD): ");
                        String data_E_zgjedhur = scanner.nextLine();

                        // Validoni datën e zgjedhur kundër datave të disponueshme
                        if (eshtDataOK(aeroprtinisjes, aerprotidestinacionit, data_E_zgjedhur)) {
                         

                            // Marrja e hyrjeve së përdoruesit për detajet e biletës
                            boolean eshteEkonomik = false;
                            boolean isValidEshteEkonomik = false;
                            do {
                                System.out.print("Udhetimi juaj eshte i klasit ekonomike: Pergjigjuni (true ose false): ");
                                try {
                                    eshteEkonomik = scanner.nextBoolean();
                                    scanner.nextLine(); // Consume the newline character
                                    isValidEshteEkonomik = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Ju lutem vendosni true ose false.");
                                    scanner.nextLine(); // Clear invalid input
                                }
                            } while (!isValidEshteEkonomik);

                            boolean kaValixhe = false;
                            boolean isValidKaValixhe = false;
                            do {
                                System.out.print("A keni valixhe? Pergjigjuni (true ose false): ");
                                try {
                                    kaValixhe = scanner.nextBoolean();
                                    scanner.nextLine();
                                    isValidKaValixhe = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Ju lutem vendosni true ose false.");
                                    scanner.nextLine();
                                }
                            } while (!isValidKaValixhe);

                            int nrpasagjereve = 1;
                            boolean validInt;  
                            //Kerkohen nga perdoruesi te dhenat per rezervimin
                            do {
                                System.out.print("Vendosni numrin e pasagjereve: ");
                                try {
                                    nrpasagjereve = scanner.nextInt();
                                    scanner.nextLine(); 
                                    validInt = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Ju lutem vendosni numer te plote.");
                                    scanner.nextLine();
                                    validInt = false;
                                }
                            } while (!validInt);

                            String emri;
                            do {
                                System.out.print("Vendosni emrin tuaj: ");
                                emri = scanner.nextLine();
                            } while (!emri.matches("[a-zA-Z\\s]+"));

                            String mbiemri;
                            do {
                                System.out.print("Vendosni mbiemrin tuaj: ");
                                mbiemri = scanner.nextLine();
                            } while (!mbiemri.matches("[a-zA-Z\\s]+"));

                            String gjini;
                            do {
                                System.out.print("Vendosni gjinin tuaj (Mashkull/Femer): ");
                                gjini = scanner.nextLine();
                            } while (!gjini.equalsIgnoreCase("Mashkull") && !gjini.equalsIgnoreCase("Femer"));

                            String nrTel;
                            do {
                                System.out.print("Vendosni numrin e telefonit: ");
                                nrTel = scanner.nextLine();
                            } while (!nrTel.matches("\\d+") || nrTel.length() < 8 || nrTel.length() > 15);

                            boolean eshteVjatje = false;
                            boolean isValideshteVajtje = false;
                            do {
                                System.out.print("Udhetimi eshte vetem vajtje? Pergjigjuni (true ose false): ");
                                try {
                                    eshteVjatje = scanner.nextBoolean();
                                    scanner.nextLine(); 
                                    isValideshteVajtje = true;
                                } catch (InputMismatchException e) {
                                    System.out.println("Ju lutem vendosni true ose false.");
                                    scanner.nextLine(); 
                                }
                            } while (!isValideshteVajtje); 

                            // Llogarit dhe shfaq çmimin e biletës
                            double cmimiBiletes = llogaritCmimBilete(nrpasagjereve, aeroprtinisjes, aerprotidestinacionit, kaValixhe, eshteEkonomik, eshteVjatje);
                            System.out.println("Çmimi total i biletes: $" + cmimiBiletes);

                            String data_e_kthimit = null;
                            if (!eshteVjatje) {
                                // Nëse  është një udhëtim vajtje-ardhje, shfaq datat e disponueshme për kthim
                                shfaqDatatFluturime(aerprotidestinacionit, aeroprtinisjes);

                                // Zgjidh datën e kthimit
                                boolean validdata_e_kthimit = false;
                                do {
                                    data_e_kthimit = dataZgjedhur("Vendosni daten e kthimit qe deshironi (YYYY-MM-DD): ");
                                    validdata_e_kthimit = krahasoData(data_E_zgjedhur, data_e_kthimit);

                                    if (!validdata_e_kthimit) {
                                        System.out.println("Data e kthimit duhet te jete pas dates se nisjes");
                                    }
                                } while (!validdata_e_kthimit);
                            }

                            // Bën rezervimin e udhëtimit
                            rezervo(aeroprtinisjes, aerprotidestinacionit, data_E_zgjedhur, nrpasagjereve, eshteEkonomik, data_e_kthimit);

                            // Shfaq informacionin e rezervimit
                            System.out.println("\nInformacioni i rezervimit:");
                            System.out.println("Emri: " + emri + " " + mbiemri);
                            System.out.println("Gjini: " + gjini);
                            System.out.println("Numri i telefonit: " + nrTel);
                            System.out.println("Data e nisjes: " + data_E_zgjedhur);
                            if (!eshteVjatje) {
                                System.out.println("Data e kthimit: " + data_e_kthimit);
                            }
                            System.out.println("Numri i pasagjereve: " + nrpasagjereve);
                            System.out.println("Cmimi total i biletes: $" + cmimiBiletes);

                            //Mesazhi konfigurimit
                            System.out.println("\nRezervimi u krye me suksese! Faleminderit qe zgjodhet Albawings.");
                        } else {
                            System.out.println("Ne nuk ofrojme fluturime ne kete date.");
                        }
                    } else {
                        System.out.println("Ne nuk e ofrojme kete flturim.");
                        // Kërko përdoruesin të vendosë përsëri destinacionin
                        aerprotidestinacionit = zgjidhAeroport("Zgjidh ID e aeroportit destinacion (vendos  ID): ");
                    }
                } else {
                    System.out.println("Te dhena jane te pasakta.");
                }
            } else {
                System.out.println("Nuk egziston llogari me kete email.");
            }
        }
        if (zgjedhja == 2) {
        

            // Vendosen te dhenat per sign-upin
            System.out.print("Vendos email per tu rregjistruar: ");
            String email = scanner.nextLine();

            // Kontrollon nese emaili i vendosur egziston
            if (egzEmaili(email)) {
                System.out.println("Egziston nje llogari me kete email.Ju lutem logo-uni me te dhenat e sakta.");

                // I kerkon userit te logohet
                System.out.print("Vendos emailin: ");
                String loginEmail = scanner.nextLine();
                System.out.print("Vendos paswordin: ");
                String loginPassword = merrInputinUserit("", true);

                // Kontrollon te dhenat e vendosura
                if (LoginOK(loginEmail, loginPassword)) {
                    System.out.println("Jeni loguar me suksese!");
                }
                // Shfaqe aeroportet dhe zgjedh nisjen dhe destinacionin
                int aeroprtinisjes = zgjidhAeroport("Zgjidhni id e aeroportit te nisjes (vendos  ID): ");
                int aerprotidestinacionit = zgjidhAeroport("Zgjidhni id e aeroportit te destinacionit(vendos ID): ");

                // Kontrollon nese fluturimi egziston
                if (IDBaraz(aeroprtinisjes, aerprotidestinacionit)) {
                    System.out.println("Vendi i nisjes dhe i mberritjes nuk mund te jene njesoj.");
                    // Kerkon perdoruesit te vendos vendin e mberritjes
                    aerprotidestinacionit = zgjidhAeroport("Zgjidhni id e aeroportit te destinacionit(vendos ID): ");
                }
                if (egzFluturim(aeroprtinisjes, aerprotidestinacionit)) {
                    // Shfaqe datat
                    shfaqDatatFluturime(aeroprtinisjes, aerprotidestinacionit);

                    // Zgjedh datat
                    System.out.print("Vendosni daten qe deshironi fluturimin (YYYY-MM-DD): ");
                    String data_E_zgjedhur = scanner.nextLine();

                    // Kontrollon daten nese eshte ok
                    if (eshtDataOK(aeroprtinisjes, aerprotidestinacionit, data_E_zgjedhur)) {

                        // Merr nga useri te dhenat e udhetimit
                        // Marrja e hyrjes së përdoruesit për detajet e biletës
                        boolean eshteEkonomik = false;
                        boolean isValidEshteEkonomik = false;
                        do {
                            System.out.print("Udhetimi juaj eshte i klasit ekonomike: Pergjigjuni (true ose false): ");
                            try {
                                eshteEkonomik = scanner.nextBoolean();
                                scanner.nextLine(); 
                                isValidEshteEkonomik = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Ju lutem vendosni true ose false.");
                                scanner.nextLine();
                            }
                        } while (!isValidEshteEkonomik);

                        boolean kaValixhe = false;
                        boolean isValidKaValixhe = false;
                        do {
                            System.out.print("A keni valixhe? Pergjigjuni (true ose false): ");
                            try {
                                kaValixhe = scanner.nextBoolean();
                                scanner.nextLine(); 
                                isValidKaValixhe = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Ju lutem vendosni true ose false.");
                                scanner.nextLine(); 
                            }
                        } while (!isValidKaValixhe);

                        int nrpasagjereve = 1;
                        boolean validInt;
                        do {
                            System.out.print("Vendosni numrin e pasagjereve: ");
                            try {
                                nrpasagjereve = scanner.nextInt();
                                scanner.nextLine(); 
                                validInt = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Ju lutem vendosni numer te plote.");
                                scanner.nextLine();
                                validInt = false;
                            }
                        } while (!validInt);

                        String emri;
                        do {
                            System.out.print("Vendosni emrin tuaj: ");
                            emri = scanner.nextLine();
                        } while (!emri.matches("[a-zA-Z\\s]+"));

                        String mbiemri;
                        do {
                            System.out.print("Vendosni mbiemrin tuaj: ");
                            mbiemri = scanner.nextLine();
                        } while (!mbiemri.matches("[a-zA-Z\\s]+"));

                        String gjini;
                        do {
                            System.out.print("Vendosni gjinin tuaj (Mashkull/Femer): ");
                            gjini = scanner.nextLine();
                        } while (!gjini.equalsIgnoreCase("Mashkull") && !gjini.equalsIgnoreCase("Femer"));

                        String nrTel;
                        do {
                            System.out.print("Vendosni numrin e telefonit: ");
                            nrTel = scanner.nextLine();
                        } while (!nrTel.matches("\\d+") || nrTel.length() < 8 || nrTel.length() > 15);

                        boolean eshteVjatje = false;
                        boolean isValideshteVajtje = false;
                        do {
                            System.out.print("Udhetimi eshte vetem vajtje? Pergjigjuni (true ose false): ");
                            try {
                                eshteVjatje = scanner.nextBoolean();
                                scanner.nextLine(); 
                                isValideshteVajtje = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Ju lutem vendosni true ose false.");
                                scanner.nextLine(); 
                            }
                        } while (!isValideshteVajtje); 
                        // Llogarit cmimin
                        double cmimiBiletes = llogaritCmimBilete(nrpasagjereve, aeroprtinisjes, aerprotidestinacionit, kaValixhe, eshteEkonomik, eshteVjatje);

                        String data_e_kthimit = null;

                        if (!eshteVjatje) {
                            // Nese udhetimi eshte vajtje ardhje shfaqen datat e kthimit
                            shfaqDatatFluturime(aerprotidestinacionit, aeroprtinisjes);

                            // Zgjedh daten e kthimit
                            boolean validdata_e_kthimit = false;
                            do {
                                data_e_kthimit = dataZgjedhur("Vendosni daten e kthimit qe deshironi (YYYY-MM-DD): ");
                                validdata_e_kthimit = krahasoData(data_E_zgjedhur, data_e_kthimit);

                                if (!validdata_e_kthimit) {
                                    System.out.println("Data e kthimit duhet te jete pas dates se nisjes");
                                }
                            } while (!validdata_e_kthimit);
                        }

                        // Rezervo fluturimin
                        rezervo(aeroprtinisjes, aerprotidestinacionit, data_E_zgjedhur, nrpasagjereve, eshteEkonomik, data_e_kthimit);

                        // Informacionet e rezervimit
                        System.out.println("\nInformacioni i rezervimit:");
                        System.out.println("Emri: " + emri + " " + mbiemri);
                        System.out.println("Gjinia: " + gjini);
                        System.out.println("Numri i telefonit: " + nrTel);
                        System.out.println("Data e nisjes: " + data_E_zgjedhur);
                        if (!eshteVjatje) {
                            System.out.println("Data e kthimit: " + data_e_kthimit);
                        }
                        System.out.println("Numri i pasagjereve: " + nrpasagjereve);
                        System.out.println("Cmimi total i biletes: $" + cmimiBiletes);

                        // Meszahi konfigurimi
                        System.out.println("\nRezervimi u krye me suksese! Faleminderit qe zgjodhet Albawings.");
                    } else {
                        System.out.println("Ne nuk ofrojme fluturime ne kete date.");
                    }
                } else {
                    System.out.println("Ne nuk e ofrojme kete flturim.");
                    // Kerkoj userit te vendosi vendin e destinacionit perseri
                    aerprotidestinacionit = zgjidhAeroport("Zgjidh ID e aeroportit destinacion (vendos  ID): ");
                }
            } else {
                System.out.println("Te dhena jane te pasakta.");
            }
        } 
    }

}